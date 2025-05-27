package com.example.inventory.service;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import ch.qos.logback.classic.Logger;

public class KafkaConsumerService {

		@Autowired
	    private KafkaTemplate<String, String> kafkaTemplate;

	    private static final Logger logger = (Logger) LoggerFactory.getLogger(KafkaConsumerService.class);

	    @KafkaListener(topics = "orders.new", groupId = "inventory-group")
	    public void consume(ConsumerRecord<String, String> record) {

	        String orderData = record.value();
	        Headers headers = record.headers();

	        String correlationId = null;
	        if (headers != null) {
	            Header header = headers.lastHeader("correlation-id");
	            if (header != null) {
	                correlationId = new String(header.value(), StandardCharsets.UTF_8);
	                MDC.put("traceId", correlationId);
	            }
	        }

	        logger.info("Consumed order: {}", orderData);

	        // Respond with inventory status
	        ProducerRecord<String, String> response = new ProducerRecord<>("inventory.status", "INVENTORY_OK");
	        if (correlationId != null) {
	            response.headers().add("correlation-id", correlationId.getBytes(StandardCharsets.UTF_8));
	        }

	        kafkaTemplate.send(response);
	        MDC.clear();
	    }
}
