import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.dropbox.affectedmoduledetector:affectedmoduledetector:0.1.4")
}
