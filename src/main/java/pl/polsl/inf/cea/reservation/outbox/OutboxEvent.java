package pl.polsl.inf.cea.reservation.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    @GeneratedValue
    private Long id;

    private String eventType;          // e.g. "SEAT_RESERVED"

    @Column(columnDefinition = "TEXT")
    private String payload;            // JSON payload

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private LocalDateTime createdAt;

    protected OutboxEvent() {
        // for JPA
    }

    public static OutboxEvent pending(String type, String payload) {
        OutboxEvent e = new OutboxEvent();
        e.eventType = type;
        e.payload   = payload;
        e.status    = OutboxStatus.PENDING;
        e.createdAt = LocalDateTime.now();
        return e;
    }

    public Long getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
