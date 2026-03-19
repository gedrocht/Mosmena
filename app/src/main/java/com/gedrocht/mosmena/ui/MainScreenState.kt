package com.gedrocht.mosmena.ui

import com.gedrocht.mosmena.audio.DistanceMeasurement

/**
 * Immutable state snapshot for the main screen.
 *
 * @property hasMicrophonePermission Whether the app currently has microphone access.
 * @property isMeasurementInProgress Whether a measurement is currently running.
 * @property statusMessage Short status line shown near the top of the screen.
 * @property measurementSummary Longer beginner-friendly explanation of the last result.
 * @property latestDistanceMeasurement The latest successful measurement, if any.
 */
data class MainScreenState(
  val hasMicrophonePermission: Boolean = false,
  val isMeasurementInProgress: Boolean = false,
  val statusMessage: String = "Microphone access is required before any measurement can start.",
  val measurementSummary: String = "No measurement has been recorded yet.",
  val latestDistanceMeasurement: DistanceMeasurement? = null
)
