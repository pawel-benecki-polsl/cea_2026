package pl.polsl.inf.cea.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.polsl.inf.cea.reservation.domain.Seat;
import pl.polsl.inf.cea.reservation.domain.SeatStatus;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByEventIdAndStatus(Long eventIt, SeatStatus status);
}
