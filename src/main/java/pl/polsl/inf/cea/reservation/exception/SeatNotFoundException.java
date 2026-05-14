package pl.polsl.inf.cea.reservation.exception;

public class SeatNotFoundException extends RuntimeException {
    public SeatNotFoundException(Long seatId) {
        super("Seat not found: " + seatId);
    }
}
