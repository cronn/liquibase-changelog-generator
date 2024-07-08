package de.cronn.liquibase.changelog.generator;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class HibernateIntegratorForSchemaExport implements org.hibernate.integrator.spi.Integrator {

	static Metadata metadata;

	@Override
	public void integrate(Metadata metadata, BootstrapContext bootstrapContext, SessionFactoryImplementor sessionFactory) {
		HibernateIntegratorForSchemaExport.metadata = metadata;
	}

	@Override
	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		metadata = null;
	}

	public static Metadata getMetadata() {
		return metadata;
	}
}
