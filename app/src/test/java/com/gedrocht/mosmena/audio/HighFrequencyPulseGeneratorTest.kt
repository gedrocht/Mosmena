package com.gedrocht.mosmena.audio

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.math.abs

/**
 * Unit tests for [HighFrequencyPulseGenerator].
 */
class HighFrequencyPulseGeneratorTest {

  @Test
  fun generatePulseSignal_createsExpectedNumberOfSamples() {
    val acousticPulseConfiguration = AcousticPulseConfiguration(
      sampleRateInHertz = 48_000,
      pulseDurationInMilliseconds = 25
    )

    val generatedPulseSignal = HighFrequencyPulseGenerator().generatePulseSignal(
      acousticPulseConfiguration = acousticPulseConfiguration
    )

    assertThat(generatedPulseSignal.floatingPointSamples.size).isEqualTo(1_200)
    assertThat(generatedPulseSignal.sixteenBitPulseCodeModulationSamples.size).isEqualTo(1_200)
  }

  @Test
  fun generatePulseSignal_respectsAmplitudeWindowing() {
    val acousticPulseConfiguration = AcousticPulseConfiguration(pulseAmplitude = 0.40)

    val generatedPulseSignal = HighFrequencyPulseGenerator().generatePulseSignal(
      acousticPulseConfiguration = acousticPulseConfiguration
    )

    val maximumAbsoluteAmplitude = generatedPulseSignal.floatingPointSamples.maxOf { sampleValue ->
      abs(sampleValue)
    }

    assertThat(maximumAbsoluteAmplitude).isAtMost(0.4001f)
    assertThat(generatedPulseSignal.floatingPointSamples.first()).isWithin(0.0001f).of(0.0f)
    assertThat(generatedPulseSignal.floatingPointSamples.last()).isWithin(0.0001f).of(0.0f)
  }
}
