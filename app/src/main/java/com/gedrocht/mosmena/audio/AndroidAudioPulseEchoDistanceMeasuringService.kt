package com.gedrocht.mosmena.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.SystemClock
import com.gedrocht.mosmena.logging.ApplicationLogRecorder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Android implementation that drives the speaker and microphone.
 */
class AndroidAudioPulseEchoDistanceMeasuringService(
  private val applicationLogRecorder: ApplicationLogRecorder,
  private val highFrequencyPulseGenerator: HighFrequencyPulseGenerator,
  private val nearestReflectionDistanceEstimator: CorrelationBasedNearestReflectionDistanceEstimator,
  private val inputOutputCoroutineDispatcher: CoroutineDispatcher
) : AcousticReflectionDistanceMeasuringService {

  /**
   * Runs one full measurement cycle on a background dispatcher.
   */
  override suspend fun measureNearestReflectionDistance(
    acousticPulseConfiguration: AcousticPulseConfiguration
  ): DistanceMeasurement = withContext(inputOutputCoroutineDispatcher) {
    val effectiveAcousticPulseConfiguration = acousticPulseConfiguration
    val generatedPulseSignal = highFrequencyPulseGenerator.generatePulseSignal(
      acousticPulseConfiguration = effectiveAcousticPulseConfiguration
    )

    applicationLogRecorder.recordInformationMessage(
      tag = TAG,
      message =
        "Starting measurement with ${effectiveAcousticPulseConfiguration.sampleRateInHertz} Hz " +
          "sampling and a ${effectiveAcousticPulseConfiguration.pulseDurationInMilliseconds} ms pulse."
    )

    val recordingSampleCount = (
      effectiveAcousticPulseConfiguration.sampleRateInHertz *
        effectiveAcousticPulseConfiguration.totalRecordingDurationInMilliseconds / 1_000.0
      ).roundToInt()

    val recordingBufferSizeInBytes = determineRecordingBufferSizeInBytes(
      acousticPulseConfiguration = effectiveAcousticPulseConfiguration,
      recordingSampleCount = recordingSampleCount
    )

    val audioRecord = createAudioRecord(
      acousticPulseConfiguration = effectiveAcousticPulseConfiguration,
      recordingBufferSizeInBytes = recordingBufferSizeInBytes
    )

    val audioTrack = createAudioTrack(
      acousticPulseConfiguration = effectiveAcousticPulseConfiguration,
      generatedPulseSignal = generatedPulseSignal
    )

    val recordedPulseCodeModulationSamples = ShortArray(recordingSampleCount)
    val measurementStartTimeInNanoseconds = SystemClock.elapsedRealtimeNanos()

    try {
      audioRecord.startRecording()
      delay(effectiveAcousticPulseConfiguration.prePlaybackCaptureDurationInMilliseconds.toLong())

      audioTrack.play()

      var totalRecordedSampleCount = 0
      while (totalRecordedSampleCount < recordingSampleCount) {
        val numberOfSamplesRead = audioRecord.read(
          recordedPulseCodeModulationSamples,
          totalRecordedSampleCount,
          recordingSampleCount - totalRecordedSampleCount,
          AudioRecord.READ_BLOCKING
        )

        if (numberOfSamplesRead <= 0) {
          throw IllegalStateException(
            "AudioRecord returned an invalid sample count: $numberOfSamplesRead"
          )
        }

        totalRecordedSampleCount += numberOfSamplesRead
      }

      audioTrack.stop()
      audioRecord.stop()

      val recordedFloatingPointSamples = recordedPulseCodeModulationSamples.map { sampleValue ->
        sampleValue / Short.MAX_VALUE.toFloat()
      }.toFloatArray()

      val distanceMeasurement = nearestReflectionDistanceEstimator.estimateNearestReflectionDistance(
        emittedPulseSamples = generatedPulseSignal.floatingPointSamples,
        recordedSamples = recordedFloatingPointSamples,
        acousticPulseConfiguration = effectiveAcousticPulseConfiguration
      )

      val measurementDurationInMilliseconds =
        (SystemClock.elapsedRealtimeNanos() - measurementStartTimeInNanoseconds) / 1_000_000.0

      applicationLogRecorder.recordInformationMessage(
        tag = TAG,
        message =
          "Measurement completed in %.2f ms with distance %.2f m and confidence %.2f.".format(
            Locale.US,
            measurementDurationInMilliseconds,
            distanceMeasurement.measuredDistanceInMeters,
            distanceMeasurement.measurementConfidence
          )
      )

      distanceMeasurement
    } catch (exception: Exception) {
      applicationLogRecorder.recordErrorMessage(
        tag = TAG,
        message = "Measurement failed because the audio pipeline or estimator rejected the result.",
        throwable = exception
      )
      throw exception
    } finally {
      audioTrack.release()
      audioRecord.release()
    }
  }

  /**
   * Uses the platform-reported minimum size, but never allocates less than the
   * number of samples we intend to collect.
   */
  private fun determineRecordingBufferSizeInBytes(
    acousticPulseConfiguration: AcousticPulseConfiguration,
    recordingSampleCount: Int
  ): Int {
    val platformMinimumBufferSizeInBytes = AudioRecord.getMinBufferSize(
      acousticPulseConfiguration.sampleRateInHertz,
      AudioFormat.CHANNEL_IN_MONO,
      AudioFormat.ENCODING_PCM_16BIT
    )

    if (platformMinimumBufferSizeInBytes <= 0) {
      throw IllegalStateException(
        "AudioRecord could not provide a valid minimum buffer size."
      )
    }

    val requestedBufferSizeInBytes = recordingSampleCount * Short.SIZE_BYTES
    return max(platformMinimumBufferSizeInBytes, requestedBufferSizeInBytes)
  }

  /**
   * Creates the microphone capture object.
   */
  private fun createAudioRecord(
    acousticPulseConfiguration: AcousticPulseConfiguration,
    recordingBufferSizeInBytes: Int
  ): AudioRecord {
    val audioRecord = AudioRecord.Builder()
      .setAudioSource(MediaRecorder.AudioSource.DEFAULT)
      .setAudioFormat(
        AudioFormat.Builder()
          .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
          .setSampleRate(acousticPulseConfiguration.sampleRateInHertz)
          .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
          .build()
      )
      .setBufferSizeInBytes(recordingBufferSizeInBytes)
      .build()

    check(audioRecord.state == AudioRecord.STATE_INITIALIZED) {
      "AudioRecord failed to initialize. Verify microphone permission and hardware support."
    }

    return audioRecord
  }

  /**
   * Creates the speaker playback object and preloads the generated pulse.
   */
  private fun createAudioTrack(
    acousticPulseConfiguration: AcousticPulseConfiguration,
    generatedPulseSignal: GeneratedPulseSignal
  ): AudioTrack {
    val audioTrack = AudioTrack.Builder()
      .setAudioAttributes(
        AudioAttributes.Builder()
          .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
          .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
          .build()
      )
      .setAudioFormat(
        AudioFormat.Builder()
          .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
          .setSampleRate(acousticPulseConfiguration.sampleRateInHertz)
          .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
          .build()
      )
      .setTransferMode(AudioTrack.MODE_STATIC)
      .setBufferSizeInBytes(
        generatedPulseSignal.sixteenBitPulseCodeModulationSamples.size * Short.SIZE_BYTES
      )
      .build()

    check(audioTrack.state == AudioTrack.STATE_INITIALIZED) {
      "AudioTrack failed to initialize. Verify speaker playback support."
    }

    val numberOfSamplesWritten = audioTrack.write(
      generatedPulseSignal.sixteenBitPulseCodeModulationSamples,
      0,
      generatedPulseSignal.sixteenBitPulseCodeModulationSamples.size
    )

    check(numberOfSamplesWritten == generatedPulseSignal.sixteenBitPulseCodeModulationSamples.size) {
      "AudioTrack did not accept the full pulse buffer."
    }

    return audioTrack
  }

  companion object {
    private const val TAG = "AndroidAudioPulseEchoDistanceMeasuringService"
  }
}
