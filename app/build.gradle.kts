plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.dokka)
  alias(libs.plugins.jetbrains.kotlinx.kover)
}

android {
  namespace = "com.gedrocht.mosmena"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.gedrocht.mosmena"
    minSdk = 29
    targetSdk = 36
    versionCode = 1
    versionName = "0.1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures {
    viewBinding = true
    buildConfig = true
  }

  buildTypes {
    debug {
      enableUnitTestCoverage = true
      enableAndroidTestCoverage = true
    }

    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  lint {
    abortOnError = true
    warningsAsErrors = true
    checkDependencies = true
    checkTestSources = true
    sarifReport = true
    xmlReport = true
    textReport = true
    explainIssues = true
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }

  testOptions {
    animationsDisabled = true
    unitTests.isIncludeAndroidResources = true
  }
}

dependencies {
  implementation(libs.androidx.activity.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.ktx)
  implementation(libs.androidx.recyclerview)
  implementation(libs.coroutines.android)
  implementation(libs.material)
  implementation(libs.timber)

  testImplementation(libs.coroutines.test)
  testImplementation(libs.junit4)
  testImplementation(libs.mockk)
  testImplementation(libs.truth)
  testImplementation(libs.turbine)

  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.test.core.ktx)
  androidTestImplementation(libs.androidx.test.rules)
}

kotlin {
  compilerOptions {
    allWarningsAsErrors.set(true)
    freeCompilerArgs.add("-Xjspecify-annotations=strict")
  }
}

kover {
  reports {
    total {
      filters {
        excludes {
          classes(
            "com.gedrocht.mosmena.MosmenaApplication",
            "com.gedrocht.mosmena.application.*",
            "com.gedrocht.mosmena.audio.AndroidAudioPulseEchoDistanceMeasuringService",
            "com.gedrocht.mosmena.ui.*",
          )
        }
      }
      verify {
        rule("Application coverage") {
          minBound(85)
          minBound(75, kotlinx.kover.gradle.plugin.dsl.CoverageUnit.BRANCH)
        }
      }
    }
  }
}
