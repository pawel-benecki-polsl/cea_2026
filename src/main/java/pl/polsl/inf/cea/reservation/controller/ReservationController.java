package pl.polsl.inf.cea.reservation.controller;

import org.springframework.web.bind.annotation.*;
import pl.polsl.inf.cea.reservation.ReservationApplication;
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
    public Reservation reserve(@RequestParam Long seatId,
                               @RequestParam String userId) {
        return reservationService.reserve(seatId, userId);
    }


    @GetMapping
    public List<Reservation> retrieveAll() {
        return reservationService.retrieveAllReservations();
    }

    @GetMapping("/freeSeats")
    public List<Seat> getFreeSeatsForEvent(Long eventId) {
        return reservationService.getFreeSeatsForEvent(eventId);
    }
}