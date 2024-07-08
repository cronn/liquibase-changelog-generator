package de.cronn.liquibase.changelog.generator.postgresql;

import de.cronn.liquibase.changelog.generator.HibernateToLiquibaseDiff;
import liquibase.database.AbstractJdbcDatabase;
import liquibase.database.core.PostgresDatabase;

public class HibernateToLiquibaseDiffForPostgres extends HibernateToLiquibaseDiff {
	public HibernateToLiquibaseDiffForPostgres(String changeSetAuthor) {
		super(changeSetAuthor);
	}

	@Override
	protected AbstractJdbcDatabase createDatabase() {
		return new PostgresDatabase();
	}
}
