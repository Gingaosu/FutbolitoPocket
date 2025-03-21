pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Add JitPack repository for compose-sensors
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "FutbolitoPocket2"
include(":app")
 