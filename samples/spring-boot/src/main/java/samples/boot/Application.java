package samples.boot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Application {
  public static void main(String... args) {
    new SpringApplication(Application.class).run(args);
  }

  @Bean
  @SuppressWarnings("unused")
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
      var server = ctx.getBean(Server.class);
      System.out.println(server.getConfig());
    };
  }
}