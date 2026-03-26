package pl.polsl.inf.cea.reservation.controller;

import org.springframework.web.bind.annotation.*;
import pl.polsl.inf.cea.reservation.domain.Reservation;
import pl.polsl.inf.cea.reservation.service.ReservationService;

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
}