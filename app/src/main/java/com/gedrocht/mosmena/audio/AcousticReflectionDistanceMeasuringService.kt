package com.gedrocht.mosmena.audio

/**
 * Abstraction for the part of the application that performs an end-to-end
 * measurement. Tests can replace the Android implementation with a fake.
 */
interface AcousticReflectionDistanceMeasuringService {

  /**
   * Emits a pulse, records the response, and estimates the nearest reflection.
   *
   * @throws IllegalStateException when the microphone or speaker pipeline
   * cannot be prepared or when no believable reflection is found.
   */
  suspend fun measureNearestReflectionDistance(
    acousticPulseConfiguration: AcousticPulseConfiguration
  ): DistanceMeasurement
}
