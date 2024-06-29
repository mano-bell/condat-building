package de.condat.app.service;

import de.condat.app.exception.CountException;
import de.condat.app.model.PersonMovement;
import de.condat.app.repository.PersonMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {
  private final PersonMovementRepository repository;

  @Value("${person.max-capacity}")
  private int maxCapacity;

  public void enter(int count) {
    int currentCount = repository.calculateCurrentPersonCount().orElse(0);
    if (currentCount + count > maxCapacity) {
      throw new CountException();
    }
    repository.save(new PersonMovement(count, OffsetDateTime.now(ZoneOffset.UTC), true));
  }

  public void leave(int count) {
    int currentCount = repository.calculateCurrentPersonCount().orElse(0);
    if (count > currentCount) {
      throw new CountException();
    }
    repository.save(new PersonMovement(count, OffsetDateTime.now(ZoneOffset.UTC), false));
  }

  public int count(OffsetDateTime time) {
    if (time == null) {
      return repository.calculateCurrentPersonCount().orElse(0);
    }
    return repository.calculatePersonCountUpToTimestamp(time).orElse(0);
  }

  public long average(OffsetDateTime start, OffsetDateTime end) {
    long average = 0;
    List<PersonMovement> movements = repository.obtainMovementsForAverage(start, end);

    if (movements != null && !movements.isEmpty()) {
      OffsetDateTime lastTimestamp = start;
      OffsetDateTime nextTimestamp;
      long sum = 0;

      for (PersonMovement movement : movements) {
        nextTimestamp = movement.getTime();
        int lastCurrentCount = count(nextTimestamp);

        sum += Duration.between(lastTimestamp, nextTimestamp).toMillis() * lastCurrentCount;
        lastTimestamp = nextTimestamp;
      }

      long totalPeriod = Duration.between(start, end).toMillis();
      average = sum / totalPeriod;
    }

    return average;
  }
}
