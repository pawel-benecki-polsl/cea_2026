# Reservation — wykład 5

Zmiany naniesione na projekt zgodnie z wykładem "Live Coding & Testing in Spring Boot:
Outbox Pattern in Practice + Writing Tests That Matter".

## Co przybyło

### Outbox Pattern (`outbox/` + `compensation/`)
- `OutboxStatus` — enum `PENDING / PROCESSED / FAILED`
- `OutboxEvent` — encja z `eventType`, `payload` (JSON jako TEXT), `status`, `createdAt`
- `OutboxEventRepository` — `findByStatus(...)`
- `OutboxProcessor` — `@Scheduled(fixedDelay = 5000)`, woła `PaymentService`, przy
  `PaymentException` ustawia status `FAILED` i odpala kompensację
- `CompensationService` — czyta `seatId` z payloadu i ustawia status miejsca z powrotem
  na `AVAILABLE`
- `SchedulingConfig` — `@EnableScheduling` wydzielone do oddzielnej `@Configuration`
  z `@ConditionalOnProperty("outbox.scheduling.enabled")`, żeby móc wyłączać scheduler
  w testach

### Płatności (`service/`)
- `PaymentService` — interfejs
- `MockPaymentService` — symulator z konfigurowalnym `payment.failure-rate`
  (`@Value("${payment.failure-rate:20}")`)

### Wyjątki domenowe (`exception/`)
- `SeatNotFoundException` → 404 (przez `GlobalExceptionHandler`)
- `SeatNotAvailableException` → 409
- `PaymentException`

### Controller (`controller/`)
- `ReservationController` — POST przyjmuje JSON body (`ReservationRequest`), zwraca
  `201 Created`
- `GlobalExceptionHandler` — `@RestControllerAdvice` mapujący wyjątki na `ProblemDetail`
- `ReservationRequest` — record DTO

### Konfiguracja
- `application.properties` → `application.yml`
- `application-dev.yml` — profil dev (do `--spring.profiles.active=dev`)
- `payment.failure-rate: 20` (główny), zmień na `100` w `application-dev.yml`,
  żeby wymusić ścieżkę kompensacji

### Domena (zmiany istniejących klas)
- `SeatStatus`: `FREE` → `AVAILABLE`, dodany `PAID`
- `Event`: dodane pola `name`, `venue` + konstruktor `Event(name, venue)`
- `Seat`: dodane pole `label` + konstruktor `Seat(label, event)`
- `ReservationService`: sprawdzanie statusu, wyrzucanie wyjątków domenowych,
  zapis do outboxa **w tej samej transakcji**, payload generowany przez `ObjectMapper`

### Testy (`src/test/`)
1. `OutboxEventRepositoryTest` — `@DataJpaTest`, sprawdza zapis pending row
2. `ReservationServiceTest` — `@DataJpaTest` + `@Import`, 4 testy:
   - `reserve_marksTheSeatAsReserved`
   - `reserve_savesOutboxEventInSameTransaction`
   - `reserve_throwsWhenSeatNotAvailable`
   - `reserve_throwsWhenSeatNotFound`
3. `ReservationControllerTest` — `@SpringBootTest` + `MockMvc`:
   - 201 dla wolnego miejsca
   - 409 dla zajętego
4. `OutboxProcessorTest` — `@SpringBootTest` z wyłączonym schedulerem:
   - PROCESSED gdy płatność OK
   - FAILED gdy płatność rzuca
5. `ReservationApplicationTests` — naprawiony test concurrency:
   - `pool.shutdown()` + `doneLatch` zamiast samego `awaitTermination`

## Odstępstwa od slajdów (świadome)

### 1. `ObjectMapper` zamiast sklejania stringów w `ReservationService`
**Slajd**: `"{\"reservationId\":" + r.getId() + ",\"seatId\":" + seatId + "}"`
**Kod**: `objectMapper.writeValueAsString(Map.of("reservationId", ..., "seatId", ...))`

Sklejanie stringów wybucha jak tylko ktoś doda string z apostrofem/znakiem specjalnym.
Skoro `CompensationService` używa `ObjectMapper` do parsowania, używamy go też do tworzenia.

### 2. Podział transakcji w `OutboxProcessor`
**Slajd**: `@Transactional` na całej metodzie `process()`, pętla wewnątrz.
**Kod**: pętla bez transakcji, każdy event w osobnej transakcji przez self-injection
i `@Transactional(propagation = REQUIRES_NEW)`.

Powód: w wersji ze slajdu jeden rzucony niespodziewany wyjątek w środku pętli rolluje
**całą** transakcję, w tym statusy `PROCESSED` ustawione dla wcześniej przetworzonych
eventów. Self-injection (`@Lazy OutboxProcessor self`) gwarantuje, że wywołanie
`self.processOne(...)` przechodzi przez proxy Springa, dzięki czemu `@Transactional`
działa.

### 3. `@EnableScheduling` w osobnej konfiguracji
**Slajd**: w ogóle nie wspomina o `@EnableScheduling`. Bez tej adnotacji `@Scheduled`
**nie działa**. Standardowy błąd, na który nabija się 1/3 studentów.

Wydzielone do `SchedulingConfig` z `@ConditionalOnProperty`, żeby móc wyłączać
w testach (`outbox.scheduling.enabled=false`).

### 4. `ThreadLocalRandom` zamiast pola `new Random()`
**Slajd**: `private final Random random = new Random();`
**Kod**: `ThreadLocalRandom.current().nextInt(...)`

`Random` nie jest bezpieczny do dzielenia między wątkami. `MockPaymentService` jest
singletonem, więc gdy w przyszłości scheduler stanie się `@Async`, wątki uderzą
w to samo pole. Drobiazg, ale czysty.

### 5. `@MockitoBean` zamiast `@MockBean`
**Slajd**: `@MockBean PaymentService paymentService;`
**Kod**: `@MockitoBean PaymentService paymentService;`

Spring Boot 4.0 **usunął** `@MockBean` (był deprecated od 3.4). Trzeba używać
`@MockitoBean` z `org.springframework.test.context.bean.override.mockito`.

## Uruchomienie

```bash
# Tryb deweloperski (failure-rate domyślnie 20%)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# H2 console: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb

# Testy
./mvnw test
```

## Demo z wykładu

### Happy path (failure-rate=20)
```bash
curl -X POST http://localhost:8080/reservations \
  -H "Content-Type: application/json" \
  -d '{"seatId":1,"userId":"alice"}'
```
1. Otwórz H2 console — w `outbox_events` jest wiersz `PENDING`
2. Czekaj 5 sekund — status `PROCESSED`
3. `seats` ma status `RESERVED`

### Failure path
Zmień w `application-dev.yml`:
```yaml
payment:
  failure-rate: 100
```
Restart, ten sam request:
1. Outbox: `PENDING` → `FAILED`
2. `seats`: wraca do `AVAILABLE`
3. W logach: "Compensation: seat 1 released"

## Co warto byłoby dorobić (na wykład 6 / studentom)

- pole `attempts` w `OutboxEvent` + max retry + backoff
- `SELECT ... FOR UPDATE SKIP LOCKED` przy odczycie pending events (gdy wiele instancji
  aplikacji)
- migracje (Flyway) — w produkcji `ddl-auto=create-drop` to katastrofa
- realna implementacja `PaymentService` (RestClient z `@HttpExchange` w SB 4)
