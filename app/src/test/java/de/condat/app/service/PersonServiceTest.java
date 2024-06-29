package de.condat.app.service;

import de.condat.app.exception.CountException;
import de.condat.app.model.PersonMovement;
import de.condat.app.repository.PersonMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersonServiceTest {

  @Mock private PersonMovementRepository repository;

  @InjectMocks private PersonService personService;

  private final int maxCapacity = 100;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(personService, "maxCapacity", maxCapacity);
  }

  @Test
  void testEnter() {
    when(repository.calculateCurrentPersonCount()).thenReturn(Optional.of(10));

    personService.enter(5);

    verify(repository, times(1)).save(any(PersonMovement.class));
  }

  @Test
  void testEnterExceedsCapacity() {
    when(repository.calculateCurrentPersonCount()).thenReturn(Optional.of(95));

    assertThrows(CountException.class, () -> personService.enter(10));
  }

  @Test
  void testLeave() {
    when(repository.calculateCurrentPersonCount()).thenReturn(Optional.of(10));

    personService.leave(5);

    verify(repository, times(1)).save(any(PersonMovement.class));
  }

  @Test
  void testLeaveExceedsCurrentCount() {
    when(repository.calculateCurrentPersonCount()).thenReturn(Optional.of(5));

    assertThrows(CountException.class, () -> personService.leave(10));
  }

  @Test
  void testCount() {
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    when(repository.calculatePersonCountUpToTimestamp(now)).thenReturn(Optional.of(10));

    int count = personService.count(now);

    assertEquals(10, count);
  }

  @Test
  void testCurrentCount() {
    when(repository.calculateCurrentPersonCount()).thenReturn(Optional.of(15));

    int count = personService.count(null);

    assertEquals(15, count);
  }

  @Test
  void testAverage() {
    OffsetDateTime start = OffsetDateTime.of(2023, 1, 1, 10, 0, 0, 0, ZoneOffset.UTC);
    OffsetDateTime end = OffsetDateTime.of(2023, 1, 1, 14, 0, 0, 0, ZoneOffset.UTC);

    PersonMovement movement1 = new PersonMovement(5, start.plusHours(1), true);
    PersonMovement movement2 = new PersonMovement(3, start.plusHours(2), false);
    PersonMovement movement3 = new PersonMovement(7, start.plusHours(3), true);

    List<PersonMovement> movements = Arrays.asList(movement1, movement2, movement3);

    when(repository.obtainMovementsForAverage(start, end)).thenReturn(movements);
    when(repository.calculatePersonCountUpToTimestamp(start.plusHours(1)))
        .thenReturn(Optional.of(5));
    when(repository.calculatePersonCountUpToTimestamp(start.plusHours(2)))
        .thenReturn(Optional.of(2));
    when(repository.calculatePersonCountUpToTimestamp(start.plusHours(3)))
        .thenReturn(Optional.of(9));

    long average = personService.average(start, end);

    // Average calculation for the given test case
    long expectedAverage = (5 * 3600000L + 2 * 3600000L + 9 * 3600000L) / 14400000L;
    assertEquals(expectedAverage, average);
  }
}
