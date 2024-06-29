package de.condat.app.repository;

import de.condat.app.model.PersonMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface PersonMovementRepository extends JpaRepository<PersonMovement, Long> {

  @Query("SELECT p FROM PersonMovement p WHERE p.time BETWEEN :start AND :end")
  List<PersonMovement> obtainMovementsForAverage(
      @Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

  @Query(
      "SELECT SUM(CASE WHEN p.isEntrance = true THEN p.count ELSE -p.count END) "
          + "FROM PersonMovement p WHERE p.time <= :time")
  Optional<Integer> calculatePersonCountUpToTimestamp(@Param("time") OffsetDateTime time);

  @Query(
      "SELECT SUM(CASE WHEN p.isEntrance = true THEN p.count ELSE -p.count END) "
          + "FROM PersonMovement p")
  Optional<Integer> calculateCurrentPersonCount();
}
