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
    val generatedPulseSignal = highFrequencyPulseGenerator.generatePulseSignal(
      acousticPulseConfiguration = acousticPulseConfiguration
    )
    logMeasurementStart(acousticPulseConfiguration)

    val recordingSampleCount = determineRecordingSampleCount(acousticPulseConfiguration)
    val recordingBufferSizeInBytes = determineRecordingBufferSizeInBytes(
      acousticPulseConfiguration = acousticPulseConfiguration,
      recordingSampleCount = recordingSampleCount
    )
    val audioRecord = createAudioRecord(
      acousticPulseConfiguration = acousticPulseConfiguration,
      recordingBufferSizeInBytes = recordingBufferSizeInBytes
    )
    val audioTrack = createAudioTrack(
      acousticPulseConfiguration = acousticPulseConfiguration,
      generatedPulseSignal = generatedPulseSignal
    )
    val measurementStartTimeInNanoseconds = SystemClock.elapsedRealtimeNanos()

    try {
      val recordedPulseCodeModulationSamples = captureRecordedPulseCodeModulationSamples(
        audioRecord = audioRecord,
        audioTrack = audioTrack,
        recordingSampleCount = recordingSampleCount,
        acousticPulseConfiguration = acousticPulseConfiguration
      )
      val distanceMeasurement = nearestReflectionDistanceEstimator.estimateNearestReflectionDistance(
        emittedPulseSamples = generatedPulseSignal.floatingPointSamples,
        recordedSamples = convertRecordedSamplesToFloatingPoint(recordedPulseCodeModulationSamples),
        acousticPulseConfiguration = acousticPulseConfiguration
      )
      logMeasurementSuccess(
        measurementStartTimeInNanoseconds = measurementStartTimeInNanoseconds,
        distanceMeasurement = distanceMeasurement
      )
      distanceMeasurement
    } catch (exception: IllegalStateException) {
      logAndRethrowMeasurementFailure(exception)
    } catch (exception: SecurityException) {
      logAndRethrowMeasurementFailure(exception)
    } finally {
      audioTrack.release()
      audioRecord.release()
    }
  }

  /**
   * Converts the configured recording duration into a sample count.
   */
  private fun determineRecordingSampleCount(
    acousticPulseConfiguration: AcousticPulseConfiguration
  ): Int {
    return (
      acousticPulseConfiguration.sampleRateInHertz *
        acousticPulseConfiguration.totalRecordingDurationInMilliseconds / MILLISECONDS_PER_SECOND
      ).roundToInt()
  }

  /**
   * Writes a clear starting log entry so beginners can see what the service is
   * about to do.
   */
  private fun logMeasurementStart(acousticPulseConfiguration: AcousticPulseConfiguration) {
    applicationLogRecorder.recordInformationMessage(
      tag = TAG,
      message =
        "Starting measurement with ${acousticPulseConfiguration.sampleRateInHertz} Hz " +
          "sampling and a ${acousticPulseConfiguration.pulseDurationInMilliseconds} ms pulse."
    )
  }

  /**
   * Captures the microphone recording that contains both the direct coupling and
   * the reflected pulse.
   */
  private suspend fun captureRecordedPulseCodeModulationSamples(
    audioRecord: AudioRecord,
    audioTrack: AudioTrack,
    recordingSampleCount: Int,
    acousticPulseConfiguration: AcousticPulseConfiguration
  ): ShortArray {
    val recordedPulseCodeModulationSamples = ShortArray(recordingSampleCount)
    var hasStartedRecording = false
    var hasStartedPlayback = false

    try {
      audioRecord.startRecording()
      hasStartedRecording = true
      delay(acousticPulseConfiguration.prePlaybackCaptureDurationInMilliseconds.toLong())

      audioTrack.play()
      hasStartedPlayback = true

      var totalRecordedSampleCount = 0
      while (totalRecordedSampleCount < recordingSampleCount) {
        val numberOfSamplesRead = audioRecord.read(
          recordedPulseCodeModulationSamples,
          totalRecordedSampleCount,
          recordingSampleCount - totalRecordedSampleCount,
          AudioRecord.READ_BLOCKING
        )

        check(numberOfSamplesRead > 0) {
          "AudioRecord returned an invalid sample count: $numberOfSamplesRead"
        }

        totalRecordedSampleCount += numberOfSamplesRead
      }

      return recordedPulseCodeModulationSamples
    } finally {
      if (hasStartedPlayback) {
        audioTrack.stop()
      }
      if (hasStartedRecording) {
        audioRecord.stop()
      }
    }
  }

  /**
   * Converts sixteen-bit audio samples into normalized floating-point samples so
   * the correlation estimator can process them.
   */
  private fun convertRecordedSamplesToFloatingPoint(
    recordedPulseCodeModulationSamples: ShortArray
  ): FloatArray {
    return recordedPulseCodeModulationSamples.map { sampleValue ->
      sampleValue / Short.MAX_VALUE.toFloat()
    }.toFloatArray()
  }

  /**
   * Writes a success log entry with duration and result details.
   */
  private fun logMeasurementSuccess(
    measurementStartTimeInNanoseconds: Long,
    distanceMeasurement: DistanceMeasurement
  ) {
    val measurementDurationInMilliseconds =
      (SystemClock.elapsedRealtimeNanos() - measurementStartTimeInNanoseconds) /
        NANOSECONDS_PER_MILLISECOND

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
  }

  /**
   * Records a failure with the shared logging format and then rethrows it so the
   * caller still sees the original error.
   */
  private fun logAndRethrowMeasurementFailure(exception: Exception): Nothing {
    applicationLogRecorder.recordErrorMessage(
      tag = TAG,
      message = "Measurement failed because the audio pipeline or estimator rejected the result.",
      throwable = exception
    )
    throw exception
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

    check(platformMinimumBufferSizeInBytes > 0) {
      "AudioRecord could not provide a valid minimum buffer size."
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

  private companion object {
    private const val MILLISECONDS_PER_SECOND = 1_000.0
    private const val NANOSECONDS_PER_MILLISECOND = 1_000_000.0
    private const val TAG = "AndroidAudioPulseEchoDistanceMeasuringService"
  }
}
