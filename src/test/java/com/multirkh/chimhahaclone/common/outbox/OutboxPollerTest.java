package com.multirkh.chimhahaclone.common.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OutboxPollerTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private OutboxPoller poller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setBatchSize() {
        ReflectionTestUtils.setField(poller, "batchSize", 100);
    }

    private OutboxEvent newPostEvent(String id, String title) {
        ObjectNode payload = objectMapper.createObjectNode().put("postId", id).put("title", title);
        OutboxEvent ev = new OutboxEvent("Post", id, "PostCreated", payload);
        ReflectionTestUtils.setField(ev, "id", Long.parseLong(id));
        return ev;
    }

    private CompletableFuture<SendResult<String, String>> successfulSend() {
        RecordMetadata meta = new RecordMetadata(new TopicPartition("post.events.v1", 0), 0L, 0, 0L, 0, 0);
        SendResult<String, String> result = new SendResult<>(null, meta);
        return CompletableFuture.completedFuture(result);
    }

    @Test
    @DisplayName("nothing to publish → KafkaTemplate is never called")
    void emptyPendingSkipsKafka() {
        when(outboxEventRepository.findPending(any(PageRequest.class))).thenReturn(List.of());

        poller.publishPending();

        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    @DisplayName("pending Post events → publish to post.events.v1 with aggregateId as key and mark publishedAt")
    void pendingEventsArePublishedAndMarked() {
        OutboxEvent event = newPostEvent("42", "hi");
        when(outboxEventRepository.findPending(any(PageRequest.class))).thenReturn(List.of(event));
        when(kafkaTemplate.send(eq("post.events.v1"), eq("42"), anyString())).thenReturn(successfulSend());

        poller.publishPending();

        verify(kafkaTemplate).send("post.events.v1", "42", event.getPayload().toString());
        assertThat(event.getPublishedAt()).isNotNull();
    }

    @Test
    @DisplayName("Kafka send failure → row stays unpublished and subsequent rows still attempted")
    void publishFailureLeavesRowPending() {
        OutboxEvent failing = newPostEvent("1", "boom");
        OutboxEvent succeeding = newPostEvent("2", "ok");
        when(outboxEventRepository.findPending(any(PageRequest.class))).thenReturn(List.of(failing, succeeding));
        CompletableFuture<SendResult<String, String>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("kafka down"));
        when(kafkaTemplate.send(eq("post.events.v1"), eq("1"), anyString())).thenReturn(failed);
        when(kafkaTemplate.send(eq("post.events.v1"), eq("2"), anyString())).thenReturn(successfulSend());

        poller.publishPending();

        assertThat(failing.getPublishedAt()).isNull();
        assertThat(succeeding.getPublishedAt()).isNotNull();
    }

    @Test
    @DisplayName("unknown aggregateType routes to {type}.events.v1 with lowercase prefix")
    void unknownAggregateTypeRoutesByConvention() {
        ObjectNode payload = objectMapper.createObjectNode().put("commentId", "7");
        OutboxEvent commentEvent = new OutboxEvent("Comment", "7", "CommentCreated", payload);
        ReflectionTestUtils.setField(commentEvent, "id", 7L);
        when(outboxEventRepository.findPending(any(PageRequest.class))).thenReturn(List.of(commentEvent));
        when(kafkaTemplate.send(eq("comment.events.v1"), eq("7"), anyString())).thenReturn(successfulSend());

        poller.publishPending();

        verify(kafkaTemplate).send("comment.events.v1", "7", commentEvent.getPayload().toString());
        verify(kafkaTemplate, never()).send(eq("post.events.v1"), anyString(), anyString());
    }
}
