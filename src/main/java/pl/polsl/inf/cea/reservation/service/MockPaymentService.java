package pl.polsl.inf.cea.reservation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.polsl.inf.cea.reservation.exception.PaymentException;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class MockPaymentService implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(MockPaymentService.class);

    @Value("${payment.failure-rate:20}")
    private int failureRate;

    @Override
    public void charge(String payload) {
        if (ThreadLocalRandom.current().nextInt(100) < failureRate) {
            throw new PaymentException(
                    "Payment declined (simulated, rate=" + failureRate + "%)");
        }
        log.info("Payment charged: {}", payload);
        // Production: replace body with a real HTTP call
    }
}
