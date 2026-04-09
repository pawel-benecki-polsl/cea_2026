package pl.polsl.inf.cea.reservation.domain;

import jakarta.persistence.*;

@Entity
public class Reservation {

    @Id
    @GeneratedValue
    private Long id;

    private String userId;

    @ManyToOne
    private Seat seat;

    public Reservation() {}

    public Reservation(Seat seat, String userId) {
        this.seat = seat;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Seat getSeat() {
        return seat;
    }
}
