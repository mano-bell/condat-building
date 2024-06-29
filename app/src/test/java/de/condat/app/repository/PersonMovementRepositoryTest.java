package de.condat.app.repository;

import de.condat.app.model.PersonMovement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class PersonMovementRepositoryTest {

  @Autowired private PersonMovementRepository repository;

  private PersonMovement movement1;
  private PersonMovement movement2;
  private PersonMovement movement3;

  @BeforeEach
  void setUp() {
    repository.deleteAll();

    OffsetDateTime time1 = OffsetDateTime.of(2023, 1, 1, 10, 0, 0, 0, ZoneOffset.UTC);
    OffsetDateTime time2 = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
    OffsetDateTime time3 = OffsetDateTime.of(2023, 1, 1, 14, 0, 0, 0, ZoneOffset.UTC);

    movement1 = new PersonMovement(5, time1, true);
    movement2 = new PersonMovement(3, time2, false);
    movement3 = new PersonMovement(7, time3, true);

    repository.save(movement1);
    repository.save(movement2);
    repository.save(movement3);
  }

  @Test
  void testObtainMovementsForAverage() {
    OffsetDateTime start = OffsetDateTime.of(2023, 1, 1, 9, 0, 0, 0, ZoneOffset.UTC);
    OffsetDateTime end = OffsetDateTime.of(2023, 1, 1, 13, 0, 0, 0, ZoneOffset.UTC);

    List<PersonMovement> movements = repository.obtainMovementsForAverage(start, end);

    assertEquals(2, movements.size());
    assertTrue(movements.contains(movement1));
    assertTrue(movements.contains(movement2));
  }

  @Test
  void testCalculatePersonCountUpToTimestamp() {
    OffsetDateTime time = OffsetDateTime.of(2023, 1, 1, 13, 0, 0, 0, ZoneOffset.UTC);

    Optional<Integer> count = repository.calculatePersonCountUpToTimestamp(time);

    assertTrue(count.isPresent());
    assertEquals(2, count.get());
  }

  @Test
  void testCalculateCurrentPersonCount() {
    Optional<Integer> count = repository.calculateCurrentPersonCount();

    assertTrue(count.isPresent());
    assertEquals(9, count.get());
  }
}
