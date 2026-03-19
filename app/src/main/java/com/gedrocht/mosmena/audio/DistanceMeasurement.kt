package com.gedrocht.mosmena.audio

/**
 * Represents a single successful distance measurement.
 *
 * @property measuredDistanceInMeters Estimated distance from the phone to the
 * nearest reflecting surface.
 * @property measurementConfidence Zero-to-one heuristic indicating how cleanly
 * the reflection peak stood out from background noise.
 * @property directCouplingPeakSampleIndex Sample position of the earliest
 * speaker-to-microphone coupling peak.
 * @property nearestReflectionPeakSampleIndex Sample position of the nearest
 * reflection peak.
 * @property correlationStrength Absolute correlation value of the reflection
 * peak, used for logging and diagnostics.
 * @property humanReadableExplanation Beginner-friendly summary of the result.
 */
data class DistanceMeasurement(
  val measuredDistanceInMeters: Double,
  val measurementConfidence: Double,
  val directCouplingPeakSampleIndex: Int,
  val nearestReflectionPeakSampleIndex: Int,
  val correlationStrength: Double,
  val humanReadableExplanation: String
)
