package com.gedrocht.mosmena.logging

import kotlinx.coroutines.flow.StateFlow

/**
 * Contract for collecting logs that are easy to consume both in the app and in
 * platform-native tools such as Logcat.
 */
interface ApplicationLogRecorder {

  /**
   * A stream of the most recent log entries, ordered from oldest to newest.
   */
  val recentLogMessages: StateFlow<List<ApplicationLogMessage>>

  /**
   * Records a very detailed diagnostic message.
   */
  fun recordVerboseMessage(tag: String, message: String)

  /**
   * Records an informational message.
   */
  fun recordInformationMessage(tag: String, message: String)

  /**
   * Records a warning message.
   */
  fun recordWarningMessage(tag: String, message: String)

  /**
   * Records an error message and optionally includes an exception.
   */
  fun recordErrorMessage(tag: String, message: String, throwable: Throwable? = null)

  /**
   * Builds a plain-text transcript that can be shared from the device.
   */
  fun buildShareableLogTranscript(): String
}
