dependencies {
    api(project(":"))

    runtimeOnly("org.testcontainers:testcontainers-postgresql:latest.release")
    runtimeOnly("org.postgresql:postgresql")

    testRuntimeOnly("org.hibernate.orm:hibernate-vector")

    testImplementation(testFixtures(project(":")))
}
