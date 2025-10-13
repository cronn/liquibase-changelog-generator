package de.cronn.liquibase.changelog.generator.pgvector.model;

import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(indexes = @Index(name = "idx_vector_data", columnList = "vector_data"))
public class EntityWithVector {
	@Id
	private Long id;

	@Column
	@JdbcTypeCode(SqlTypes.VECTOR)
	@Array(length = 768)
	private float[] vectorData;
}
