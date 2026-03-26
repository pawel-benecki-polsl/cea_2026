package pl.polsl.inf.cea.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.polsl.inf.cea.reservation.domain.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}