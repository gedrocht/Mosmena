package com.gedrocht.mosmena.audio

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import java.util.Locale

/**
 * Uses normalized cross-correlation to find the nearest reflection.
 *
 * The basic idea is:
 * 1. Slide the known pulse over the recording.
 * 2. Measure how similar the recording is to the pulse at each position.
 * 3. Treat the first strong peak as direct speaker-to-microphone coupling.
 * 4. Treat the next strong peak as the nearest external reflection.
 * 5. Convert the extra time delay into distance.
 */
class CorrelationBasedNearestReflectionDistanceEstimator {

  /**
   * Estimates the distance of the nearest reflection.
   *
   * @return A successful measurement when a believable reflection is found.
   * @throws IllegalStateException when the recording is too short or when no
   * valid reflection peak can be identified.
   */
  fun estimateNearestReflectionDistance(
    emittedPulseSamples: FloatArray,
    recordedSamples: FloatArray,
    acousticPulseConfiguration: AcousticPulseConfiguration
  ): DistanceMeasurement {
    require(recordedSamples.size >= emittedPulseSamples.size) {
      "The recording must be at least as long as the emitted pulse."
    }

    val correlationValues = calculateNormalizedCorrelationValues(
      emittedPulseSamples = emittedPulseSamples,
      recordedSamples = recordedSamples
    )

    val maximumAbsoluteCorrelation = correlationValues.maxOf { it.absoluteValue }
    val minimumPeakHeight = maximumAbsoluteCorrelation * 0.45
    val searchStartSampleIndex =
      (
        acousticPulseConfiguration.prePlaybackCaptureDurationInMilliseconds *
          acousticPulseConfiguration.sampleRateInHertz / 1_000.0
        ).toInt()

    val minimumReflectionDelayInSamples = max(
      1,
      (
        (
          acousticPulseConfiguration.minimumExpectedDistanceInMeters * 2.0 /
            acousticPulseConfiguration.speedOfSoundInMetersPerSecond
          ) * acousticPulseConfiguration.sampleRateInHertz
        ).toInt()
    )

    val maximumReflectionDelayInSamples = max(
      minimumReflectionDelayInSamples,
      (
        (
          acousticPulseConfiguration.maximumExpectedDistanceInMeters * 2.0 /
            acousticPulseConfiguration.speedOfSoundInMetersPerSecond
          ) * acousticPulseConfiguration.sampleRateInHertz
        ).toInt()
    )

    val detectedPeaks = detectPeakSampleIndices(
      correlationValues = correlationValues,
      minimumPeakHeight = minimumPeakHeight
    ).filter { peakSampleIndex -> peakSampleIndex >= searchStartSampleIndex }

    val directCouplingPeakSampleIndex = detectedPeaks.firstOrNull()
      ?: throw IllegalStateException(
        "The algorithm could not find the direct speaker-to-microphone coupling peak."
      )

    val nearestReflectionPeakSampleIndex = detectedPeaks.firstOrNull { peakSampleIndex ->
      val sampleDelayAfterDirectCoupling = peakSampleIndex - directCouplingPeakSampleIndex
      sampleDelayAfterDirectCoupling in minimumReflectionDelayInSamples..maximumReflectionDelayInSamples
    } ?: throw IllegalStateException(
      "No reflection peak was found inside the expected distance window."
    )

    val sampleDelayBetweenDirectCouplingAndReflection =
      nearestReflectionPeakSampleIndex - directCouplingPeakSampleIndex

    val roundTripTravelTimeInSeconds =
      sampleDelayBetweenDirectCouplingAndReflection.toDouble() /
        acousticPulseConfiguration.sampleRateInHertz

    val measuredDistanceInMeters =
      roundTripTravelTimeInSeconds *
        acousticPulseConfiguration.speedOfSoundInMetersPerSecond / 2.0

    val reflectionCorrelationStrength = correlationValues[nearestReflectionPeakSampleIndex].absoluteValue
    val measurementConfidence =
      min(
        1.0,
        reflectionCorrelationStrength / max(maximumAbsoluteCorrelation, 0.0001)
      )

    return DistanceMeasurement(
      measuredDistanceInMeters = measuredDistanceInMeters,
      measurementConfidence = measurementConfidence,
      directCouplingPeakSampleIndex = directCouplingPeakSampleIndex,
      nearestReflectionPeakSampleIndex = nearestReflectionPeakSampleIndex,
      correlationStrength = reflectionCorrelationStrength,
      humanReadableExplanation =
        "The strongest early peak marks direct speaker-to-microphone coupling, " +
          "and the next believable peak appears ${sampleDelayBetweenDirectCouplingAndReflection} " +
          "samples later. That delay corresponds to about %.2f meters.".format(
            Locale.US,
            measuredDistanceInMeters
          )
    )
  }

  /**
   * Calculates normalized correlation values for every possible pulse position.
   */
  private fun calculateNormalizedCorrelationValues(
    emittedPulseSamples: FloatArray,
    recordedSamples: FloatArray
  ): DoubleArray {
    val correlationSampleCount = recordedSamples.size - emittedPulseSamples.size + 1
    val correlationValues = DoubleArray(correlationSampleCount)
    val emittedPulseEnergy = emittedPulseSamples.sumOf { sampleValue ->
      sampleValue.toDouble() * sampleValue
    }

    for (correlationIndex in 0 until correlationSampleCount) {
      var numerator = 0.0
      var recordedWindowEnergy = 0.0

      for (pulseSampleIndex in emittedPulseSamples.indices) {
        val recordedSampleValue = recordedSamples[correlationIndex + pulseSampleIndex].toDouble()
        val emittedSampleValue = emittedPulseSamples[pulseSampleIndex].toDouble()

        numerator += recordedSampleValue * emittedSampleValue
        recordedWindowEnergy += recordedSampleValue * recordedSampleValue
      }

      correlationValues[correlationIndex] =
        numerator / sqrt((emittedPulseEnergy * recordedWindowEnergy).coerceAtLeast(0.0000001))
    }

    return correlationValues
  }

  /**
   * Finds local maxima that exceed the supplied threshold.
   */
  private fun detectPeakSampleIndices(
    correlationValues: DoubleArray,
    minimumPeakHeight: Double
  ): List<Int> {
    val peakSampleIndices = mutableListOf<Int>()

    for (sampleIndex in 1 until correlationValues.lastIndex) {
      val previousValue = correlationValues[sampleIndex - 1].absoluteValue
      val currentValue = correlationValues[sampleIndex].absoluteValue
      val nextValue = correlationValues[sampleIndex + 1].absoluteValue

      if (
        currentValue >= minimumPeakHeight &&
        currentValue >= previousValue &&
        currentValue > nextValue
      ) {
        peakSampleIndices += sampleIndex
      }
    }

    return peakSampleIndices
  }
}
