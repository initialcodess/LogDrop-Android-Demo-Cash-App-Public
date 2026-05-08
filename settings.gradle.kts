pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://artifactory.logdrop.io/repository/android-logdrop-sdk/") }
        maven { url = uri("https://dev-nexus.logdrop.io/repository/dev-logdrop-sdk/") }
    }
}

rootProject.name = "LogDropAndroidDemoApp"
include(":app")
 