package de.cronn.liquibase.changelog.generator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class OtherEntity {
  @Id private Long id;

  @OneToMany(mappedBy = "other")
  private List<TestEntity> owningEntities;
}
