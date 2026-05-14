package pl.polsl.inf.cea.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.polsl.inf.cea.reservation.outbox.OutboxEvent;
import pl.polsl.inf.cea.reservation.outbox.OutboxStatus;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByStatus(OutboxStatus status);
}
