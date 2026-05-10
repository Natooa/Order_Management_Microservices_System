package kz.natooa.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Long> {
    @Query(value = """
        SELECT * FROM outbox
        WHERE status = :status
        ORDER BY created_at
        FOR UPDATE SKIP LOCKED
        LIMIT :limit
    """, nativeQuery = true)
    List<Outbox> findByStatus(@Param("status") String status, @Param("limit") int limit);
}
