package com.gedrocht.mosmena.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gedrocht.mosmena.audio.AcousticPulseConfiguration
import com.gedrocht.mosmena.audio.AcousticReflectionDistanceMeasuringService
import com.gedrocht.mosmena.audio.DistanceMeasurement
import com.gedrocht.mosmena.logging.ApplicationLogMessage
import com.gedrocht.mosmena.logging.ApplicationLogRecorder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Coordinates user actions and long-running measurement work.
 */
class MainScreenViewModel(
  private val acousticReflectionDistanceMeasuringService: AcousticReflectionDistanceMeasuringService,
  private val applicationLogRecorder: ApplicationLogRecorder,
  private val defaultAcousticPulseConfiguration: AcousticPulseConfiguration
) : ViewModel() {

  private val mutableScreenState = MutableStateFlow(MainScreenState())

  /**
   * The activity collects this flow to render the latest screen snapshot.
   */
  val screenState: StateFlow<MainScreenState> = mutableScreenState.asStateFlow()

  /**
   * The activity also collects log messages directly for the on-screen log list.
   */
  val visibleLogMessages: StateFlow<List<ApplicationLogMessage>> = applicationLogRecorder.recentLogMessages

  /**
   * Updates the stored permission state after the activity checks or requests
   * microphone permission.
   */
  fun onMicrophonePermissionStateChanged(hasMicrophonePermission: Boolean) {
    mutableScreenState.value = mutableScreenState.value.copy(
      hasMicrophonePermission = hasMicrophonePermission,
      statusMessage = if (hasMicrophonePermission) {
        "Ready to emit a pulse and measure the nearest reflection."
      } else {
        "Microphone permission has not been granted yet."
      }
    )
  }

  /**
   * Starts a single measurement unless one is already running.
   */
  fun startSingleMeasurement() {
    if (mutableScreenState.value.isMeasurementInProgress) {
      applicationLogRecorder.recordWarningMessage(
        tag = TAG,
        message = "Ignored a start request because a measurement is already running."
      )
      return
    }

    if (!mutableScreenState.value.hasMicrophonePermission) {
      mutableScreenState.value = mutableScreenState.value.copy(
        statusMessage = "Microphone permission must be granted before measuring."
      )
      return
    }

    viewModelScope.launch {
      mutableScreenState.value = mutableScreenState.value.copy(
        isMeasurementInProgress = true,
        statusMessage = "Listening for the direct pulse and the first reflection..."
      )

      try {
        val distanceMeasurement =
          acousticReflectionDistanceMeasuringService.measureNearestReflectionDistance(
            acousticPulseConfiguration = defaultAcousticPulseConfiguration
          )

        mutableScreenState.value = mutableScreenState.value.copy(
          isMeasurementInProgress = false,
          statusMessage = "Measurement complete.",
          measurementSummary = buildMeasurementSummary(distanceMeasurement),
          latestDistanceMeasurement = distanceMeasurement
        )
      } catch (_: Exception) {
        mutableScreenState.value = mutableScreenState.value.copy(
          isMeasurementInProgress = false,
          statusMessage = "Measurement failed. Review the logs for details."
        )
      }
    }
  }

  /**
   * Returns the current logs as plain text so the activity can share them.
   */
  fun buildShareableLogTranscript(): String {
    return applicationLogRecorder.buildShareableLogTranscript()
  }

  /**
   * Converts a measurement object into beginner-friendly summary text.
   */
  private fun buildMeasurementSummary(distanceMeasurement: DistanceMeasurement): String {
    return "Nearest reflection: %.2f meters\nConfidence: %.0f%%\nExplanation: %s".format(
      Locale.US,
      distanceMeasurement.measuredDistanceInMeters,
      distanceMeasurement.measurementConfidence * 100.0,
      distanceMeasurement.humanReadableExplanation
    )
  }

  companion object {
    private const val TAG = "MainScreenViewModel"
  }
}
