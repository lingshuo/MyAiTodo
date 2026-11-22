// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // AGP 9's built-in Kotlin pins KGP at 2.2.10, which cannot read the Kotlin
        // metadata (2.4.x) produced by the current AndroidX / Compose artifacts.
        // Explicitly overriding the KGP on the buildscript classpath upgrades both
        // the compiler and the bundled kotlin-stdlib. Keep in sync with
        // `kotlin` in gradle/libs.versions.toml.
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.21")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
}
