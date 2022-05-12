package net.oteltest.restservice.web;

import lombok.RequiredArgsConstructor;
import net.oteltest.restservice.config.KafkaConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class SomeRestController {

  private final Producer<Long, String> kafkaProducer;
  private final KafkaConfig kafkaConfig;

  @GetMapping(path = "/ok")
  public String sendGoodEvent() {

    kafkaProducer.send(new ProducerRecord<>(kafkaConfig.getTopic(), "ok"));

    return "ok";
  }

  @GetMapping(path = "/fail")
  public String sendFailEvent() {

    kafkaProducer.send(new ProducerRecord<>(kafkaConfig.getTopic(), "fail"));

    return "fail";
  }
}
