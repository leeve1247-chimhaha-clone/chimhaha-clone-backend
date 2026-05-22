package com.multirkh.chimhahaclone.common.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Phase 0 placeholder. Logs every post.events.v1 message.
 * To be replaced by the notification service consumer in Phase 1.
 */
@Slf4j
@Component
public class TempPostCreatedConsumer {

    @KafkaListener(
        topics = "post.events.v1",
        groupId = "chimhaha-temp-logger",
        properties = {
            "auto.offset.reset=earliest"
        }
    )
    public void onMessage(ConsumerRecord<String, String> record) {
        log.info("[temp-consumer] topic={} partition={} offset={} key={} value={}",
            record.topic(), record.partition(), record.offset(), record.key(), record.value());
    }
}
