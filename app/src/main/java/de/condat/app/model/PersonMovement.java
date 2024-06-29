package de.condat.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
public class PersonMovement {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private int count;
  private OffsetDateTime time;
  private boolean isEntrance;

  public PersonMovement(int count, OffsetDateTime time, boolean isEntrance) {
    this.count = count;
    this.time = time;
    this.isEntrance = isEntrance;
  }
}
