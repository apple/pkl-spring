import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import java.nio.charset.StandardCharsets
import java.util.*

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  `java-library`
  `maven-publish`
  idea
  alias(libs.plugins.pkl)
  alias(libs.plugins.nexusPublish)
  alias(libs.plugins.spotless)
  signing
}

private val isReleaseBuild = System.getProperty("releaseBuild") != null

version = if (isReleaseBuild) version else "$version-SNAPSHOT"

spotless {
  format("pkl") {
    target("*.pkl")
    licenseHeaderFile(rootProject.file("buildSrc/src/main/resources/license-header.line-comment.txt"), "/// ")
  }
  java {
    googleJavaFormat("1.15.0")
    targetExclude("**/generated/**", "**/build/**")
    licenseHeaderFile(rootProject.file("buildSrc/src/main/resources/license-header.star-block.txt"))
  }
}

nexusPublishing {
  repositories {
    sonatype {
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
    }
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
}

dependencies {
  compileOnly(libs.springBoot)

  // `api` instead of `implementation`
  // so that users of pkl-spring don't need to add pkl-config-java-all
  // to be able to compile their generated config classes
  api(libs.pklConfigJavaAll)

  testImplementation(libs.springTest)
  testImplementation(libs.springBoot)
  testImplementation(libs.springBootTest)
  testImplementation(libs.springBootAutoConfigure)
  testImplementation(libs.junitApi)
  testImplementation(libs.assertJ)

  testRuntimeOnly(libs.junitEngine)
}

pkl {
  javaCodeGenerators {
    register("configClasses") {
      generateGetters.set(true)
      generateSpringBootConfig.set(true)
      sourceSet.set(sourceSets.test.get())
      sourceModules.set(files("src/test/resources/application.pkl"))
    }
  }
}

idea {
  project {
    languageLevel = IdeaLanguageLevel("11")
    jdkName = "11"
  }
}

tasks.idea {
  doFirst {
    throw GradleException(
        "To open this project in IntelliJ, go to File->Open " +
            "and select the project's root directory. Do *not* run `./gradlew idea`.")
  }
}

tasks.jar {
  manifest {
    attributes("Automatic-Module-Name" to "org.pkl.spring")
  }
}

tasks.test {
  useJUnitPlatform()
  reports {
    html.required.set(true)
  }
}

tasks.javadoc {
  classpath = sourceSets.main.get().output + sourceSets.main.get().compileClasspath
  source = sourceSets.main.get().allJava
  title = "${project.name} ${project.version} API"
}

val sourcesJar by tasks.registering(Jar::class) {
  archiveClassifier.set("sources")
  from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.registering(Jar::class) {
  archiveClassifier.set("javadoc")
  from(tasks.javadoc)
}

publishing {
  publications {
    create<MavenPublication>("library") {
      from(components["java"])
      artifact(sourcesJar)
      artifact(javadocJar)
      pom {
        name.set("pkl-spring")
        url.set("https://github.com/apple/pkl-spring")
        description.set("Spring Boot extension for configuring Boot apps with Pkl.")
        licenses {
          license {
            name = "Apache 2.0"
            url = "https://github.com/apple/pkl-spring/blob/main/LICENSE.txt"
          }
        }
        developers {
          developer {
            id.set("pkl-authors")
            name.set("The Pkl Authors")
            email.set("pkl-oss@group.apple.com")
          }
        }
        scm {
          connection.set("scm:git:git://github.com/apple/pkl.git")
          developerConnection.set("scm:git:ssh://github.com/apple/pkl.git")
          url.set("https://github.com/apple/pkl/tree/${if (isReleaseBuild) version else "main"}")
        }
        issueManagement {
          system.set("GitHub Issues")
          url.set("https://github.com/apple/pkl-spring/issues")
        }
        ciManagement {
          system.set("Circle CI")
          url.set("https://app.circleci.com/pipelines/github/apple/pkl-spring")
        }
      }
    }
  }
}

val printVersion by tasks.registering {
  doFirst { println(version) }
}

signing {
  // provided as env vars `ORG_GRADLE_PROJECT_signingKey` and `ORG_GRADLE_PROJECT_signingPassword`
  // in CI.
  val signingKey = (findProperty("signingKey") as String?)
    ?.let { Base64.getDecoder().decode(it).toString(StandardCharsets.US_ASCII) }
  val signingPassword = findProperty("signingPassword") as String?
  if (signingKey != null && signingPassword != null) {
    useInMemoryPgpKeys(signingKey, signingPassword)
  }
  sign(publishing.publications["library"])
}
