package kz.natooa.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public abstract class DomainEvent{
    UUID eventId = UUID.randomUUID();
    Instant timeStamp = Instant.now();
}
