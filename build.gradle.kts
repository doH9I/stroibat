// Top-level build file where you can add configuration options common to all sub-modules/projects.
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("kotlin-kapt") apply false
    id("kotlin-parcelize") apply false
}

buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
    }
}