package pl.polsl.inf.cea.reservation;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.polsl.inf.cea.reservation.domain.Event;
import pl.polsl.inf.cea.reservation.domain.Seat;
import pl.polsl.inf.cea.reservation.domain.SeatStatus;
import pl.polsl.inf.cea.reservation.repository.EventRepository;
import pl.polsl.inf.cea.reservation.repository.SeatRepository;

@Component
public class TestHelper {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SeatRepository seatRepository;

    @Transactional
    public Long createAvailableSeat() {
        Event event = new Event();
        eventRepository.save(event);

        Seat seat = new Seat();
        seat.setEvent(event);
        seat.setStatus(SeatStatus.FREE);
        seatRepository.save(seat);

        return seat.getId();
    }
}