# Contributing to Mosmena

Thank you for helping improve Mosmena.

## Development expectations

- Use descriptive, beginner-friendly code and comments.
- Keep variable names explicit rather than abbreviated.
- Add or update tests with every behavior change.
- Keep public APIs documented with KDoc so Dokka can publish them.
- Run the full local quality suite before opening a pull request.

## Recommended local workflow

```bash
./gradlew clean ktlintCheck detekt lintDebug testDebugUnitTest connectedDebugAndroidTest koverVerify
```

Equivalent helper scripts are also available:

```bash
./scripts/build.sh
./scripts/test.sh
./scripts/quality.sh --include-instrumentation-tests
```

## Pull request quality bar

- All GitHub Actions checks must pass.
- Coverage thresholds must remain satisfied.
- Security review must be addressed.
- Documentation must stay accurate for both the README and the docs site.
