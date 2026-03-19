# Mosmena Documentation

Mosmena is an Android learning project that demonstrates a simple acoustic pulse-echo workflow on a phone.

## What the app does

1. It generates a short high-frequency chirp.
2. It plays that chirp through the phone speaker.
3. It records audio from the phone microphone.
4. It looks for the direct speaker-to-microphone coupling peak.
5. It looks for the next significant peak, which is interpreted as the nearest reflection.
6. It converts the extra delay into a distance estimate.

## Who this documentation is for

- Beginners who want a patient explanation of the code
- Android developers who want a testable signal-processing demo
- Reviewers who want to understand the project's quality gates

## Documentation map

- Start with [Beginner Quickstart](beginner-quickstart.md) if you want the shortest step-by-step path.
- Start with [Getting Started](getting-started.md) if you are new to the repo.
- Read [First Measurement Walkthrough](tutorials/first-measurement.md) for a guided tour.
- Read [Acoustic Ranging Explained](acoustic-ranging-explained.md) to understand the algorithm.
- Read [Architecture](architecture.md) before changing the app structure.
- Read [Testing and Quality](testing-and-quality.md) before opening a pull request.
- Read [External Libraries](external-libraries.md) when you need third-party references.
- Open [API Docs](api/index.md) for generated reference material.
