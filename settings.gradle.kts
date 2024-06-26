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
//        maven { url = java.net.URI("https://devrepo.kakao.com/nexus/repository/kakaomap-releases/") }
        maven { url = java.net.URI("https://repository.map.naver.com/archive/maven") }
    }
}

rootProject.name = "Bus_AutoSystem"
include(":app")
