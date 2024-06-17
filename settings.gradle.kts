pluginManagement {
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

rootProject.name = "pkl-spring"
