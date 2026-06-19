// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.services) apply false
}
buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://artifactory.logdrop.io/repository/logdrop-gradle-plugin/") }
    }
    dependencies {
        classpath("io.logdrop.gradle:plugin:1.1.0")
    }
}