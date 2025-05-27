package com.order.service.service;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;

@Service
public class KafkaProducerService {

	 @Autowired
	 private KafkaTemplate<String, String> kafkaTemplate;
	 
	 public void sendOrder(String orderJson) {
		    ProducerRecord<String, String> record = new ProducerRecord<>("orders.new", orderJson);

		    // Use OpenTelemetry API to get current span context
		    Span currentSpan = Span.current();
		    SpanContext context = currentSpan.getSpanContext();

		    if (context.isValid()) {
		        String traceId = context.getTraceId();
		        record.headers().add("correlation-id", traceId.getBytes(StandardCharsets.UTF_8));
		    } else {
		        System.out.println("Trace ID is invalid — defaulting to none");
		    }

		    kafkaTemplate.send(record);
	}

}
