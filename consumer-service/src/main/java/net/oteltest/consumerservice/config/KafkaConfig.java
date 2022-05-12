package net.oteltest.consumerservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("kafka")
@Getter
@Setter
public class KafkaConfig {
  private String topic;
  private String clientId;
  private String bootstrapServers;
  private String groupId;
  private String securityProtocol;
  private String saslMechanism;
  private String saslJaasConfig;
}
