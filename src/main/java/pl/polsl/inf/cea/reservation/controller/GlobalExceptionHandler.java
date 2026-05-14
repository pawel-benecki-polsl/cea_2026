package pl.polsl.inf.cea.reservation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.polsl.inf.cea.reservation.exception.SeatNotAvailableException;
import pl.polsl.inf.cea.reservation.exception.SeatNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SeatNotFoundException.class)
    public ProblemDetail handleSeatNotFound(SeatNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SeatNotAvailableException.class)
    public ProblemDetail handleSeatNotAvailable(SeatNotAvailableException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }
}
