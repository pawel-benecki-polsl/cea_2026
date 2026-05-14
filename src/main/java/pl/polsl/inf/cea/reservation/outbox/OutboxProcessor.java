package pl.polsl.inf.cea.reservation.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.polsl.inf.cea.reservation.compensation.CompensationService;
import pl.polsl.inf.cea.reservation.exception.PaymentException;
import pl.polsl.inf.cea.reservation.repository.OutboxEventRepository;
import pl.polsl.inf.cea.reservation.service.PaymentService;

import java.util.List;

@Component
public class OutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(OutboxProcessor.class);

    private final OutboxEventRepository outboxRepository;
    private final PaymentService paymentService;
    private final CompensationService compensationService;

    private final OutboxProcessor self;

    public OutboxProcessor(OutboxEventRepository outboxRepository,
                           PaymentService paymentService,
                           CompensationService compensationService,
                           @Lazy OutboxProcessor self) {
        this.outboxRepository = outboxRepository;
        this.paymentService = paymentService;
        this.compensationService = compensationService;
        this.self = self;
    }

    @Scheduled(fixedDelay = 5000)
    public void process() {
        List<OutboxEvent> pending =
                outboxRepository.findByStatus(OutboxStatus.PENDING);

        for (OutboxEvent event : pending) {
            try {
                self.processOne(event.getId());
            } catch (Exception unexpected) {
                // Nieoczekiwany blad poza obsluga PaymentException -
                // logujemy i idziemy dalej. Event zostaje PENDING i bedzie
                // probowany ponownie w nastepnym cyklu.
                log.error("Unexpected error processing outbox event {}",
                        event.getId(), unexpected);
            }
        }
    }

    /**
     * Przetwarza pojedynczy event w osobnej transakcji.
     * REQUIRES_NEW gwarantuje izolacje od ewentualnej zewnetrznej transakcji
     * w testach (np. @DataJpaTest).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOne(Long eventId) {
        OutboxEvent event = outboxRepository.findById(eventId).orElseThrow();
        try {
            paymentService.charge(event.getPayload());
            event.setStatus(OutboxStatus.PROCESSED);
        } catch (PaymentException ex) {
            event.setStatus(OutboxStatus.FAILED);
            compensationService.releaseSeat(event.getPayload());
        }
        // Brak explicit save() - dirty checking JPA zalatwia zapis.
    }
}
