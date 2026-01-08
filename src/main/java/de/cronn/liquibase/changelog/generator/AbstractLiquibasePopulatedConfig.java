package de.cronn.liquibase.changelog.generator;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration;

@ImportAutoConfiguration({ DataSourceAutoConfiguration.class, LiquibaseAutoConfiguration.class })
public abstract class AbstractLiquibasePopulatedConfig {
}
