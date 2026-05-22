package com.multirkh.chimhahaclone.common.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OutboxEventPublisherTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private OutboxEventPublisher publisher;

    private record DummyPayload(String postId, String title) {}

    @Test
    @DisplayName("publish() persists OutboxEvent with serialized payload")
    void publishPersistsOutboxRow() {
        when(outboxEventRepository.save(any(OutboxEvent.class))).thenAnswer(inv -> inv.getArgument(0));

        publisher.publish("Post", "42", "PostCreated", new DummyPayload("42", "hello"));

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(captor.capture());

        OutboxEvent saved = captor.getValue();
        assertThat(saved.getAggregateType()).isEqualTo("Post");
        assertThat(saved.getAggregateId()).isEqualTo("42");
        assertThat(saved.getEventType()).isEqualTo("PostCreated");
        assertThat(saved.getPayload().get("postId").asText()).isEqualTo("42");
        assertThat(saved.getPayload().get("title").asText()).isEqualTo("hello");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getPublishedAt()).isNull();
    }
}
