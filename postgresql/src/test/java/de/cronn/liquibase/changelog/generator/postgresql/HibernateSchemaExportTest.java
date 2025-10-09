package de.cronn.liquibase.changelog.generator.postgresql;

import org.junit.jupiter.api.Test;

import de.cronn.assertions.validationfile.FileExtensions;
import de.cronn.liquibase.changelog.generator.BaseTest;
import de.cronn.liquibase.changelog.generator.HibernateSchemaExport;
import de.cronn.liquibase.changelog.generator.pgvector.model.HibernatePopulatedConfigForPgvectorWithTestModel;

class HibernateSchemaExportTest extends BaseTest {
	@Test
	void testExport() {
		String hibernateSchema = new HibernateSchemaExport(HibernatePopulatedConfigForPostgresWithTestModel.class)
			.export();
		assertWithFile(hibernateSchema, FileExtensions.SQL);
	}

	@Test
	void testExport_pgVector() {
		String hibernateSchema = new HibernateSchemaExport(HibernatePopulatedConfigForPgvectorWithTestModel.class)
			.export();
		assertWithFile(hibernateSchema, FileExtensions.SQL);
	}
}
