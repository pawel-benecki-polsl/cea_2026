package pl.polsl.inf.cea.reservation.exception;

public class SeatNotAvailableException extends RuntimeException {
    public SeatNotAvailableException(Long seatId) {
        super("Seat not available: " + seatId);
    }
}
