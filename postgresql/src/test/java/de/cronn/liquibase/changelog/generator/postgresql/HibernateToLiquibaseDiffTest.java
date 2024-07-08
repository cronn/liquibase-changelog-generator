package de.cronn.liquibase.changelog.generator.postgresql;

import de.cronn.liquibase.changelog.generator.BaseTest;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.PropertySource;

import de.cronn.assertions.validationfile.FileExtensions;
import de.cronn.assertions.validationfile.normalization.SimpleRegexReplacement;
import de.cronn.assertions.validationfile.normalization.ValidationNormalizer;

class HibernateToLiquibaseDiffTest extends BaseTest {

	@Test
	void testGenerateDiff_emptyLiquibaseChangelog() {
		String diff = new HibernateToLiquibaseDiffForPostgres("Jane Doe")
			.generateDiff(HibernatePopulatedConfigForPostgresWithTestModel.class, EmptyLiquibaseConfig.class);
		assertWithFile(diff, maskGeneratedId(), FileExtensions.XML);
	}

	@Test
	void testGenerateDiff_fullLiquibaseChangelog() {
		String diff = new HibernateToLiquibaseDiffForPostgres("Jane Doe")
			.generateDiff(HibernatePopulatedConfigForPostgresWithTestModel.class, FullLiquibaseConfig.class);
		assertWithFile(diff, maskGeneratedId(), FileExtensions.XML);
	}

	@PropertySource("classpath:/empty-changelog.properties")
	private static class EmptyLiquibaseConfig extends LiquibasePopulatedConfigForPostgres {
	}

	@PropertySource("classpath:/full-changelog.properties")
	private static class FullLiquibaseConfig extends LiquibasePopulatedConfigForPostgres {
	}

	private static ValidationNormalizer maskGeneratedId() {
		return new SimpleRegexReplacement("id=\"\\d{10,}-", "id=\"[MASKED]-");
	}
}

