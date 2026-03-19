package com.gedrocht.mosmena

import android.app.Application
import com.gedrocht.mosmena.application.ApplicationDependencyContainer
import timber.log.Timber

/**
 * Application entry point.
 *
 * This class is created before any activity. It is a convenient place to
 * create long-lived dependencies such as loggers and services.
 */
class MosmenaApplication : Application() {

  /**
   * The dependency container groups together the objects that the rest of the
   * app needs. We keep it here so the activity can retrieve shared instances.
   */
  lateinit var applicationDependencyContainer: ApplicationDependencyContainer
    private set

  /**
   * Initializes logging and dependency wiring once for the whole process.
   */
  override fun onCreate() {
    super.onCreate()

    // Timber forwards messages to Logcat. We plant the debug tree only in
    // debug builds so release builds do not emit unnecessary verbose logs.
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }

    applicationDependencyContainer = ApplicationDependencyContainer()
  }
}
