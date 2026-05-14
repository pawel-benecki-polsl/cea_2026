package pl.polsl.inf.cea.reservation.compensation;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.polsl.inf.cea.reservation.domain.SeatStatus;
import pl.polsl.inf.cea.reservation.repository.SeatRepository;
import tools.jackson.databind.ObjectMapper;

@Service
public class CompensationService {

    private static final Logger log = LoggerFactory.getLogger(CompensationService.class);

    private final SeatRepository seatRepository;
    private final ObjectMapper objectMapper;

    public CompensationService(SeatRepository seatRepository, ObjectMapper objectMapper) {
        this.seatRepository = seatRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void releaseSeat(String payload) {
        try {
            JsonNode json   = objectMapper.readTree(payload);
            Long     seatId = json.get("seatId").asLong();

            seatRepository.findById(seatId).ifPresentOrElse(
                    seat -> {
                        seat.setStatus(SeatStatus.AVAILABLE);
                        log.info("Compensation: seat {} released", seatId);
                    },
                    () -> log.warn("Seat {} not found for compensation", seatId)
            );
        } catch (Exception e) {
            // Do NOT rethrow: a failing compensation needs manual handling,
            // not an infinite retry loop.
            log.error("Compensation failed for payload: {}", payload, e);
        }
    }
}
