package kz.natooa.events;

import kz.natooa.processed.ProcessedMessage;
import kz.natooa.processed.ProcessedMessageRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DeduplicationService {
    private final ProcessedMessageRepository processedMessageRepository;

    public DeduplicationService(ProcessedMessageRepository processedMessageRepository) {
        this.processedMessageRepository = processedMessageRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean tryMarkAsProcessed(String eventId){
        try {
            processedMessageRepository.save(new ProcessedMessage(eventId, Instant.now()));
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
}
