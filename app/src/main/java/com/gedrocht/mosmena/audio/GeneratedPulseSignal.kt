package com.gedrocht.mosmena.audio

/**
 * Holds the same pulse in two formats:
 *
 * - floating-point samples for signal processing
 * - sixteen-bit pulse-code-modulation samples for Android audio playback
 *
 * @property floatingPointSamples The pulse as normalized values in the range
 * roughly between -1.0 and 1.0.
 * @property sixteenBitPulseCodeModulationSamples The same pulse converted into
 * the format required by Android's sixteen-bit audio APIs.
 */
data class GeneratedPulseSignal(
  val floatingPointSamples: FloatArray,
  val sixteenBitPulseCodeModulationSamples: ShortArray
)
