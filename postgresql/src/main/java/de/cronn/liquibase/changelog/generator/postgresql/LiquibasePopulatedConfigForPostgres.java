package de.cronn.liquibase.changelog.generator.postgresql;

import de.cronn.liquibase.changelog.generator.AbstractLiquibasePopulatedConfig;

import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:/de/cronn/liquibase/changelog/generator/postgresql/liquibase-populated-postgresql.properties")
public class LiquibasePopulatedConfigForPostgres extends AbstractLiquibasePopulatedConfig {
}
