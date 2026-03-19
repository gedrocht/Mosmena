package com.gedrocht.mosmena.audio

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Generates a short high-frequency chirp.
 *
 * The pulse sweeps from one frequency to another while a Hann window tapers
 * the start and end. The window is important because abrupt edges would add
 * extra frequencies and make the correlation peak less clean.
 */
class HighFrequencyPulseGenerator {

  /**
   * Builds a pulse in both floating-point and sixteen-bit playback form.
   *
   * Example:
   * ```
   * val pulseSignal = HighFrequencyPulseGenerator().generatePulseSignal(
   *   AcousticPulseConfiguration()
   * )
   * ```
   */
  fun generatePulseSignal(acousticPulseConfiguration: AcousticPulseConfiguration): GeneratedPulseSignal {
    val pulseSampleCount = (
      acousticPulseConfiguration.sampleRateInHertz *
        acousticPulseConfiguration.pulseDurationInMilliseconds / MILLISECONDS_PER_SECOND
      ).roundToInt()

    val floatingPointSamples = FloatArray(pulseSampleCount)
    val pulseCodeModulationSamples = ShortArray(pulseSampleCount)
    var accumulatedPhaseInRadians = 0.0

    for (sampleIndex in 0 until pulseSampleCount) {
      val normalizedProgress =
        sampleIndex.toDouble() / (pulseSampleCount - FINAL_SAMPLE_INDEX_OFFSET).coerceAtLeast(
          MINIMUM_PROGRESS_DENOMINATOR
        )

      // The chirp gradually changes frequency over time.
      val instantaneousFrequencyInHertz =
        acousticPulseConfiguration.pulseStartFrequencyInHertz +
          (
            acousticPulseConfiguration.pulseEndFrequencyInHertz -
              acousticPulseConfiguration.pulseStartFrequencyInHertz
            ) * normalizedProgress

      // This window smoothly fades in and fades out the pulse.
      val hannWindowAmplitude =
        HALF_AMPLITUDE - HALF_AMPLITUDE * cos(FULL_CIRCLE_IN_RADIANS * normalizedProgress)

      // Phase controls the current position on the sine wave.
      accumulatedPhaseInRadians +=
        FULL_CIRCLE_IN_RADIANS * instantaneousFrequencyInHertz /
          acousticPulseConfiguration.sampleRateInHertz

      val floatingPointSample =
        (
          sin(accumulatedPhaseInRadians) *
            hannWindowAmplitude *
            acousticPulseConfiguration.pulseAmplitude
          ).toFloat()

      floatingPointSamples[sampleIndex] = floatingPointSample
      pulseCodeModulationSamples[sampleIndex] =
        (floatingPointSample * Short.MAX_VALUE).roundToInt().toShort()
    }

    return GeneratedPulseSignal(
      floatingPointSamples = floatingPointSamples,
      sixteenBitPulseCodeModulationSamples = pulseCodeModulationSamples
    )
  }

  private companion object {
    private const val FINAL_SAMPLE_INDEX_OFFSET = 1
    private const val FULL_CIRCLE_IN_RADIANS = 2.0 * PI
    private const val HALF_AMPLITUDE = 0.5
    private const val MILLISECONDS_PER_SECOND = 1_000.0
    private const val MINIMUM_PROGRESS_DENOMINATOR = 1
  }
}
