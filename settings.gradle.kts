pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "keep-together-server"

include("backend")
include("crypto")
include("protocol")
include("relay")
include("test-support")
include("tunnel")
