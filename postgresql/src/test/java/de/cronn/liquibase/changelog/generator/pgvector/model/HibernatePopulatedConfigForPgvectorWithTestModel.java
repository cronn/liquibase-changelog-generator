package de.cronn.liquibase.changelog.generator.pgvector.model;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import de.cronn.liquibase.changelog.generator.pgvector.HibernatePopulatedConfigForPgVector;

@EntityScan("de.cronn.liquibase.changelog.generator.pgvector.model")
public class HibernatePopulatedConfigForPgvectorWithTestModel extends HibernatePopulatedConfigForPgVector {
}
