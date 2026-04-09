package pl.polsl.inf.cea.reservation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

	@Test
	void contextLoads() {
	}

	@Test
	void twoUsersOneSeat() throws InterruptedException {
		Long seatId = testHelper.createAvailableSeat();
		CountDownLatch latch = new CountDownLatch(1);
		AtomicInteger successes = new AtomicInteger();

		Runnable task = () -> {
            try {
				latch.await();
				reservationService.reserve(seatId, "user-" + Thread.currentThread().getId());
				successes.incrementAndGet();
			} catch (Exception ignored) {}
		};

		ExecutorService pool = Executors.newFixedThreadPool(2);
		pool.submit(task); pool.submit(task);
		latch.countDown();
		pool.awaitTermination(3, TimeUnit.SECONDS);

		// Without locking: successes may be 2
		Assertions.assertThat(successes.get()).isEqualTo(1);
	}

}
