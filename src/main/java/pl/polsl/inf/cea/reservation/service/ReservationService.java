package pl.polsl.inf.cea.reservation.service;

import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.polsl.inf.cea.reservation.domain.Reservation;
import pl.polsl.inf.cea.reservation.domain.Seat;
import pl.polsl.inf.cea.reservation.domain.SeatStatus;
import pl.polsl.inf.cea.reservation.exception.SeatNotAvailableException;
import pl.polsl.inf.cea.reservation.exception.SeatNotFoundException;
import pl.polsl.inf.cea.reservation.outbox.OutboxEvent;
import pl.polsl.inf.cea.reservation.repository.OutboxEventRepository;
import pl.polsl.inf.cea.reservation.repository.ReservationRepository;
import pl.polsl.inf.cea.reservation.repository.SeatRepository;

import java.util.List;
import java.util.Map;

@Service
public class ReservationService {

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public ReservationService(SeatRepository seatRepository,
                              ReservationRepository reservationRepository,
                              OutboxEventRepository outboxRepository,
                              ObjectMapper objectMapper) {
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Reservation reserve(Long seatId, String userId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new SeatNotAvailableException(seatId);
        }

        seat.setStatus(SeatStatus.RESERVED);

        Reservation reservation = reservationRepository.save(
                new Reservation(seat, userId));

        // Zapisane w TEJ SAMEJ transakcji - atomowe z rezerwacja.
        // Jesli proces padnie tutaj, oba zapisy zostana wycofane razem.
        outboxRepository.save(OutboxEvent.pending(
                "SEAT_RESERVED",
                buildPayload(reservation.getId(), seatId)));

        return reservation;
    }

    public List<Reservation> retrieveAllReservations() {
        return reservationRepository.findAll();
    }

    public List<Seat> getAvailableSeatsForEvent(Long eventId) {
        return seatRepository.findByEventIdAndStatus(eventId, SeatStatus.AVAILABLE);
    }

    private String buildPayload(Long reservationId, Long seatId) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "reservationId", reservationId,
                    "seatId", seatId
            ));
        } catch (Exception e) {
            // Map z Longami zawsze sie serializuje - to nie powinno sie zdarzyc.
            throw new IllegalStateException("Failed to serialize outbox payload", e);
        }
    }
}
