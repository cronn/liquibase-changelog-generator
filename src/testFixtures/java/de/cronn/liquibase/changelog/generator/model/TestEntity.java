package de.cronn.liquibase.changelog.generator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class TestEntity {
	@Id
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	private String description;

	@ManyToOne(optional = false)
	private OtherEntity other;
}
