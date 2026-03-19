# Testing and Quality

## Test layers

- Unit tests for signal generation
- Unit tests for reflection estimation
- Unit tests for logging behavior
- Unit tests for view-model state transitions
- Instrumented smoke tests for the activity

## Static analysis

- Detekt enforces Kotlin code quality rules.
- ktlint enforces consistent formatting.
- Android Lint checks Android-specific correctness problems.

## Security and review checks

- Gitleaks scans for committed secrets.
- CodeQL performs static security analysis.
- Dependency Review blocks risky pull request dependency changes.
- Gradle wrapper validation protects the wrapper from tampering.
- OpenSSF Scorecard monitors repository security posture.

## Coverage

Kover enforces project coverage thresholds in CI.
