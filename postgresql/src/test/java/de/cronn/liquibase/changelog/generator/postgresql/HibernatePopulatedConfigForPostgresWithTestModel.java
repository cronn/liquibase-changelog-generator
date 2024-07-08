package de.cronn.liquibase.changelog.generator.postgresql;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("de.cronn.liquibase.changelog.generator.model")
public class HibernatePopulatedConfigForPostgresWithTestModel extends HibernatePopulatedConfigForPostgres {
}
