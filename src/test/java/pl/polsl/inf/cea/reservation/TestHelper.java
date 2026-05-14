package pl.polsl.inf.cea.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.polsl.inf.cea.reservation.domain.Event;
import pl.polsl.inf.cea.reservation.domain.Seat;
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
        Event event = eventRepository.save(new Event("Test Event", "Test Venue"));
        Seat  seat  = seatRepository.save(new Seat("A01", event));
        return seat.getId();
    }
}
