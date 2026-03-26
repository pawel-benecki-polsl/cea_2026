package pl.polsl.inf.cea.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.polsl.inf.cea.reservation.domain.Reservation;
import pl.polsl.inf.cea.reservation.domain.Seat;
import pl.polsl.inf.cea.reservation.domain.SeatStatus;
import pl.polsl.inf.cea.reservation.repository.ReservationRepository;
import pl.polsl.inf.cea.reservation.repository.SeatRepository;

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
}