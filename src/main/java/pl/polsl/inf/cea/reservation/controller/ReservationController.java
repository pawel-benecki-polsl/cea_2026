package pl.polsl.inf.cea.reservation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.polsl.inf.cea.reservation.domain.Reservation;
import pl.polsl.inf.cea.reservation.domain.Seat;
import pl.polsl.inf.cea.reservation.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<Reservation> reserve(@RequestBody ReservationRequest request) {
        Reservation reservation = reservationService.reserve(
                request.seatId(), request.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @GetMapping
    public List<Reservation> retrieveAll() {
        return reservationService.retrieveAllReservations();
    }

    @GetMapping("/availableSeats")
    public List<Seat> getAvailableSeatsForEvent(@RequestParam Long eventId) {
        return reservationService.getAvailableSeatsForEvent(eventId);
    }
}
