package de.cronn.liquibase.changelog.generator.model;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class EntityWithEnum1 {
	@Id
	private Long id;

	@JdbcType(PostgreSQLEnumJdbcType.class)
	private Count count;

	@JdbcType(PostgreSQLEnumJdbcType.class)
	private Size size;
}
