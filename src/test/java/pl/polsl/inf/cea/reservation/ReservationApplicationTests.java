package pl.polsl.inf.cea.reservation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.polsl.inf.cea.reservation.service.PaymentService;
import pl.polsl.inf.cea.reservation.service.ReservationService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class ReservationApplicationTests {

    @Autowired
    ReservationService reservationService;

    @Autowired
    TestHelper testHelper;

    /**
     * PaymentService podmienione na mock, zeby outbox processor nie probowal
     * dzwonic do losowego "platnika" w trakcie testu i nie zmienial stanu miejsc.
     */
    @MockitoBean
    PaymentService paymentService;

    @Test
    void contextLoads() {
    }

    @Test
    void twoUsers_cannotBothReserveTheSameSeat() throws InterruptedException {
        // Given
        Long seatId = testHelper.createAvailableSeat();
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch  = new CountDownLatch(2);
        AtomicInteger  successes  = new AtomicInteger();

        Runnable task = () -> {
            try {
                startLatch.await();
                reservationService.reserve(seatId,
                        "user-" + Thread.currentThread().getId());
                successes.incrementAndGet();
            } catch (Exception ignored) {
                // expected for the losing thread
            } finally {
                doneLatch.countDown();
            }
        };

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            pool.submit(task);
            pool.submit(task);

            // When: oba watki ruszaja jednoczesnie
            startLatch.countDown();

            // Then: czekamy az oba skoncza, a nie tylko 3 sekundy
            boolean finished = doneLatch.await(5, TimeUnit.SECONDS);
            Assertions.assertThat(finished)
                    .as("Both threads should finish within 5s")
                    .isTrue();
            Assertions.assertThat(successes.get()).isEqualTo(1);
        } finally {
            pool.shutdown();
            pool.awaitTermination(2, TimeUnit.SECONDS);
        }
    }

}
