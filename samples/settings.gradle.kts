enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "samples"

include("spring-boot")
include("spring-boot-kotlin")

includeBuild("../")

pluginManagement {
  repositories {
    mavenCentral()
  }
}

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  repositories {
    mavenCentral()
  }

  // use same version catalog as main build
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}
