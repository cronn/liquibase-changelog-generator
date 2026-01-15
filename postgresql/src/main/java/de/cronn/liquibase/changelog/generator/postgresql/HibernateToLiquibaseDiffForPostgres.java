package de.cronn.liquibase.changelog.generator.postgresql;

import java.util.Locale;
import java.util.Set;

import de.cronn.liquibase.changelog.generator.HibernateToLiquibaseDiff;
import liquibase.database.AbstractJdbcDatabase;
import liquibase.database.core.PostgresDatabase;
import liquibase.diff.DiffResult;
import liquibase.diff.Difference;
import liquibase.diff.ObjectDifferences;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Index;

public class HibernateToLiquibaseDiffForPostgres extends HibernateToLiquibaseDiff {

	private static final Set<String> WELL_KNOWN_PGVECTOR_INDEX_TYPES = Set.of("hnsw", "ivfflat");

	public HibernateToLiquibaseDiffForPostgres(String changeSetAuthor) {
		super(changeSetAuthor);
	}

	@Override
	protected AbstractJdbcDatabase createDatabase() {
		return new PostgresDatabase();
	}


	// Filter out pgvector index type changes (btree → hnsw/ivfflat)
	private static boolean isFalsePositiveVectorIndexTypeChange(DatabaseObject obj, ObjectDifferences differences) {
		if (!(obj instanceof Index) || differences.getDifferences().size() != 1) {
			return false;
		}

		Difference difference = differences.getDifferences().iterator().next();
		return difference.getField().equalsIgnoreCase("using")
			   && difference.getReferenceValue().toString().equalsIgnoreCase("btree")
			   && WELL_KNOWN_PGVECTOR_INDEX_TYPES.contains(difference.getComparedValue().toString().toLowerCase(Locale.ROOT));
	}

	@Override
	protected void handleChangedObject(DiffResult result, DatabaseObject obj,
									   ObjectDifferences differences) {
		if (isFalsePositiveVectorIndexTypeChange(obj, differences)) {
			return;
		}
		super.handleChangedObject(result, obj, differences);
	}
}
