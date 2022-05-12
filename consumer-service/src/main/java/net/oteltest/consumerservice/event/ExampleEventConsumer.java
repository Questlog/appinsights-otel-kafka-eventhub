package net.oteltest.consumerservice.event;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;

@Service
public class ExampleEventConsumer implements Consumer<String> {
  @Override
  public void accept(String value) {
    var client = HttpClient.newHttpClient();
    var request = HttpRequest.newBuilder()
        .uri(URI.create("https://google.de"))
        .build();

    try {
      client.send(request, BodyHandlers.ofString());
      client.send(request, BodyHandlers.ofString());
      client.send(request, BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

    if(value.equals("fail")) {
      throw new RuntimeException("something failed");
    }
  }
}
