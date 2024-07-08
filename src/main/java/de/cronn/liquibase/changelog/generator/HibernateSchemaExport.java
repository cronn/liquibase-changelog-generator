package de.cronn.liquibase.changelog.generator;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.tool.schema.SourceType;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.internal.ExceptionHandlerHaltImpl;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToWriter;
import org.hibernate.tool.schema.spi.ContributableMatcher;
import org.hibernate.tool.schema.spi.ExceptionHandler;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaCreator;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.hibernate.tool.schema.spi.ScriptSourceInput;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;
import org.hibernate.tool.schema.spi.SourceDescriptor;
import org.hibernate.tool.schema.spi.TargetDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

// This implementation is based on org.hibernate.tool.hbm2ddl.SchemaExport from the "hibernate-ant" dependency.
public class HibernateSchemaExport {

	private static final Logger log = LoggerFactory.getLogger(HibernateSchemaExport.class);

	private final Class<? extends AbstractHibernatePopulatedConfig> hibernatePopulatedConfigClass;

	public HibernateSchemaExport(Class<? extends AbstractHibernatePopulatedConfig> hibernatePopulatedConfigClass) {
		this.hibernatePopulatedConfigClass = hibernatePopulatedConfigClass;
	}

	public String export() {
		try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(hibernatePopulatedConfigClass)) {
			log.trace("Created application context {}", context);
			String schemaExport = exportSchema();
			return normalizeSchemaDumpFile(schemaExport);
		}
	}

	protected String exportSchema() {
		Metadata metadata = HibernateIntegratorForSchemaExport.getMetadata();

		Map<String, Object> config = buildConfig();

		StringWriter writer = new StringWriter();

		SchemaCreator schemaCreator = getSchemaCreator(config);
		schemaCreator.doCreation(metadata, new ExecutionOptionsForSchemaExport(config), ContributableMatcher.ALL,
			new MetadataSourceDescriptor(), new ScriptTargetDescriptor(writer));

		return writer.toString();
	}

	protected String normalizeSchemaDumpFile(String schemaExport) {
		String schemaExportWithNormalizedWhitespaces = normalizeIndentations(schemaExport);
		return sortCreateTypeStatements(schemaExportWithNormalizedWhitespaces);
	}

	protected String normalizeIndentations(String schemaExport) {
		return Arrays.stream(schemaExport.split("\r?\n"))
			.map(line -> {
				line = line.replaceFirst("^ {4}", "");
				line = line.replaceFirst("^ {3}(\\w)", "    $1");
				return StringUtils.stripEnd(line, null);
			})
			.collect(Collectors.joining("\n"));
	}

	protected String sortCreateTypeStatements(String schemaExport) {
		String partToSort = StringUtils.substringBefore(schemaExport, "create table");
		String partAfterSort = schemaExport.substring(partToSort.length());
		String sortedPart =
			Stream.of(partToSort.split("create type"))
				.sorted()
				.collect(Collectors.joining("create type"));
		return sortedPart + partAfterSort;
	}

	protected Map<String, Object> buildConfig() {
		ConfigurationService configurationService = getServiceRegistry().requireService(ConfigurationService.class);
		Map<String, Object> config = new LinkedHashMap<>(configurationService.getSettings());
		config.put(AvailableSettings.FORMAT_SQL, true);
		return config;
	}

	protected SchemaCreator getSchemaCreator(Map<String, Object> config) {
		return getServiceRegistry().requireService(SchemaManagementTool.class).getSchemaCreator(config);
	}

	protected StandardServiceRegistry getServiceRegistry() {
		MetadataImplementor metadata = (MetadataImplementor) HibernateIntegratorForSchemaExport.getMetadata();
		return metadata.getMetadataBuildingOptions().getServiceRegistry();
	}

	@SuppressWarnings("ClassCanBeRecord")
	private static class ScriptTargetDescriptor implements TargetDescriptor {

		private final Writer writer;

		public ScriptTargetDescriptor(Writer writer) {
			this.writer = writer;
		}

		@Override
		public EnumSet<TargetType> getTargetTypes() {
			return EnumSet.of(TargetType.SCRIPT);
		}

		@Override
		public ScriptTargetOutput getScriptTargetOutput() {
			return new ScriptTargetOutputToWriter(writer);
		}
	}

	private static class MetadataSourceDescriptor implements SourceDescriptor {
		@Override
		public SourceType getSourceType() {
			return SourceType.METADATA;
		}

		@Override
		public ScriptSourceInput getScriptSourceInput() {
			return null;
		}
	}

	@SuppressWarnings("ClassCanBeRecord")
	private static class ExecutionOptionsForSchemaExport implements ExecutionOptions {
		private final Map<String, Object> config;

		public ExecutionOptionsForSchemaExport(Map<String, Object> config) {
			this.config = config;
		}

		@Override
		public Map<String, Object> getConfigurationValues() {
			return config;
		}

		@Override
		public boolean shouldManageNamespaces() {
			return false;
		}

		@Override
		public ExceptionHandler getExceptionHandler() {
			return ExceptionHandlerHaltImpl.INSTANCE;
		}
	}
}
