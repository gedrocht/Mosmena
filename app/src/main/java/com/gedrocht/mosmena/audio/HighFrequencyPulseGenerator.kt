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
        acousticPulseConfiguration.pulseDurationInMilliseconds / 1_000.0
      ).roundToInt()

    val floatingPointSamples = FloatArray(pulseSampleCount)
    val pulseCodeModulationSamples = ShortArray(pulseSampleCount)
    var accumulatedPhaseInRadians = 0.0

    for (sampleIndex in 0 until pulseSampleCount) {
      val normalizedProgress = sampleIndex.toDouble() / (pulseSampleCount - 1).coerceAtLeast(1)

      // The chirp gradually changes frequency over time.
      val instantaneousFrequencyInHertz =
        acousticPulseConfiguration.pulseStartFrequencyInHertz +
          (
            acousticPulseConfiguration.pulseEndFrequencyInHertz -
              acousticPulseConfiguration.pulseStartFrequencyInHertz
            ) * normalizedProgress

      // This window smoothly fades in and fades out the pulse.
      val hannWindowAmplitude = 0.5 - 0.5 * cos(2.0 * PI * normalizedProgress)

      // Phase controls the current position on the sine wave.
      accumulatedPhaseInRadians +=
        2.0 * PI * instantaneousFrequencyInHertz / acousticPulseConfiguration.sampleRateInHertz

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
}
