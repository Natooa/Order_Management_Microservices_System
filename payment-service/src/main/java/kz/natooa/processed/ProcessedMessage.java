package kz.natooa.processed;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessedMessage {
    @Id
    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;
}
