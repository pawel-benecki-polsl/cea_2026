package pl.polsl.inf.cea.reservation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Seat {

    @Id
    @GeneratedValue
    private Long id;

    private String label;

    @Enumerated(EnumType.STRING)
    private SeatStatus status = SeatStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " event_id ")
    @JsonIgnore
    private Event event;

    @Version
    Long version;

    public Seat() {}

    public Seat(String label, Event event) {
        this.label = label;
        this.event = event;
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
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
