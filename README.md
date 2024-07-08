[![CI](https://github.com/cronn/liquibase-changelog-generator/workflows/CI/badge.svg)](https://github.com/cronn/liquibase-changelog-generator/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.cronn/liquibase-changelog-generator/badge.svg)](http://maven-badges.herokuapp.com/maven-central/de.cronn/liquibase-changelog-generator)
[![Apache 2.0](https://img.shields.io/github/license/cronn/liquibase-changelog-generator.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![codecov](https://codecov.io/gh/cronn/liquibase-changelog-generator/branch/main/graph/badge.svg?token=KD1WJK5ZFK)](https://codecov.io/gh/cronn/liquibase-changelog-generator)
[![Valid Gradle Wrapper](https://github.com/cronn/liquibase-changelog-generator/workflows/Validate%20Gradle%20Wrapper/badge.svg)](https://github.com/cronn/liquibase-changelog-generator/actions/workflows/gradle-wrapper-validation.yml)

# Liquibase Changelog Generator #

The `liquibase-changelog-generator` library implements an auto-generation of Liquibase changelogs
based on the Hibernate metamodel. The library was designed to be used in a JUnit test.

## Usage

Depending on the database, you need to add the following Maven **test** dependency to your project:

### PostgreSQL

```xml
<dependency>
    <groupId>de.cronn</groupId>
    <artifactId>liquibase-changelog-generator-postgresql</artifactId>
    <version>1.0</version>
    <scope>test</scope>
</dependency>
```

## Overview

This library provides a mechanism to implement a unit test that sets up two databases using [Testcontainers](testcontainers):

1. **Hibernate Database**: Uses the database schema populated by Hibernate, based on the annotations of the `jakarta.persistence.Entity` classes.
2. **Liquibase Database**: Uses the Liquibase changelog to populate the database schema.

Once both databases are ready, we use the [DiffToChangeLog](liquibase-diff) mechanism of Liquibase to compare the two databases.
If the diff is non-empty, the test fails and it outputs the required Liquibase changes.
This ensures that the build pipeline can only succeed if there are no missing changesets.
We recommend to assert that diff using our [validation-file-assertions] library.

### Example

```java
class LiquibaseTest implements JUnit5ValidationFileAssertions {
    @Test
    void testLiquibaseAndHibernatePopulationsAreConsistent() {
        HibernateToLiquibaseDiff hibernateToLiquibaseDiff = new HibernateToLiquibaseDiffForPostgres("My Author");
        String diff = hibernateToLiquibaseDiff.generateDiff(HibernatePopulatedConfig.class, LiquibasePopulatedConfig.class);
        assertWithFile(diff, FileExtensions.XML);
    }

    @EntityScan("de.cronn.example")
    static class HibernatePopulatedConfig extends HibernatePopulatedConfigForPostgres {
    }

    @PropertySource("classpath:/liquibase-test-liquibase.properties")
    static class LiquibasePopulatedConfig extends LiquibasePopulatedConfigForPostgres {
    }
}
```

Then define the path to the Liquibase changelog via `src/test/resources/liquibase-test-liquibase.properties`

```properties
spring.liquibase.change-log=classpath:/migrations/changelog.xml
```

## Steps to Change/Extend the Database Schema

As a developer, you typically follow these steps when you want to change or extend the database schema:

1. **Modify Entity Classes**: Create, modify, or delete a `@Entity` class as desired.
2. **Run the Test**: Execute the `LiquibaseTest`. The test will fail and output the generated Liquibase changeset in the form of a difference to the validation file.
3. **Update Changelog**: Take the generated Liquibase changeset and add it to the Liquibase changelog file(s).
4. **Review Changeset**: ⚠ Review the auto-generated changeset very carefully! Consider it as only a **suggestion** or a **template**.
    The Liquibase diff mechanism is not perfect. For instance, when renaming a column, it will yield a drop-column and a create-column statement.
    In such cases, you need to adjust the changeset manually.
5. **Re-run the Test**: Re-run the `LiquibaseTest` and check that the test now succeeds.

## Additional Notes

The test performs some automatic post-processing of the diff. For example, it overrides the Hibernate-generated
primary-key and foreign-key names. See the `HibernateToLiquibaseDiff.filterDiffResult(DiffResult)` method for details.

## Hibernate Schema Export

We also provide a utility class to export the schema that Hibernate would create.
This can be useful as a first feedback during a (major) change of the JPA entities.

### Example

```java
class HibernateSchemaTest implements JUnit5ValidationFileAssertions {
    @Test
    void testExport() {
        String schema = new HibernateSchemaExport(HibernatePopulatedConfig.class).export();
        assertWithFile(schema, FileExtensions.SQL);
    }

    @EntityScan("de.cronn.example")
    static class HibernatePopulatedConfig extends HibernatePopulatedConfigForPostgres {
    }
}
```

## Requirements ##

- Java 17+
- Spring Boot 3.3.1+
- Liquibase 4.27.0+
- Hibernate 6.5.2+

## Related Projects ##

- [https://github.com/liquibase/liquibase-hibernate](https://github.com/liquibase/liquibase-hibernate)

[testcontainers]: https://testcontainers.com/
[liquibase-diff]: https://docs.liquibase.com/commands/inspection/diff-changelog.html
[validation-file-assertions]: https://github.com/cronn/validation-file-assertions
