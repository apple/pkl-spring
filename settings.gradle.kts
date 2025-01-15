rootProject.name = "pkl-spring"

pluginManagement {
  // To develop against a local Pkl build's Pkl Gradle plugin, uncomment the next line.
  //includeBuild("../pkl")

  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositories {
    // only use repositories specified here
    // https://github.com/gradle/gradle/issues/15732
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    mavenCentral()
  }
}

val javaVersion = JavaVersion.current()
require(javaVersion.isCompatibleWith(JavaVersion.VERSION_17)) {
  "Project requires Java 17 or higher, but found ${javaVersion.majorVersion}."
}

//includeBuild("../pkl")
// To develop against a local Pkl build's pkl-config-java library,
// uncomment the above line and replace `name = "pkl-config-java-all"`
// with `name = "pkl-config-java"` in libs.versions.toml.
// Editing libs.versions.toml is necessary because I couldn't get
// `includeBuild(../pkl) { dependencySubstitution  {...} }` to work for pkl-config-java-all.
