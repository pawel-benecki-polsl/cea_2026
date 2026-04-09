package pl.polsl.inf.cea.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.polsl.inf.cea.reservation.domain.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
