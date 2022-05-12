package net.oteltest.restservice.config;

import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfiguration {
  private final KafkaConfig kafkaConfig;

  @Bean
  public Producer<Long, String> kafkaProducer() {
    Properties properties = new Properties();
    properties.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaConfig.getClientId());
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
    properties.put("security.protocol", kafkaConfig.getSecurityProtocol());
    properties.put("sasl.mechanism", kafkaConfig.getSaslMechanism());
    properties.put("sasl.jaas.config", kafkaConfig.getSaslJaasConfig());

    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    return new KafkaProducer<>(properties);
  }
}
