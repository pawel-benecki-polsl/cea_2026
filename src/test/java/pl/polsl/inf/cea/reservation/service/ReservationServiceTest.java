package pl.polsl.inf.cea.reservation.service;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.polsl.inf.cea.reservation.compensation.CompensationService;
import pl.polsl.inf.cea.reservation.domain.Event;
import pl.polsl.inf.cea.reservation.domain.Seat;
import pl.polsl.inf.cea.reservation.domain.SeatStatus;
import pl.polsl.inf.cea.reservation.exception.SeatNotAvailableException;
import pl.polsl.inf.cea.reservation.exception.SeatNotFoundException;
import pl.polsl.inf.cea.reservation.outbox.OutboxStatus;
import pl.polsl.inf.cea.reservation.repository.EventRepository;
import pl.polsl.inf.cea.reservation.repository.OutboxEventRepository;
import pl.polsl.inf.cea.reservation.repository.SeatRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ReservationServiceTest {

    @Autowired ReservationService    reservationService;
    @Autowired SeatRepository        seatRepository;
    @Autowired EventRepository       eventRepository;
    @Autowired OutboxEventRepository outboxRepository;

    @MockitoBean
    PaymentService paymentService;

    static class TestConfig {
        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    private Seat createAvailableSeat() {
        Event event = eventRepository.save(new Event("Concert", "Venue"));
        return seatRepository.save(new Seat("A01", event));
    }

    @Test
    void reserve_marksTheSeatAsReserved() {
        // Given
        Seat seat = createAvailableSeat();

        // When
        reservationService.reserve(seat.getId(), "alice");

        // Then
        Seat updated = seatRepository.findById(seat.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(SeatStatus.RESERVED);
    }

    @Test
    void reserve_throwsWhenSeatNotAvailable() {
        // Given
        Seat seat = createAvailableSeat();
        reservationService.reserve(seat.getId(), "alice"); // takes the seat

        // When / Then
        assertThatThrownBy(
                () -> reservationService.reserve(seat.getId(), "bob"))
                .isInstanceOf(SeatNotAvailableException.class);
    }

    @Test
    void reserve_throwsWhenSeatNotFound() {
        assertThatThrownBy(
                () -> reservationService.reserve(999L, "alice"))
                .isInstanceOf(SeatNotFoundException.class);
    }
}
