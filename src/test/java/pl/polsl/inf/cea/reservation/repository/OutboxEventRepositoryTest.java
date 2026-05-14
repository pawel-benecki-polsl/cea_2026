package pl.polsl.inf.cea.reservation.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import pl.polsl.inf.cea.reservation.outbox.OutboxEvent;
import pl.polsl.inf.cea.reservation.outbox.OutboxStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OutboxEventRepositoryTest {

    @Autowired
    OutboxEventRepository outboxRepository;

    @Test
    void save_persistsPendingOutboxRow() {
        // Given
        OutboxEvent event = OutboxEvent.pending(
                "SEAT_RESERVED",
                "{\"seatId\":1}");

        // When
        outboxRepository.save(event);

        // Then
        List<OutboxEvent> pending =
                outboxRepository.findByStatus(OutboxStatus.PENDING);
        assertThat(pending).hasSize(1);
        assertThat(pending.get(0).getEventType())
                .isEqualTo("SEAT_RESERVED");
    }
}
