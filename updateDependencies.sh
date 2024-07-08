#!/bin/bash

# We remove the lockfiles as a workaround to inform the user of a failed dependency lock update.
# This is required as Gradle currently exits successfully even in case of errors.
rm **/*.lockfile
./gradlew dependencies liquibase-changelog-generator-postgresql:dependencies --refresh-dependencies --update-locks '*:*'
