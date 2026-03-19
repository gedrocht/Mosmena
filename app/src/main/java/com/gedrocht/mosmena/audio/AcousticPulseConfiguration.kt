package com.gedrocht.mosmena.audio

/**
 * Describes how a ranging pulse should be emitted and interpreted.
 *
 * @property sampleRateInHertz Number of samples captured per second.
 * @property pulseDurationInMilliseconds How long the emitted pulse lasts.
 * @property prePlaybackCaptureDurationInMilliseconds How much audio to capture
 * before playing the pulse. This allows the algorithm to compare the direct
 * coupling and reflection peaks within the same recording.
 * @property totalRecordingDurationInMilliseconds Total capture window.
 * @property pulseStartFrequencyInHertz Frequency at the start of the chirp.
 * @property pulseEndFrequencyInHertz Frequency at the end of the chirp.
 * @property pulseAmplitude Scaling factor for the emitted pulse.
 * @property speedOfSoundInMetersPerSecond Conversion constant used to turn a
 * sample delay into a distance estimate.
 * @property minimumExpectedDistanceInMeters The shortest reflection distance we
 * consider valid. This avoids treating direct coupling as a reflection.
 * @property maximumExpectedDistanceInMeters The farthest distance shown in the
 * visualization and accepted by the estimator.
 */
data class AcousticPulseConfiguration(
  val sampleRateInHertz: Int = 48_000,
  val pulseDurationInMilliseconds: Int = 20,
  val prePlaybackCaptureDurationInMilliseconds: Int = 20,
  val totalRecordingDurationInMilliseconds: Int = 180,
  val pulseStartFrequencyInHertz: Double = 20_000.0,
  val pulseEndFrequencyInHertz: Double = 22_000.0,
  val pulseAmplitude: Double = 0.65,
  val speedOfSoundInMetersPerSecond: Double = 343.0,
  val minimumExpectedDistanceInMeters: Double = 0.15,
  val maximumExpectedDistanceInMeters: Double = 3.00
)
