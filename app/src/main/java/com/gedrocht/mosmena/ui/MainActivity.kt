package com.gedrocht.mosmena.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gedrocht.mosmena.MosmenaApplication
import com.gedrocht.mosmena.R
import com.gedrocht.mosmena.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

/**
 * The single screen of the application.
 */
class MainActivity : AppCompatActivity() {

  private lateinit var activityMainBinding: ActivityMainBinding
  private lateinit var applicationLogRecyclerViewAdapter: ApplicationLogRecyclerViewAdapter

  private val mainScreenViewModel: MainScreenViewModel by viewModels {
    (application as MosmenaApplication)
      .applicationDependencyContainer
      .createMainScreenViewModelFactory()
  }

  private val microphonePermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { wasPermissionGranted ->
    mainScreenViewModel.onMicrophonePermissionStateChanged(wasPermissionGranted)
  }

  /**
   * Inflates the screen, wires the controls, and begins collecting state.
   */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(activityMainBinding.root)

    configureRecyclerView()
    configureButtons()
    collectViewModelState()
    synchronizePermissionState()
  }

  /**
   * Hooks up the log list adapter.
   */
  private fun configureRecyclerView() {
    applicationLogRecyclerViewAdapter = ApplicationLogRecyclerViewAdapter()
    activityMainBinding.applicationLogRecyclerView.adapter = applicationLogRecyclerViewAdapter
  }

  /**
   * Wires button clicks to simple actions.
   */
  private fun configureButtons() {
    activityMainBinding.grantPermissionButton.setOnClickListener {
      microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    activityMainBinding.startMeasurementButton.setOnClickListener {
      if (hasMicrophonePermission()) {
        mainScreenViewModel.startSingleMeasurement()
      } else {
        microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
      }
    }

    activityMainBinding.shareLogsButton.setOnClickListener {
      startActivity(
        Intent.createChooser(
          Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_logs_subject))
            putExtra(Intent.EXTRA_TEXT, mainScreenViewModel.buildShareableLogTranscript())
          },
          getString(R.string.share_logs_chooser_title)
        )
      )
    }
  }

  /**
   * Collects state flows only while the activity is visible.
   */
  private fun collectViewModelState() {
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        launch {
          mainScreenViewModel.screenState.collect { mainScreenState ->
            render(mainScreenState)
          }
        }
        launch {
          mainScreenViewModel.visibleLogMessages.collect { visibleLogMessages ->
            applicationLogRecyclerViewAdapter.submitList(visibleLogMessages)
          }
        }
      }
    }
  }

  /**
   * Updates the interface from the current state snapshot.
   */
  private fun render(mainScreenState: MainScreenState) {
    activityMainBinding.grantPermissionButton.isVisible = !mainScreenState.hasMicrophonePermission
    activityMainBinding.startMeasurementButton.isEnabled =
      mainScreenState.hasMicrophonePermission && !mainScreenState.isMeasurementInProgress
    activityMainBinding.measurementStatusTextView.text = mainScreenState.statusMessage
    activityMainBinding.measurementSummaryTextView.text = mainScreenState.measurementSummary
    activityMainBinding.distanceRadarView.showDistanceMeasurement(
      mainScreenState.latestDistanceMeasurement
    )
  }

  /**
   * Reads the current permission value from the platform and forwards it to the
   * view-model.
   */
  private fun synchronizePermissionState() {
    mainScreenViewModel.onMicrophonePermissionStateChanged(hasMicrophonePermission())
  }

  private fun hasMicrophonePermission(): Boolean {
    return ContextCompat.checkSelfPermission(
      this,
      Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
  }
}
