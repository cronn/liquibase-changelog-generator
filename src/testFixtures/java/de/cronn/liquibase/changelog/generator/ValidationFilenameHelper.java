package de.cronn.liquibase.changelog.generator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.TestInfo;

final class ValidationFilenameHelper {

	private final TestInfo testInfo;

	ValidationFilenameHelper(TestInfo testInfo) {
		this.testInfo = testInfo;
	}

	String getTestName() {
		List<String> classes = ValidationFilenameHelper.classHierarchy(getTestClass());
		return String.join("/", classes) + "/" + getTestMethod().getName();
	}

	private static List<String> classHierarchy(Class<?> aClass) {
		List<String> classHierarchy = new ArrayList<>();
		classHierarchy.add(aClass.getSimpleName());
		Class<?> enclosingClass = aClass.getEnclosingClass();
		while (enclosingClass != null) {
			classHierarchy.add(enclosingClass.getSimpleName());
			enclosingClass = enclosingClass.getEnclosingClass();
		}
		Collections.reverse(classHierarchy);
		return classHierarchy;
	}

	private Method getTestMethod() {
		return testInfo.getTestMethod().orElseThrow();
	}

	private Class<?> getTestClass() {
		return testInfo.getTestClass().orElseThrow();
	}
}
