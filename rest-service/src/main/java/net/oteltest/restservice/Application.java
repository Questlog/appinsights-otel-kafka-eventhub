package net.oteltest.restservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "net.oteltest.restservice")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
