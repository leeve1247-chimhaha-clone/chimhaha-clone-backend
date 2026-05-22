package com.multirkh.chimhahaclone.common.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Persists an event row in the same DB transaction as the aggregate write.
 * OutboxPoller picks it up asynchronously and publishes to Kafka.
 */
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void publish(String aggregateType, String aggregateId, String eventType, Object payload) {
        JsonNode payloadNode = objectMapper.valueToTree(payload);
        outboxEventRepository.save(new OutboxEvent(aggregateType, aggregateId, eventType, payloadNode));
    }
}
