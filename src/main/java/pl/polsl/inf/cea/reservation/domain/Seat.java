package pl.polsl.inf.cea.reservation.domain;

import jakarta.persistence.*;

@Entity
public class Seat {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private SeatStatus status = SeatStatus.FREE;

    public Long getId() {
        return id;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }
}