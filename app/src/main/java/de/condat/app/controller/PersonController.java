package de.condat.app.controller;

import de.condat.app.exception.CountException;
import de.condat.app.service.PersonService;
import de.condat.api.PersonControllerApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.HashSet;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PersonController implements PersonControllerApi {
  private final PersonService personService;

  @Override
  public ResponseEntity<String> average(OffsetDateTime start, OffsetDateTime end) {
    if (end.isBefore(start)) {
      throw new ConstraintViolationException(
          "End date can not be before start date", new HashSet<>());
    }
    return ResponseEntity.ok("Average: " + personService.average(start, end));
  }

  @Override
  public ResponseEntity<String> count(OffsetDateTime time) {
    return ResponseEntity.ok("Count: " + personService.count(time));
  }

  @Override
  public ResponseEntity<String> enter(Integer count) {
    try {
      personService.enter(count);
      return ResponseEntity.ok("Number of people has been updated");
    } catch (CountException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Maximum capacity exceeded");
    }
  }

  @Override
  public ResponseEntity<String> leave(Integer count) {
    try {
      personService.leave(count);
      return ResponseEntity.ok("Number of people has been updated");
    } catch (CountException se) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body("Number of people to leave exceeds the current number in the building");
    }
  }
}
