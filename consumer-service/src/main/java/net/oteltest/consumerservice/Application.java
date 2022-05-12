package net.oteltest.consumerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "net.oteltest.consumerservice")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
