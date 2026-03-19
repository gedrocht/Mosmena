package com.gedrocht.mosmena.logging

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for [InMemoryApplicationLogRecorder].
 */
class InMemoryApplicationLogRecorderTest {

  @Test
  fun recordInformationMessage_addsMessageToStateFlowAndTranscript() {
    val inMemoryApplicationLogRecorder = InMemoryApplicationLogRecorder()

    inMemoryApplicationLogRecorder.recordInformationMessage(
      tag = "TestTag",
      message = "A clear and descriptive test log message."
    )

    assertThat(inMemoryApplicationLogRecorder.recentLogMessages.value).hasSize(1)
    assertThat(inMemoryApplicationLogRecorder.buildShareableLogTranscript()).contains("TestTag")
    assertThat(inMemoryApplicationLogRecorder.buildShareableLogTranscript()).contains("descriptive")
  }

  @Test
  fun recordVerboseMessage_trimsOldMessagesAfterMaximumRetentionCount() {
    val inMemoryApplicationLogRecorder = InMemoryApplicationLogRecorder()

    repeat(300) { messageIndex ->
      inMemoryApplicationLogRecorder.recordVerboseMessage(
        tag = "RetentionTest",
        message = "Message number $messageIndex"
      )
    }

    assertThat(inMemoryApplicationLogRecorder.recentLogMessages.value).hasSize(250)
    assertThat(inMemoryApplicationLogRecorder.buildShareableLogTranscript()).doesNotContain("Message number 0")
    assertThat(inMemoryApplicationLogRecorder.buildShareableLogTranscript()).contains("Message number 299")
  }
}
