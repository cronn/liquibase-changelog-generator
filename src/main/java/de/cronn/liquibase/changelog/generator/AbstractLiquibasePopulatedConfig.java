package de.cronn.liquibase.changelog.generator;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

@ImportAutoConfiguration({ DataSourceAutoConfiguration.class, LiquibaseAutoConfiguration.class })
public abstract class AbstractLiquibasePopulatedConfig {
}
