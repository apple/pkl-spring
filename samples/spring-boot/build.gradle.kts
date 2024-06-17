plugins {
  application
  idea
  alias(libs.plugins.pkl)
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
}

dependencies {
  implementation(libs.springBoot)
  implementation(libs.springBootTest)
  implementation(libs.springBootAutoConfigure)
  implementation(libs.pklSpring)

  testImplementation(libs.junitApi)
  testImplementation(libs.assertJ)
}

pkl {
  javaCodeGenerators {
    register("configClasses") {
      generateGetters.set(true)
      generateSpringBootConfig.set(true)
      sourceModules.set(files("src/main/resources/AppConfig.pkl"))
    }
  }
}

application {
  mainClass.set("samples.boot.Application")
}

tasks.check {
  dependsOn(tasks.named("run"))
}
