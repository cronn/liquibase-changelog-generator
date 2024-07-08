package de.cronn.liquibase.changelog.generator;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.PropertySource;

@ImportAutoConfiguration({ DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@PropertySource("classpath:/de/cronn/liquibase/changelog/generator/hibernate-populated.properties")
public abstract class AbstractHibernatePopulatedConfig {
}
