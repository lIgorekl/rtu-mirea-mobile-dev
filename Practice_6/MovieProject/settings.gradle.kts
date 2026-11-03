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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MovieProject"
include(":app")
include(":data")
include(":domain")
include(":1_1_ScrollViewApp")
include(":1_2_ListViewApp")
include(":1_3_RecyclerViewApp")
include(":1_RetrofitApp")
include(":1_FragmentApp")
include(":2_FragmentManagerApp")
include(":3_ResultApiFragmentApp")
