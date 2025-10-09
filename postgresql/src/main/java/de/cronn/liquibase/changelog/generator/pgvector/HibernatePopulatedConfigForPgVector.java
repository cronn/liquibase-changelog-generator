package de.cronn.liquibase.changelog.generator.pgvector;

import org.springframework.context.annotation.PropertySource;

import de.cronn.liquibase.changelog.generator.AbstractHibernatePopulatedConfig;

@PropertySource("classpath:/de/cronn/liquibase/changelog/generator/pgvector/hibernate-populated-pgvector.properties")
public abstract class HibernatePopulatedConfigForPgVector extends AbstractHibernatePopulatedConfig {
}
