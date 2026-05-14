package pl.polsl.inf.cea.reservation.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Event {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String venue;

    @OneToMany(
            mappedBy = "event")
    private List<Seat> seats;

    public Event() {}

    public Event(String name, String venue) {
        this.name = name;
        this.venue = venue;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVenue() {
        return venue;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
