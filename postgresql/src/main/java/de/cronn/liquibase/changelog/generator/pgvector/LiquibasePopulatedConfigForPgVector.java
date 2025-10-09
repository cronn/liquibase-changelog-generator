package de.cronn.liquibase.changelog.generator.pgvector;

import org.springframework.context.annotation.PropertySource;

import de.cronn.liquibase.changelog.generator.AbstractLiquibasePopulatedConfig;

@PropertySource("classpath:/de/cronn/liquibase/changelog/generator/pgvector/liquibase-populated-pgvector.properties")
public class LiquibasePopulatedConfigForPgVector extends AbstractLiquibasePopulatedConfig {
}
