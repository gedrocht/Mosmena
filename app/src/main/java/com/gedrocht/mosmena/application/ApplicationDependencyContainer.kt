package com.gedrocht.mosmena.application

import com.gedrocht.mosmena.audio.AcousticPulseConfiguration
import com.gedrocht.mosmena.audio.AndroidAudioPulseEchoDistanceMeasuringService
import com.gedrocht.mosmena.audio.CorrelationBasedNearestReflectionDistanceEstimator
import com.gedrocht.mosmena.audio.HighFrequencyPulseGenerator
import com.gedrocht.mosmena.logging.ApplicationLogRecorder
import com.gedrocht.mosmena.logging.InMemoryApplicationLogRecorder
import com.gedrocht.mosmena.ui.MainScreenViewModelFactory
import kotlinx.coroutines.Dispatchers

/**
 * A very small manual dependency injection container.
 *
 * We avoid a larger framework here because the project is meant to be read by
 * beginners. Every dependency is created in plain Kotlin code.
 */
class ApplicationDependencyContainer {

  /**
   * The logger stores messages in memory for the on-screen log viewer and also
   * mirrors them to Logcat through Timber.
   */
  val applicationLogRecorder: ApplicationLogRecorder = InMemoryApplicationLogRecorder()

  /**
   * The default audio configuration expresses the pulse shape and timing values
   * used throughout the app unless a test injects different values.
   */
  val defaultAcousticPulseConfiguration = AcousticPulseConfiguration()

  /**
   * The signal generator produces the high-frequency pulse that the speaker
   * emits during each ranging attempt.
   */
  val highFrequencyPulseGenerator = HighFrequencyPulseGenerator()

  /**
   * The estimator consumes a recorded audio buffer and searches for the first
   * reflection that appears after the direct speaker-to-microphone coupling.
   */
  val nearestReflectionDistanceEstimator = CorrelationBasedNearestReflectionDistanceEstimator()

  /**
   * The measuring service is the Android-specific layer that talks to the
   * speaker and microphone.
   */
  val acousticDistanceMeasuringService = AndroidAudioPulseEchoDistanceMeasuringService(
    applicationLogRecorder = applicationLogRecorder,
    highFrequencyPulseGenerator = highFrequencyPulseGenerator,
    nearestReflectionDistanceEstimator = nearestReflectionDistanceEstimator,
    inputOutputCoroutineDispatcher = Dispatchers.IO
  )

  /**
   * Creates the view-model factory used by the activity.
   */
  fun createMainScreenViewModelFactory(): MainScreenViewModelFactory {
    return MainScreenViewModelFactory(
      acousticReflectionDistanceMeasuringService = acousticDistanceMeasuringService,
      applicationLogRecorder = applicationLogRecorder,
      defaultAcousticPulseConfiguration = defaultAcousticPulseConfiguration
    )
  }
}
