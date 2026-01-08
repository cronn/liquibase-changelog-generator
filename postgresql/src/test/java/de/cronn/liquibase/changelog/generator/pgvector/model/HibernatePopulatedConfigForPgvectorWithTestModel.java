package de.cronn.liquibase.changelog.generator.pgvector.model;

import org.springframework.boot.persistence.autoconfigure.EntityScan;

import de.cronn.liquibase.changelog.generator.pgvector.HibernatePopulatedConfigForPgVector;

@EntityScan("de.cronn.liquibase.changelog.generator.pgvector.model")
public class HibernatePopulatedConfigForPgvectorWithTestModel extends HibernatePopulatedConfigForPgVector {
}
