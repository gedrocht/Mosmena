package com.gedrocht.mosmena.logging

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Stores recent logs in memory while also mirroring them to Logcat.
 */
class InMemoryApplicationLogRecorder : ApplicationLogRecorder {

  private val mutableRecentLogMessages = MutableStateFlow<List<ApplicationLogMessage>>(emptyList())
  private val timestampFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS")
    .withZone(ZoneId.systemDefault())

  override val recentLogMessages: StateFlow<List<ApplicationLogMessage>> =
    mutableRecentLogMessages.asStateFlow()

  override fun recordVerboseMessage(tag: String, message: String) {
    appendMessage(
      applicationLogSeverity = ApplicationLogSeverity.VERBOSE,
      tag = tag,
      message = message
    )
    Timber.tag(tag).v(message)
  }

  override fun recordInformationMessage(tag: String, message: String) {
    appendMessage(
      applicationLogSeverity = ApplicationLogSeverity.INFORMATION,
      tag = tag,
      message = message
    )
    Timber.tag(tag).i(message)
  }

  override fun recordWarningMessage(tag: String, message: String) {
    appendMessage(
      applicationLogSeverity = ApplicationLogSeverity.WARNING,
      tag = tag,
      message = message
    )
    Timber.tag(tag).w(message)
  }

  override fun recordErrorMessage(tag: String, message: String, throwable: Throwable?) {
    appendMessage(
      applicationLogSeverity = ApplicationLogSeverity.ERROR,
      tag = tag,
      message = if (throwable == null) {
        message
      } else {
        "$message\n${throwable.stackTraceToString()}"
      }
    )
    if (throwable == null) {
      Timber.tag(tag).e(message)
    } else {
      Timber.tag(tag).e(throwable, message)
    }
  }

  override fun buildShareableLogTranscript(): String {
    return recentLogMessages.value.joinToString(separator = "\n\n") { applicationLogMessage ->
      "[${applicationLogMessage.timestampText}] " +
        "${applicationLogMessage.severity.name} " +
        "${applicationLogMessage.tag}: ${applicationLogMessage.message}"
    }
  }

  /**
   * Adds a message and trims the list so it stays easy to browse.
   */
  private fun appendMessage(
    applicationLogSeverity: ApplicationLogSeverity,
    tag: String,
    message: String
  ) {
    val timestampText = timestampFormatter.format(Instant.now())
    val newLogMessage = ApplicationLogMessage(
      severity = applicationLogSeverity,
      tag = tag,
      message = message,
      timestampText = timestampText
    )

    mutableRecentLogMessages.value = (
      mutableRecentLogMessages.value + newLogMessage
      ).takeLast(MAXIMUM_RETAINED_LOG_MESSAGE_COUNT)
  }

  companion object {
    private const val MAXIMUM_RETAINED_LOG_MESSAGE_COUNT = 250
  }
}
