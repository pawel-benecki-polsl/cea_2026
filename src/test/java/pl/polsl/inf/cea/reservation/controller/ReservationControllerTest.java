package pl.polsl.inf.cea.reservation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.polsl.inf.cea.reservation.domain.Event;
import pl.polsl.inf.cea.reservation.domain.Seat;
import pl.polsl.inf.cea.reservation.repository.EventRepository;
import pl.polsl.inf.cea.reservation.repository.SeatRepository;
import pl.polsl.inf.cea.reservation.service.PaymentService;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ReservationControllerTest {

    @Autowired MockMvc        mockMvc;
    @Autowired SeatRepository seatRepository;
    @Autowired EventRepository eventRepository;

    @MockitoBean
    PaymentService paymentService;

    @Test
    void postReservation_returns201_whenSeatAvailable() throws Exception {
        Event event = eventRepository.save(new Event("Gig", "Hall"));
        Seat  seat  = seatRepository.save(new Seat("B01", event));

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seatId\":" + seat.getId()
                                + ",\"userId\":\"alice\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void postReservation_returns409_whenSeatAlreadyTaken() throws Exception {
        Event event = eventRepository.save(new Event("Gig", "Hall"));
        Seat  seat  = seatRepository.save(new Seat("B02", event));

        // First reservation succeeds
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seatId\":" + seat.getId()
                                + ",\"userId\":\"alice\"}"))
                .andExpect(status().isCreated());

        // Second one for the same seat should conflict
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seatId\":" + seat.getId()
                                + ",\"userId\":\"bob\"}"))
                .andExpect(status().isConflict());

        verifyNoInteractions(paymentService);
    }
}
