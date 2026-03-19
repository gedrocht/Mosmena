import org.jetbrains.dokka.gradle.DokkaTask
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import io.gitlab.arturbosch.detekt.Detekt

plugins {
  alias(libs.plugins.jetbrains.dokka)
  alias(libs.plugins.jetbrains.kotlinx.kover)
  alias(libs.plugins.detekt)
  alias(libs.plugins.ktlint)
}

allprojects {
  group = "com.gedrocht.mosmena"
  version = "0.1.0"
}

subprojects {
  apply(plugin = "io.gitlab.arturbosch.detekt")
  apply(plugin = "org.jlleitschuh.gradle.ktlint")

  extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    parallel = true
  }

  extensions.configure<KtlintExtension> {
    android.set(true)
    verbose.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)
  }

  tasks.withType<Detekt>().configureEach {
    jvmTarget = "17"
    reports {
      html.required.set(true)
      sarif.required.set(true)
      xml.required.set(true)
      md.required.set(false)
    }
  }

  tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets.configureEach {
      documentedVisibilities.set(
        setOf(
          org.jetbrains.dokka.DokkaConfiguration.Visibility.PUBLIC,
          org.jetbrains.dokka.DokkaConfiguration.Visibility.PROTECTED
        )
      )
      reportUndocumented.set(true)
      skipEmptyPackages.set(true)
      jdkVersion.set(17)
    }
  }
}
