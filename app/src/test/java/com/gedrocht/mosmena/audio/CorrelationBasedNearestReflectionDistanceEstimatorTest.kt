package com.gedrocht.mosmena.audio

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for [CorrelationBasedNearestReflectionDistanceEstimator].
 */
class CorrelationBasedNearestReflectionDistanceEstimatorTest {

  private val acousticPulseConfiguration = AcousticPulseConfiguration(
    prePlaybackCaptureDurationInMilliseconds = 10,
    minimumExpectedDistanceInMeters = 0.10,
    maximumExpectedDistanceInMeters = 1.50
  )

  @Test
  fun estimateNearestReflectionDistance_returnsExpectedDistanceForSyntheticSignal() {
    val generatedPulseSignal = HighFrequencyPulseGenerator().generatePulseSignal(
      acousticPulseConfiguration = acousticPulseConfiguration
    )
    val expectedDistanceInMeters = 0.52
    val directCouplingStartSampleIndex = 500
    val reflectionDelayInSamples = (
      expectedDistanceInMeters * 2.0 /
        acousticPulseConfiguration.speedOfSoundInMetersPerSecond *
        acousticPulseConfiguration.sampleRateInHertz
      ).toInt()
    val reflectionStartSampleIndex = directCouplingStartSampleIndex + reflectionDelayInSamples

    val recordedSamples = FloatArray(6_000)
    injectPulse(
      destinationSamples = recordedSamples,
      pulseSamples = generatedPulseSignal.floatingPointSamples,
      startSampleIndex = directCouplingStartSampleIndex,
      amplitudeScale = 1.0f
    )
    injectPulse(
      destinationSamples = recordedSamples,
      pulseSamples = generatedPulseSignal.floatingPointSamples,
      startSampleIndex = reflectionStartSampleIndex,
      amplitudeScale = 0.65f
    )

    val distanceMeasurement = CorrelationBasedNearestReflectionDistanceEstimator()
      .estimateNearestReflectionDistance(
        emittedPulseSamples = generatedPulseSignal.floatingPointSamples,
        recordedSamples = recordedSamples,
        acousticPulseConfiguration = acousticPulseConfiguration
      )

    assertThat(distanceMeasurement.measuredDistanceInMeters).isWithin(0.03).of(expectedDistanceInMeters)
    assertThat(distanceMeasurement.measurementConfidence).isGreaterThan(0.50)
  }

  @Test(expected = IllegalStateException::class)
  fun estimateNearestReflectionDistance_throwsWhenNoReflectionExists() {
    val generatedPulseSignal = HighFrequencyPulseGenerator().generatePulseSignal(
      acousticPulseConfiguration = acousticPulseConfiguration
    )
    val recordedSamples = FloatArray(4_000)

    injectPulse(
      destinationSamples = recordedSamples,
      pulseSamples = generatedPulseSignal.floatingPointSamples,
      startSampleIndex = 500,
      amplitudeScale = 1.0f
    )

    CorrelationBasedNearestReflectionDistanceEstimator().estimateNearestReflectionDistance(
      emittedPulseSamples = generatedPulseSignal.floatingPointSamples,
      recordedSamples = recordedSamples,
      acousticPulseConfiguration = acousticPulseConfiguration
    )
  }

  /**
   * Copies a pulse into a larger recording buffer.
   */
  private fun injectPulse(
    destinationSamples: FloatArray,
    pulseSamples: FloatArray,
    startSampleIndex: Int,
    amplitudeScale: Float
  ) {
    pulseSamples.indices.forEach { pulseSampleIndex ->
      destinationSamples[startSampleIndex + pulseSampleIndex] +=
        pulseSamples[pulseSampleIndex] * amplitudeScale
    }
  }
}
