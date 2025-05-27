package com.example.notification.service;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;

@Service
public class NotificationConsumerService {

	private static final Logger logger = (Logger) LoggerFactory.getLogger(NotificationConsumerService.class);

    @KafkaListener(topics = "inventory.status", groupId = "notification-group")
    public void consume(ConsumerRecord<String, String> record) {
        String message = record.value();
        Headers headers = record.headers();

        String correlationId = null;
        if (headers != null) {
            Header header = headers.lastHeader("correlation-id");
            if (header != null) {
                correlationId = new String(header.value(), StandardCharsets.UTF_8);
                MDC.put("traceId", correlationId);
            }
        }

        logger.info("Sending notification for inventory status: {}", message);
        // Simulate notification logic (email/SMS/etc.)

        MDC.clear();
    }
}
