package com.gedrocht.mosmena.ui

import android.os.SystemClock
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.gedrocht.mosmena.R
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented smoke tests for the main activity.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

  @Suppress("unused")
  @get:Rule
  val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

  @Test
  fun launch_showsTitleAndLogSection() {
    waitForWindowFocus()

    onView(withId(R.id.applicationTitleTextView))
      .check(matches(isDisplayed()))
      .check(matches(withText("Mosmena")))

    onView(withId(R.id.logsHeadlineTextView))
      .check(matches(isDisplayed()))
  }

  private fun waitForWindowFocus() {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val timeoutAtUptimeMillis = SystemClock.uptimeMillis() + WINDOW_FOCUS_TIMEOUT_IN_MILLISECONDS

    while (SystemClock.uptimeMillis() < timeoutAtUptimeMillis) {
      instrumentation.waitForIdleSync()

      var hasWindowFocus = false
      activityScenarioRule.scenario.onActivity { mainActivity ->
        hasWindowFocus = mainActivity.window?.decorView?.hasWindowFocus() == true
      }

      if (hasWindowFocus) {
        return
      }

      SystemClock.sleep(WINDOW_FOCUS_POLL_INTERVAL_IN_MILLISECONDS)
    }

    fail("MainActivity never gained window focus before Espresso assertions.")
  }

  private companion object {
    private const val WINDOW_FOCUS_POLL_INTERVAL_IN_MILLISECONDS = 250L
    private const val WINDOW_FOCUS_TIMEOUT_IN_MILLISECONDS = 15_000L
  }
}
