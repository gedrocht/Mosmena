package com.gedrocht.mosmena.ui

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.gedrocht.mosmena.R
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
    onView(withId(R.id.applicationTitleTextView))
      .check(matches(isDisplayed()))
      .check(matches(withText("Mosmena")))

    onView(withId(R.id.logsHeadlineTextView))
      .check(matches(isDisplayed()))
  }
}
