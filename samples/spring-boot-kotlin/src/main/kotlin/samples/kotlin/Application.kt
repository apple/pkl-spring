package samples.kotlin

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean

@SpringBootApplication
@ConfigurationPropertiesScan
open class Application {
  @Bean
  @Suppress("unused")
  open fun commandLineRunner(ctx: ApplicationContext): CommandLineRunner {
    return CommandLineRunner {
      val server = ctx.getBean(Server::class.java)
      println(server.config)
    }
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      SpringApplication(Application::class.java).run(*args)
    }
  }
}