package samples.boot;

import org.springframework.stereotype.Service;

@Service
public class Server {
  private final AppConfig.Server config;

  public Server(AppConfig.Server config) {
    this.config = config;
  }

  public AppConfig.Server getConfig() {
    return config;
  }
}