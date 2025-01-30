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
      sourceModules.set(files("src/main/resources/AppConfig.pkl", "appConfigExternal.pkl"))
    }
  }
}

tasks.check {
  dependsOn(tasks.named("bootRun"))
}
