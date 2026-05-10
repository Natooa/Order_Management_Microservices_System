package kz.natooa.outbox;

import kz.natooa.events.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@EnableScheduling
public class OrderPollerService {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public OrderPollerService(OutboxRepository outboxRepository, KafkaTemplate kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void pollOutboxMessagesAndPublish(){
        List<Outbox> unprocessedRecords = outboxRepository.findByStatus(OutboxStatus.PENDING.name(), 50);
        log.info("Unprocessed message count={}", unprocessedRecords.size());
        unprocessedRecords.forEach(outbox -> {
            try {
                publish(outbox.getPayload());
                outbox.setStatus(OutboxStatus.SUCCESS);
                outboxRepository.save(outbox);
            }catch (Exception e){
                log.error(e.getMessage());
            }
        });
    }

    private void publish(String payload){
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate
                .send("order-created-events", payload);
        future.whenComplete(
                (result, ex) -> {
                    if (ex != null) {
                        log.error("Unable to sent message={}, error={}", payload, ex.getMessage());
                    } else {
                        log.info("Sent message={}, with offset={}", payload, result.getRecordMetadata().offset());
                    }
                }
        );
    }
}

