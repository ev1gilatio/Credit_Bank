package ru.evig.dealservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.evig.dealservice.dto.EmailMessage;

@Service
public class KafkaService {
    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;

    public KafkaService(KafkaTemplate<String, EmailMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessageToTopic(String topic, EmailMessage message) {
        kafkaTemplate.send(topic, message);
    }
}
