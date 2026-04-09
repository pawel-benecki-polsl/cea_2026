package pl.polsl.inf.cea.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.polsl.inf.cea.reservation.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
