package de.cronn.liquibase.changelog.generator;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

import de.cronn.assertions.validationfile.junit5.JUnit5ValidationFileAssertions;

@ExtendWith(SoftAssertionsExtension.class)
public abstract class BaseTest implements JUnit5ValidationFileAssertions {
	@InjectSoftAssertions
	private SoftAssertions softly;

	private ValidationFilenameHelper validationFilenameHelper;

	@Override
	public FailedAssertionHandler failedAssertionHandler() {
		return callable -> softly.check(callable::call);
	}

	@BeforeEach
	void storeTestInfo(TestInfo testInfo) {
		this.validationFilenameHelper = new ValidationFilenameHelper(testInfo);
	}

	@Override
	public String getTestName() {
		return validationFilenameHelper.getTestName();
	}
}
