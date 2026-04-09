package pl.polsl.inf.cea.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.polsl.inf.cea.reservation.ReservationApplication;
import pl.polsl.inf.cea.reservation.domain.Reservation;
import pl.polsl.inf.cea.reservation.domain.Seat;
import pl.polsl.inf.cea.reservation.domain.SeatStatus;
import pl.polsl.inf.cea.reservation.repository.ReservationRepository;
import pl.polsl.inf.cea.reservation.repository.SeatRepository;

import java.util.List;

@Service
public class ReservationService {

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(SeatRepository seatRepository,
                              ReservationRepository reservationRepository) {
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Reservation reserve(Long seatId, String userId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow();

        seat.setStatus(SeatStatus.RESERVED);

        Reservation reservation = new Reservation(seat, userId);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> retrieveAllReservations() {
        return reservationRepository.findAll();
    }

    public List<Seat> getFreeSeatsForEvent(Long eventId) {
        return seatRepository.findByEventIdAndStatus(eventId, SeatStatus.FREE);
    }
}
