# Acoustic Ranging Explained

## Core idea

Acoustic ranging measures how long it takes for sound to travel to an object and back.

Distance is estimated with:

`distance = (time_delay * speed_of_sound) / 2`

The division by two matters because the sound travels twice:

- from the phone to the object
- from the object back to the phone

## Why direct coupling matters

Phones introduce speaker and microphone latency. Mosmena reduces that problem by detecting the direct speaker-to-microphone coupling inside the same recording and using it as a reference point.

That means the app focuses on the delay between:

- the direct coupling peak
- the first later reflection peak

## Why correlation is used

The emitted pulse is known in advance. Correlation answers the question:

"At this point in the recording, how much does the audio look like the pulse we transmitted?"

A strong match produces a peak.

## Limitations

- Strong device audio processing can distort the pulse.
- Some speakers barely reproduce energy near 20 kHz.
- Weak reflections can be masked by direct coupling.
- The algorithm is simple and does not perform advanced beamforming or hardware calibration.
