package de.cronn.liquibase.changelog.generator.pgvector.model;

import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class EntityWithVector {
	@Id
	private Long id;

	@Column
	@JdbcTypeCode(SqlTypes.VECTOR)
	@Array(length = 768)
	private float[] vectorData;
}
