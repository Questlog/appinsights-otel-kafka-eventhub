package net.oteltest.consumerservice.config;

import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.oteltest.consumerservice.event.ExampleEventConsumer;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerConfiguration {
  private final KafkaConfig kafkaConfig;
  private final ExecutorService executorService = Executors.newSingleThreadExecutor();
  private final ExampleEventConsumer exampleEventConsumer;

  @Bean
  public Consumer<Long, String> kafkaConsumer() {
    Properties properties = new Properties();
    properties.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaConfig.getClientId());
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
    properties.put("security.protocol", kafkaConfig.getSecurityProtocol());
    properties.put("sasl.mechanism", kafkaConfig.getSaslMechanism());
    properties.put("sasl.jaas.config", kafkaConfig.getSaslJaasConfig());
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getGroupId());

    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    final Consumer<Long, String> consumer = new KafkaConsumer<>(properties);

    consumer.subscribe(Collections.singletonList(kafkaConfig.getTopic()));

    executorService.submit(() -> pollMessages(kafkaConsumer()));

    return consumer;
  }

  private void pollMessages(Consumer<Long, String> kafkaConsumer) {
    try {
      while (true) {
        try {
          final ConsumerRecords<Long, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(1000));
          for(ConsumerRecord<Long, String> cr : consumerRecords) {
            log.error("Consumer Record:({}, {}, {}, {})", cr.key(), cr.value(), cr.partition(), cr.offset());

            log.info("Headers:");
            for (Header header : cr.headers()) {
              log.info("Key: {}, Value: {}", header.key(), new String(header.value()));
            }

            // without workaround
            exampleEventConsumer.accept(cr.value());

            // with workaround
            /* uncomment this and remove the other to use the workaround
            Context context = getTraceParent(cr);
            try (Scope scope = context.makeCurrent()) {
              exampleEventConsumer.accept(cr.value());
            }
            */
          }
          kafkaConsumer.commitAsync();
        } catch (Exception e) {
          log.error("got some error: ", e);
        }
      }
    } catch (CommitFailedException e) {
      log.error("CommitFailedException", e);
    } finally {
      kafkaConsumer.close();
    }
  }

  /**
   * Get trace context from consumer record
   */
  private Context getTraceParent(ConsumerRecord<Long, String> consumerRecord) {
    var textMapGetter = new TextMapGetter<ConsumerRecord<Long, String>>() {
      @Override
      public Iterable<String> keys(ConsumerRecord carrier) {
        return Collections.singleton("traceparent");
      }

      @Override
      public String get(ConsumerRecord carrier, String key) {
        Header header = carrier.headers().lastHeader(key);
        return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
      }
    };

    return W3CTraceContextPropagator
        .getInstance()
        .extract(Context.root(), consumerRecord, textMapGetter);
  }
}
