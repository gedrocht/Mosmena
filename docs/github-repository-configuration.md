# GitHub Repository Configuration

## Intended repository description

Android acoustic ranging demo that emits near-ultrasonic pulses, estimates the nearest reflection, and teaches the codebase through beginner-first docs.

## Intended topics

- android
- kotlin
- acoustics
- signal-processing
- ultrasonic
- testing
- documentation

## Branch protection recommendations

- Require `ci / repository-policy`
- Require `ci / static-analysis`
- Require `ci / unit-tests`
- Require `ci / instrumentation-tests`
- Require `security / codeql`
- Require `review / dependency-review`
- Require `review / wrapper-validation`

## Applying metadata automatically

Run the repository script `scripts/github/configure-repository.ps1` with GitHub CLI authenticated.
