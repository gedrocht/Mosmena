package com.gedrocht.mosmena.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gedrocht.mosmena.audio.AcousticPulseConfiguration
import com.gedrocht.mosmena.audio.AcousticReflectionDistanceMeasuringService
import com.gedrocht.mosmena.logging.ApplicationLogRecorder

/**
 * Simple factory for constructing the view-model with its dependencies.
 */
class MainScreenViewModelFactory(
  private val acousticReflectionDistanceMeasuringService: AcousticReflectionDistanceMeasuringService,
  private val applicationLogRecorder: ApplicationLogRecorder,
  private val defaultAcousticPulseConfiguration: AcousticPulseConfiguration
) : ViewModelProvider.Factory {

  /**
   * Creates the one supported view-model type for the main screen.
   */
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    check(modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
      "Unsupported view-model class: ${modelClass.name}"
    }

    return MainScreenViewModel(
      acousticReflectionDistanceMeasuringService = acousticReflectionDistanceMeasuringService,
      applicationLogRecorder = applicationLogRecorder,
      defaultAcousticPulseConfiguration = defaultAcousticPulseConfiguration
    ) as T
  }
}
