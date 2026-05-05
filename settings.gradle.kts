rootProject.name = "liquibase-changelog-generator"

include("postgresql")

rootProject.children.forEach { it.name = rootProject.name + "-" + it.name }
