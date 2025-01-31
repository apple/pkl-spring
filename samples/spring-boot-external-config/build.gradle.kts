plugins {
  java
  alias(libs.plugins.pkl)
  alias(libs.plugins.springBoot)
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
      sourceModules.set(files("config/AppConfig.pkl"))
    }
  }
}

tasks.check {
  dependsOn(tasks.named("bootRun"))
}
