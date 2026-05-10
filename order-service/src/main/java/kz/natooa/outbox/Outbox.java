package kz.natooa.outbox;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "outbox", indexes = {
        @Index(name = "idx_outbox_status_created", columnList = "status, created_at")
})
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // Outbox row id

    @Column(unique = true, nullable = false)
    private String eventId; // event Id

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType; // Event type (OrderCreatedEvent, OrderCompletedEvent, ...)

    @Column(nullable = false)
    private String aggregateId; // Order id

    @Lob
    private String payload; // JSON full event snapshot

    @Enumerated(EnumType.STRING)
    private OutboxStatus status; // Status (PENDING/SENT)

    @Column(name = "created_at")
    private Instant createdAt; // createdAt
}
