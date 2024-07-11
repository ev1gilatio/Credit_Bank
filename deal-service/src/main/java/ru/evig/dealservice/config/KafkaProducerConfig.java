package ru.evig.dealservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.evig.dealservice.dto.EmailMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${bootstrapUrl}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin admin()
    {
        Map<String, Object> configs = new HashMap<>();

        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);

        return new KafkaAdmin(configs);
    }

    @Bean
    public ProducerFactory<String, EmailMessage> producerFactory() {
        Map<String, Object> configs = new HashMap<>();

        configs.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );
        configs.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );
        configs.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, EmailMessage> kafkaTemplate() {

        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic finishRegistrationTopic() {

        return TopicBuilder
                .name("finish-registration")
                .build();
    }

    @Bean
    public NewTopic createDocumentsTopic() {

        return TopicBuilder
                .name("create-documents")
                .build();
    }

    @Bean
    public NewTopic sendDocumentsTopic() {

        return TopicBuilder
                .name("send-documents")
                .build();
    }

    @Bean
    public NewTopic sendSesTopic() {

        return TopicBuilder
                .name("send-ses")
                .build();
    }

    @Bean
    public NewTopic creditIssuedTopic() {

        return TopicBuilder
                .name("credit-issued")
                .build();
    }

    @Bean
    public NewTopic statementDeniedTopic() {

        return TopicBuilder
                .name("statement-denied")
                .build();
    }
}
