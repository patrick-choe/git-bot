plugins {
    kotlin("jvm") version "1.3.72"
    id("org.jetbrains.dokka") version "0.10.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    `maven-publish`
    signing
}

group = "com.github.patrick-mc"
version = "alpha-0.2-29APR20-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/dokka")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.discord4j:discord4j-core:3.0.12")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
}

tasks {
    compileKotlin { kotlinOptions.jvmTarget = "1.8" }

    val dokka by getting(org.jetbrains.dokka.gradle.DokkaTask::class) {
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/dokka"

        configuration {
            includeNonPublic = true
            jdkVersion = 8
        }
    }

    jar { manifest { attributes["Main-Class"] = "com.github.patrick.bot.gitBot" } }

    create<Jar>("dokkaJar") {
        archiveClassifier.set("javadoc")
        from(dokka)
        dependsOn(dokka)
    }

    create<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    shadowJar {
        archiveClassifier.set("exec")
        manifest { attributes["Main-Class"] = "com.github.patrick.bot.GitBot" }
    }
}

try {
    publishing {
        publications {
            create<MavenPublication>("gitBot") {
                from(components["java"])

                artifact(tasks["sourcesJar"])
                artifact(tasks["dokkaJar"])
                artifact(tasks["shadowJar"])

                repositories {
                    mavenLocal()
                    maven {
                        name = "central"

                        credentials {
                            username = project.property("centralUsername").toString()
                            password = project.property("centralPassword").toString()
                        }

                        val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                        val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
                        url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                    }

                    maven {
                        name = "local"

                        credentials {
                            username = project.property("localUsername").toString()
                            password = project.property("localPassword").toString()
                        }

                        val releasesRepoUrl = uri("http://localhost:8081/repository/maven-releases/")
                        val snapshotsRepoUrl = uri("http://localhost:8081/repository/maven-snapshots/")
                        url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                    }
                }

                pom {
                    name.set("patrick-bot")
                    description.set("Discord Bot written in Kotlin")
                    url.set("https://github.com/patrick-mc/git-bot")

                    licenses {
                        license {
                            name.set("GNU General Public License v2.0")
                            url.set("https://opensource.org/licenses/gpl-2.0.php")
                        }
                    }

                    developers {
                        developer {
                            id.set("patrick-mc")
                            name.set("PatrickKR")
                            email.set("mailpatrickkorea@gmail.com")
                            url.set("https://github.com/patrick-mc")
                            roles.addAll("developer")
                            timezone.set("Asia/Seoul")
                        }
                    }

                    scm {
                        connection.set("scm:git:git://github.com/patrick-mc/git-bot.git")
                        developerConnection.set("scm:git:ssh://github.com:patrick-mc/git-bot.git")
                        url.set("https://github.com/patrick-mc/git-bot")
                    }
                }
            }
        }
    }

    signing {
        isRequired = true
        sign(tasks["sourcesJar"], tasks["dokkaJar"])
        sign(publishing.publications["gitBot"])
    }
} catch (e: groovy.lang.MissingPropertyException) {}