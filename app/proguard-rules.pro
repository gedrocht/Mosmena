# Keep the model classes readable in stack traces because the application is
# intended to be educational as well as functional.
-keep class com.gedrocht.mosmena.model.** { *; }

# Timber uses static calls, so the default optimization rules are sufficient.
