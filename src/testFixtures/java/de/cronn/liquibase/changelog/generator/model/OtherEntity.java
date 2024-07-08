package de.cronn.liquibase.changelog.generator.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class OtherEntity {
	@Id
	private Long id;

	@OneToMany(mappedBy = "other")
	private List<TestEntity> owningEntities;
}
