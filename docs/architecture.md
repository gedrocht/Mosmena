# Architecture

Mosmena intentionally keeps the architecture small enough for beginners to trace.

## Layers

- `audio`: pulse generation, recording orchestration, and reflection estimation
- `logging`: in-memory and Logcat-friendly logging
- `ui`: activity, custom visualization view, adapter, and view-model
- `application`: dependency assembly

## Why the core logic is testable

The reflection estimator and pulse generator are pure Kotlin classes. That means unit tests can feed them synthetic data without touching Android hardware.

## Why there is only one activity

The project is a focused demonstration, so a single-screen design keeps navigation complexity out of the way.
