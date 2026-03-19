package com.gedrocht.mosmena.logging

/**
 * A single application log entry.
 *
 * @property severity Severity level used by both the on-screen list and Logcat.
 * @property tag Logical source label for the message.
 * @property message Human-readable message text.
 * @property timestampText Preformatted timestamp string for display.
 */
data class ApplicationLogMessage(
  val severity: ApplicationLogSeverity,
  val tag: String,
  val message: String,
  val timestampText: String
)

/**
 * Log levels used by the in-memory logger and the on-screen viewer.
 */
enum class ApplicationLogSeverity {
  /** Most detailed logging level, intended for step-by-step diagnostics. */
  VERBOSE,

  /** Normal informational messages that describe healthy application progress. */
  INFORMATION,

  /** Messages that describe unusual but still recoverable situations. */
  WARNING,

  /** Messages that describe a failure or unrecoverable situation. */
  ERROR
}
