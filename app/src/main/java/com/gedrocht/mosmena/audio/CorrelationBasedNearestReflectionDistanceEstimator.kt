package com.gedrocht.mosmena.audio

import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

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
    val minimumPeakHeight = maximumAbsoluteCorrelation * MINIMUM_PEAK_HEIGHT_RATIO
    val searchStartSampleIndex = (
      acousticPulseConfiguration.prePlaybackCaptureDurationInMilliseconds *
        acousticPulseConfiguration.sampleRateInHertz / MILLISECONDS_PER_SECOND
      ).toInt()

    val minimumReflectionDelayInSamples = max(
      MINIMUM_DELAY_SAMPLE_COUNT,
      convertDistanceInMetersToRoundTripDelayInSamples(
        distanceInMeters = acousticPulseConfiguration.minimumExpectedDistanceInMeters,
        acousticPulseConfiguration = acousticPulseConfiguration
      )
    )
    val maximumReflectionDelayInSamples = max(
      minimumReflectionDelayInSamples,
      convertDistanceInMetersToRoundTripDelayInSamples(
        distanceInMeters = acousticPulseConfiguration.maximumExpectedDistanceInMeters,
        acousticPulseConfiguration = acousticPulseConfiguration
      )
    )

    val detectedPeaks = collapseNearbyPeaks(
      peakSampleIndices = detectPeakSampleIndices(
        correlationValues = correlationValues,
        minimumPeakHeight = minimumPeakHeight
      ).filter { peakSampleIndex -> peakSampleIndex >= searchStartSampleIndex },
      correlationValues = correlationValues,
      maximumGapInSamples = calculatePeakGroupingGapInSamples(acousticPulseConfiguration)
    )

    val directCouplingPeakSampleIndex = checkNotNull(detectedPeaks.firstOrNull()) {
      "The algorithm could not find the direct speaker-to-microphone coupling peak."
    }
    val nearestReflectionPeakSampleIndex = checkNotNull(
      detectedPeaks.firstOrNull { peakSampleIndex ->
        val sampleDelayAfterDirectCoupling = peakSampleIndex - directCouplingPeakSampleIndex
        sampleDelayAfterDirectCoupling in minimumReflectionDelayInSamples..maximumReflectionDelayInSamples
      }
    ) {
      "No reflection peak was found inside the expected distance window."
    }

    val sampleDelayBetweenDirectCouplingAndReflection =
      nearestReflectionPeakSampleIndex - directCouplingPeakSampleIndex
    val roundTripTravelTimeInSeconds =
      sampleDelayBetweenDirectCouplingAndReflection.toDouble() /
        acousticPulseConfiguration.sampleRateInHertz
    val measuredDistanceInMeters = roundTripTravelTimeInSeconds *
      acousticPulseConfiguration.speedOfSoundInMetersPerSecond / ROUND_TRIP_DISTANCE_MULTIPLIER

    val reflectionCorrelationStrength = correlationValues[nearestReflectionPeakSampleIndex].absoluteValue
    val measurementConfidence = min(
      MAXIMUM_MEASUREMENT_CONFIDENCE,
      reflectionCorrelationStrength / max(
        maximumAbsoluteCorrelation,
        MINIMUM_CONFIDENCE_DENOMINATOR
      )
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

      correlationValues[correlationIndex] = numerator / sqrt(
        (emittedPulseEnergy * recordedWindowEnergy).coerceAtLeast(MINIMUM_NORMALIZATION_ENERGY)
      )
    }

    return correlationValues
  }

  /**
   * Converts a physical round-trip distance into the equivalent sample delay.
   */
  private fun convertDistanceInMetersToRoundTripDelayInSamples(
    distanceInMeters: Double,
    acousticPulseConfiguration: AcousticPulseConfiguration
  ): Int {
    return (
      (
        distanceInMeters * ROUND_TRIP_DISTANCE_MULTIPLIER /
          acousticPulseConfiguration.speedOfSoundInMetersPerSecond
        ) * acousticPulseConfiguration.sampleRateInHertz
      ).toInt()
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

  private fun collapseNearbyPeaks(
    peakSampleIndices: List<Int>,
    correlationValues: DoubleArray,
    maximumGapInSamples: Int
  ): List<Int> {
    if (peakSampleIndices.isEmpty()) {
      return emptyList()
    }

    val collapsedPeakSampleIndices = mutableListOf<Int>()
    var currentPeakCluster = mutableListOf(peakSampleIndices.first())

    for (peakSampleIndex in peakSampleIndices.drop(1)) {
      val previousPeakSampleIndex = currentPeakCluster.last()
      if (peakSampleIndex - previousPeakSampleIndex <= maximumGapInSamples) {
        currentPeakCluster += peakSampleIndex
        continue
      }

      collapsedPeakSampleIndices += selectStrongestPeakSampleIndex(
        peakSampleIndices = currentPeakCluster,
        correlationValues = correlationValues
      )
      currentPeakCluster = mutableListOf(peakSampleIndex)
    }

    collapsedPeakSampleIndices += selectStrongestPeakSampleIndex(
      peakSampleIndices = currentPeakCluster,
      correlationValues = correlationValues
    )
    return collapsedPeakSampleIndices
  }

  private fun selectStrongestPeakSampleIndex(
    peakSampleIndices: List<Int>,
    correlationValues: DoubleArray
  ): Int {
    return peakSampleIndices.maxBy { peakSampleIndex ->
      correlationValues[peakSampleIndex].absoluteValue
    }
  }

  private fun calculatePeakGroupingGapInSamples(
    acousticPulseConfiguration: AcousticPulseConfiguration
  ): Int {
    val pulseBandwidthInHertz = (
      acousticPulseConfiguration.pulseEndFrequencyInHertz -
        acousticPulseConfiguration.pulseStartFrequencyInHertz
      ).absoluteValue.coerceAtLeast(MINIMUM_PULSE_BANDWIDTH_IN_HERTZ)

    return max(
      MINIMUM_PEAK_GROUPING_GAP_IN_SAMPLES,
      (acousticPulseConfiguration.sampleRateInHertz / pulseBandwidthInHertz).toInt()
    )
  }

  private companion object {
    private const val MAXIMUM_MEASUREMENT_CONFIDENCE = 1.0
    private const val MILLISECONDS_PER_SECOND = 1_000.0
    private const val MINIMUM_CONFIDENCE_DENOMINATOR = 0.0001
    private const val MINIMUM_DELAY_SAMPLE_COUNT = 1
    private const val MINIMUM_NORMALIZATION_ENERGY = 0.0000001
    private const val MINIMUM_PEAK_HEIGHT_RATIO = 0.45
    private const val MINIMUM_PEAK_GROUPING_GAP_IN_SAMPLES = 1
    private const val MINIMUM_PULSE_BANDWIDTH_IN_HERTZ = 1.0
    private const val ROUND_TRIP_DISTANCE_MULTIPLIER = 2.0
  }
}
