package de.cronn.liquibase.changelog.generator.postgresql;

import de.cronn.liquibase.changelog.generator.BaseTest;

import org.junit.jupiter.api.Test;

import de.cronn.assertions.validationfile.FileExtensions;
import de.cronn.liquibase.changelog.generator.HibernateSchemaExport;

class HibernateSchemaExportTest extends BaseTest {
	@Test
	void testExport() {
		String hibernateSchema = new HibernateSchemaExport(HibernatePopulatedConfigForPostgresWithTestModel.class)
			.export();
		assertWithFile(hibernateSchema, FileExtensions.SQL);
	}
}
