package ru.evig.dossierservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.evig.dossierservice.dto.EmailMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${bootstrapUrl}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, EmailMessage> consumerFactory() {
        JsonDeserializer<EmailMessage> deserializer = new JsonDeserializer<>(EmailMessage.class);
        deserializer.addTrustedPackages("*");
        deserializer.ignoreTypeHeaders();

        Map<String, Object> configs = new HashMap<>();

        configs.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        return new DefaultKafkaConsumerFactory<>(configs,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmailMessage> emailMessageKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmailMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}
