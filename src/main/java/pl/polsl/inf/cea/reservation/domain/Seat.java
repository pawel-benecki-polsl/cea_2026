package pl.polsl.inf.cea.reservation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Seat {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private SeatStatus status = SeatStatus.FREE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " event_id ")
    @JsonIgnore
    private Event event;

    public Long getId() {
        return id;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
