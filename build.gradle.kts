buildscript {
    repositories {
        mavenCentral()
    }
    dependencyLocking {
        lockAllConfigurations()
    }
}

plugins {
    `java-test-fixtures`
    id("org.springframework.boot") version "latest.release" apply false
    id("io.spring.dependency-management") version "latest.release" apply false
    id("org.jreleaser") version "latest.release"
}

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "jacoco")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    group = "de.cronn"
    version = "2.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> { enabled = false }
    tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> { enabled = false }

    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
    }

    dependencies {
        add("implementation", "org.springframework:spring-context")
        add("implementation", "org.springframework.boot:spring-boot-autoconfigure")

        add("testImplementation", "org.junit.jupiter:junit-jupiter-params")
        add("testRuntimeOnly", "org.junit.jupiter:junit-jupiter-engine")
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")

        add("testRuntimeOnly", "ch.qos.logback:logback-classic")

        components {
            all {
                if (id.version.matches(Regex("(?i).+([-.])(CANDIDATE|RC|BETA|ALPHA|CR\\d+|M\\d+).*"))) {
                    status = "milestone"
                }
            }
        }
    }

    dependencyLocking {
        lockAllConfigurations()
    }

    tasks.named("test", Test::class) {
        useJUnitPlatform()
        maxHeapSize = "256m"
        inputs.dir("data/test/validation")
        outputs.dir("data/test/output")
        outputs.dir("data/test/tmp")
    }

    tasks.named("jacocoTestReport", JacocoReport::class) {
        reports {
            xml.required = true
        }
        dependsOn(tasks.named("test"))
    }

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier = "sources"
        from(project.extensions.getByType<SourceSetContainer>()["main"].allSource)
        dependsOn(tasks.named("classes"))
    }

    val javadocJar by tasks.registering(Jar::class) {
        archiveClassifier = "javadoc"
        from(tasks.named("javadoc", Javadoc::class).get().destinationDir)
        dependsOn(tasks.named("javadoc"))
    }

    extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                pom {
                    name = project.name
                    description = "Liquibase Changelog Generator"
                    url = "https://github.com/cronn/liquibase-changelog-generator"

                    licenses {
                        license {
                            name = "The Apache Software License, Version 2.0"
                            url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                            distribution = "repo"
                        }
                    }

                    developers {
                        developer {
                            id = "benedikt.waldvogel"
                            name = "Benedikt Waldvogel"
                            email = "benedikt.waldvogel@cronn.de"
                        }
                    }

                    scm {
                        url = "https://github.com/cronn/liquibase-changelog-generator"
                    }
                }

                from(components["java"])

                artifact(sourcesJar)
                artifact(javadocJar)

                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }
            }
        }
        repositories {
            maven {
                name = "staging"
                url = uri(layout.buildDirectory.dir("staging-deploy"))
            }
        }
    }

    extensions.configure<SigningExtension> {
        useGpgCmd()
        sign(extensions.getByType<PublishingExtension>().publications["mavenJava"])
    }
}

dependencies {
    add("implementation", "org.slf4j:slf4j-api")
    add("implementation", "org.slf4j:jul-to-slf4j")

    add("implementation", "org.springframework.boot:spring-boot-jdbc")
    add("implementation", "org.springframework.boot:spring-boot-hibernate")
    add("implementation", "org.springframework.boot:spring-boot-liquibase")

    add("api", "org.liquibase:liquibase-core")
    add("implementation", "org.testcontainers:testcontainers-jdbc:latest.release")

    add("implementation", "org.hibernate.orm:hibernate-core")

    add("testFixturesApi", "org.assertj:assertj-core")
    add("testFixturesApi", "org.springframework.boot:spring-boot-starter-data-jpa")
    add("testFixturesApi", "de.cronn:test-utils:latest.release")
    add("testFixturesApi", "de.cronn:validation-file-assertions:latest.release")
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }

tasks.wrapper {
    gradleVersion = "9.5.0"
    distributionType = Wrapper.DistributionType.ALL
}

jreleaser {
    signing {
        setActive("NEVER")
    }
    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    setActive("RELEASE")
                    sign = false
                    url = "https://central.sonatype.com/api/v1/publisher"
                    allprojects.forEach { p ->
                        stagingRepository(
                            p.layout.buildDirectory
                                .dir("staging-deploy")
                                .get()
                                .asFile.path,
                        )
                    }
                }
            }
        }
    }
}

