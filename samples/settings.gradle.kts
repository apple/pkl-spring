rootProject.name = "samples"

include("spring-boot")
include("spring-boot-kotlin")

includeBuild("../")

pluginManagement {
  // To develop against a local Pkl build's Pkl Gradle plugin, uncomment the next line.
  //includeBuild("../../pkl")

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
