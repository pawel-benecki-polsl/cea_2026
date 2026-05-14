package pl.polsl.inf.cea.reservation.outbox;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.polsl.inf.cea.reservation.exception.PaymentException;
import pl.polsl.inf.cea.reservation.repository.OutboxEventRepository;
import pl.polsl.inf.cea.reservation.service.PaymentService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

/**
 * Test OutboxProcessora.
 *
 * Wylaczamy scheduler (outbox.scheduling.enabled=false), zeby @Scheduled
 * nie odpalal sie sam w tle podczas testu - inaczej mielibysmy race condition
 * miedzy automatyczna inwokacja a recznym wywolaniem process().
 *
 * Mozemy wtedy w pelni kontrolowac kiedy procesor sie uruchamia.
 */
@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties = "outbox.scheduling.enabled=false")
class OutboxProcessorTest {

    @Autowired OutboxProcessor       outboxProcessor;
    @Autowired OutboxEventRepository outboxRepository;

    @MockitoBean
    PaymentService paymentService;

    @Test
    void process_marksEventAsProcessed_whenPaymentSucceeds() {
        // Given
        doNothing().when(paymentService).charge(anyString());
        outboxRepository.save(
                OutboxEvent.pending("SEAT_RESERVED", "{\"seatId\":1}"));

        // When: bezposrednie wywolanie - nie czekamy na @Scheduled
        outboxProcessor.process();

        // Then
        assertThat(outboxRepository.findByStatus(OutboxStatus.PROCESSED))
                .hasSize(1);
        assertThat(outboxRepository.findByStatus(OutboxStatus.PENDING))
                .isEmpty();
    }

    @Test
    void process_marksEventAsFailed_whenPaymentThrows() {
        // Given
        doThrow(new PaymentException("simulated"))
                .when(paymentService).charge(anyString());
        outboxRepository.save(
                OutboxEvent.pending("SEAT_RESERVED", "{\"seatId\":1}"));

        // When
        outboxProcessor.process();

        // Then
        assertThat(outboxRepository.findByStatus(OutboxStatus.FAILED))
                .hasSize(1);
    }
}
