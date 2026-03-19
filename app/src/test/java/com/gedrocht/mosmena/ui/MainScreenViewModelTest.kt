package com.gedrocht.mosmena.ui

import com.gedrocht.mosmena.audio.AcousticPulseConfiguration
import com.gedrocht.mosmena.audio.AcousticReflectionDistanceMeasuringService
import com.gedrocht.mosmena.audio.DistanceMeasurement
import com.gedrocht.mosmena.logging.InMemoryApplicationLogRecorder
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [MainScreenViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelTest {

  @Suppress("unused")
  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun startSingleMeasurement_withoutPermission_updatesStatusInsteadOfCallingService() = runTest {
    val fakeAcousticReflectionDistanceMeasuringService =
      FakeAcousticReflectionDistanceMeasuringService()
    val mainScreenViewModel = MainScreenViewModel(
      acousticReflectionDistanceMeasuringService = fakeAcousticReflectionDistanceMeasuringService,
      applicationLogRecorder = InMemoryApplicationLogRecorder(),
      defaultAcousticPulseConfiguration = AcousticPulseConfiguration()
    )

    mainScreenViewModel.startSingleMeasurement()

    assertThat(mainScreenViewModel.screenState.value.statusMessage)
      .contains("must be granted")
    assertThat(fakeAcousticReflectionDistanceMeasuringService.wasCalled).isFalse()
  }

  @Test
  fun startSingleMeasurement_withPermission_emitsSuccessfulMeasurementState() = runTest {
    val fakeAcousticReflectionDistanceMeasuringService =
      FakeAcousticReflectionDistanceMeasuringService()
    val mainScreenViewModel = MainScreenViewModel(
      acousticReflectionDistanceMeasuringService = fakeAcousticReflectionDistanceMeasuringService,
      applicationLogRecorder = InMemoryApplicationLogRecorder(),
      defaultAcousticPulseConfiguration = AcousticPulseConfiguration()
    )

    mainScreenViewModel.onMicrophonePermissionStateChanged(true)
    mainScreenViewModel.startSingleMeasurement()
    advanceUntilIdle()

    assertThat(mainScreenViewModel.screenState.value.isMeasurementInProgress).isFalse()
    assertThat(mainScreenViewModel.screenState.value.measurementSummary)
      .contains("Nearest reflection")
  }

  /**
   * Minimal fake that behaves like a successful measuring service.
   */
  private class FakeAcousticReflectionDistanceMeasuringService :
    AcousticReflectionDistanceMeasuringService {

    var wasCalled: Boolean = false

    override suspend fun measureNearestReflectionDistance(
      acousticPulseConfiguration: AcousticPulseConfiguration
    ): DistanceMeasurement {
      wasCalled = true
      assertThat(acousticPulseConfiguration.sampleRateInHertz).isGreaterThan(0)
      return DistanceMeasurement(
        measuredDistanceInMeters = 0.75,
        measurementConfidence = 0.90,
        directCouplingPeakSampleIndex = 120,
        nearestReflectionPeakSampleIndex = 330,
        correlationStrength = 0.88,
        humanReadableExplanation = "A fake test measurement."
      )
    }
  }
}
