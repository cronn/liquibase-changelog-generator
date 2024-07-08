package de.cronn.liquibase.changelog.generator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import liquibase.database.AbstractJdbcDatabase;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.ObjectDifferences;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Column;
import liquibase.structure.core.ForeignKey;
import liquibase.structure.core.Index;
import liquibase.structure.core.PrimaryKey;
import liquibase.structure.core.Table;

public abstract class HibernateToLiquibaseDiff {

	private final String changeSetAuthor;

	protected HibernateToLiquibaseDiff(String changeSetAuthor) {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		this.changeSetAuthor = changeSetAuthor;
	}

	public String generateDiff(Class<? extends AbstractHibernatePopulatedConfig> hibernatePopulatedConfigClass,
							   Class<? extends AbstractLiquibasePopulatedConfig> liquibasePopulatedConfigClass) {
		try (AnnotationConfigApplicationContext hibernatePopulatedContext = new AnnotationConfigApplicationContext(hibernatePopulatedConfigClass);
			 Connection hibernatePopulatedConnection = getConnection(hibernatePopulatedContext);
			 AnnotationConfigApplicationContext liquibasePopulatedContext = new AnnotationConfigApplicationContext(liquibasePopulatedConfigClass);
			 Connection liquibasePopulatedConnection = getConnection(liquibasePopulatedContext)) {
			DiffResult diffResult = generateDiff(hibernatePopulatedConnection, liquibasePopulatedConnection);
			return writeDiffResultToChangeLog(diffResult);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Connection getConnection(GenericApplicationContext context) throws Exception {
		return context.getBean(DataSource.class).getConnection();
	}

	protected DiffResult generateDiff(Connection reference, Connection target) throws Exception {
		Database referenceDatabase = newDatabase(reference);
		Database targetDatabase = newDatabase(target);

		DiffResult diff = DiffGeneratorFactory.getInstance().compare(referenceDatabase, targetDatabase, CompareControl.STANDARD);
		return filterDiffResult(diff);
	}

	protected DiffResult filterDiffResult(DiffResult diffResult) {
		DatabaseSnapshot referenceDatabaseSnapshot = diffResult.getReferenceSnapshot();
		DatabaseSnapshot comparisonDatabaseSnapshot = diffResult.getComparisonSnapshot();
		CompareControl compareControl = diffResult.getCompareControl();

		DiffResult result = new DiffResult(referenceDatabaseSnapshot, comparisonDatabaseSnapshot, compareControl);

		diffResult.getChangedObjects().forEach((obj, differences) -> {
			handleChangedObject(result, obj, differences);
		});

		for (DatabaseObject obj : diffResult.getMissingObjects()) {
			handleMissingObject(result, obj);
		}

		for (DatabaseObject obj : diffResult.getUnexpectedObjects()) {
			handleUnexpectedObject(result, obj);
		}

		return result;
	}

	protected void handleChangedObject(DiffResult result, DatabaseObject obj, ObjectDifferences differences) {
		result.addChangedObject(obj, differences);
	}

	protected void handleMissingObject(DiffResult result, DatabaseObject obj) {
		generateNewKeyAndIndexNames(obj);
		result.addMissingObject(obj);
	}

	protected void generateNewKeyAndIndexNames(DatabaseObject obj) {
		if (obj instanceof PrimaryKey primaryKey) {
			primaryKey.setName(generateNewPrimaryKeyName(primaryKey));
		}
		if (obj instanceof ForeignKey foreignKey) {
			foreignKey.setName(generateNewForeignKeyName(foreignKey));
		}
		if (obj instanceof Index index) {
			index.setName(generateNewIndexName(index));
		}
	}

	protected void handleUnexpectedObject(DiffResult result, DatabaseObject obj) {
		result.addUnexpectedObject(obj);
	}

	protected String writeDiffResultToChangeLog(DiffResult result) throws Exception {
		DiffOutputControl diffOutputControl = newDiffOutputControl();

		DiffToChangeLog diffToChangeLog = new DiffToChangeLog(result, diffOutputControl);
		diffToChangeLog.setChangeSetAuthor(changeSetAuthor);

		String encoding = StandardCharsets.UTF_8.name();
		try (ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			 PrintStream out = new PrintStream(bytesOut, true, encoding)) {
			diffToChangeLog.print(out);
			out.flush();
			return bytesOut.toString(encoding);
		}
	}

	protected DiffOutputControl newDiffOutputControl() {
		return new DiffOutputControl(false, false, false, null);
	}

	protected String generateNewPrimaryKeyName(PrimaryKey primaryKey) {
		return "pk_" + primaryKey.getTable().getName();
	}

	protected String generateNewForeignKeyName(ForeignKey foreignKey) {
		Table primaryKeyTable = foreignKey.getPrimaryKeyTable();
		Table foreignKeyTable = foreignKey.getForeignKeyTable();
		return "fk_" + foreignKeyTable.getName() + "_" + primaryKeyTable.getName();
	}

	protected String generateNewIndexName(Index index) {
		return index.getColumns().stream()
			.map(Column::getName)
			.collect(Collectors.joining("_", "idx_" + index.getRelation().getName() + "_", ""));
	}

	protected Database newDatabase(Connection connection) {
		AbstractJdbcDatabase database = createDatabase();
		database.setConnection(new JdbcConnection(connection));
		return database;
	}

	protected abstract AbstractJdbcDatabase createDatabase();
}
