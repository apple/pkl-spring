plugins {
  alias(libs.plugins.pkl)
  alias(libs.plugins.kotlin)
  alias(libs.plugins.springBoot)
}

dependencies {
  implementation(libs.kotlinStdLib)
  implementation(libs.springBoot)
  implementation(libs.springBootTest)
  implementation(libs.springBootAutoConfigure)
  implementation(libs.pklSpring)

  runtimeOnly(libs.kotlinReflect)

  testImplementation(libs.junitApi)
  testImplementation(libs.assertJ)
}

pkl {
  kotlinCodeGenerators {
    register("configClasses") {
      generateSpringBootConfig.set(true)
      sourceModules.set(files("src/main/resources/AppConfig.pkl"))
    }
  }
}

tasks.check {
  dependsOn(tasks.named("bootRun"))
}
