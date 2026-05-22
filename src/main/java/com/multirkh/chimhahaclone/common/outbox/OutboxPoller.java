package com.multirkh.chimhahaclone.common.outbox;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPoller {

    private static final String TOPIC_POST_EVENTS = "post.events.v1";

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${outbox.batch-size:100}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${outbox.poll-interval-ms:5000}")
    @Transactional
    public void publishPending() {
        List<OutboxEvent> pending = outboxEventRepository.findPending(PageRequest.of(0, batchSize));
        if (pending.isEmpty()) {
            return;
        }
        log.info("[outbox] publishing {} events", pending.size());
        for (OutboxEvent event : pending) {
            try {
                String topic = topicFor(event.getAggregateType());
                String key = event.getAggregateId();
                String value = event.getPayload().toString();
                kafkaTemplate.send(topic, key, value).get();
                event.setPublishedAt(Instant.now());
            } catch (Exception ex) {
                log.error("[outbox] publish failed id={} eventType={}", event.getId(), event.getEventType(), ex);
            }
        }
    }

    private String topicFor(String aggregateType) {
        return switch (aggregateType) {
            case "Post" -> TOPIC_POST_EVENTS;
            default -> aggregateType.toLowerCase() + ".events.v1";
        };
    }
}
