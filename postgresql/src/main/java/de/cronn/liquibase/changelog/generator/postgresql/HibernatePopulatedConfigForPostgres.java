package de.cronn.liquibase.changelog.generator.postgresql;

import org.springframework.context.annotation.PropertySource;

import de.cronn.liquibase.changelog.generator.AbstractHibernatePopulatedConfig;

@PropertySource("classpath:/de/cronn/liquibase/changelog/generator/postgresql/hibernate-populated-postgresql.properties")
public abstract class HibernatePopulatedConfigForPostgres extends AbstractHibernatePopulatedConfig {
}
