# First Measurement Walkthrough

This tutorial assumes you are a beginner and want to understand the full app flow.

## Step 1: Open the main activity

`MainActivity` checks whether the microphone permission is already granted. If it is not, the interface keeps the measurement button disabled and shows the permission button.

## Step 2: Request permission

When the permission button is pressed, Android shows the standard microphone permission dialog. The result is forwarded to the view-model.

## Step 3: Start one measurement

The view-model asks `AndroidAudioPulseEchoDistanceMeasuringService` to run one measurement attempt.

## Step 4: Generate the pulse

`HighFrequencyPulseGenerator` produces a short chirp whose frequency rises over time. A Hann window is applied so the signal starts and ends smoothly.

## Step 5: Record and play

The measuring service starts recording first, waits for a short pre-roll window, then plays the pulse.

## Step 6: Estimate the nearest reflection

`CorrelationBasedNearestReflectionDistanceEstimator` correlates the known pulse with the recording and searches for the earliest reflection after direct coupling.

## Step 7: Visualize the result

`DistanceRadarView` draws a simple scale, a phone marker, and a reflection marker positioned according to the measured distance.
