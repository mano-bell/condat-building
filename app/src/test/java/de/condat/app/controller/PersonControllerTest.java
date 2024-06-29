package de.condat.app.controller;

import de.condat.app.exception.CountException;
import de.condat.app.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersonControllerTest {

  @Mock private PersonService personService;

  @InjectMocks private PersonController personController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testAverage_success() {
    OffsetDateTime start = OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    OffsetDateTime end = OffsetDateTime.of(2023, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC);
    long averageCount = 5L;

    when(personService.average(start, end)).thenReturn(averageCount);

    ResponseEntity<String> response = personController.average(start, end);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Average: 5", response.getBody());
    verify(personService, times(1)).average(start, end);
  }

  @Test
  void testAverage_endBeforeStart() {
    OffsetDateTime start = OffsetDateTime.of(2023, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC);
    OffsetDateTime end = OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

    ConstraintViolationException exception =
        assertThrows(
            ConstraintViolationException.class,
            () -> {
              personController.average(start, end);
            });

    assertEquals("End date can not be before start date", exception.getMessage());
  }

  @Test
  void testCount_success() {
    OffsetDateTime time = OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    int count = 10;

    when(personService.count(time)).thenReturn(count);

    ResponseEntity<String> response = personController.count(time);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Count: 10", response.getBody());
    verify(personService, times(1)).count(time);
  }

  @Test
  void testEnter_success() {
    int count = 5;

    doNothing().when(personService).enter(count);

    ResponseEntity<String> response = personController.enter(count);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Number of people has been updated", response.getBody());
    verify(personService, times(1)).enter(count);
  }

  @Test
  void testEnter_countException() {
    int count = 5;

    doThrow(new CountException()).when(personService).enter(count);

    ResponseEntity<String> response = personController.enter(count);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Maximum capacity exceeded", response.getBody());
    verify(personService, times(1)).enter(count);
  }

  @Test
  void testLeave_success() {
    int count = 5;

    doNothing().when(personService).leave(count);

    ResponseEntity<String> response = personController.leave(count);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Number of people has been updated", response.getBody());
    verify(personService, times(1)).leave(count);
  }

  @Test
  void testLeave_countException() {
    int count = 5;

    doThrow(new CountException()).when(personService).leave(count);

    ResponseEntity<String> response = personController.leave(count);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals(
        "Number of people to leave exceeds the current number in the building", response.getBody());
    verify(personService, times(1)).leave(count);
  }
}
