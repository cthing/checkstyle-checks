import com.github.spotbugs.SpotBugsTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// This project is consumed by infrastructure bootstrap code. Therefore it does not use any
// C Thing Software Gradle plugins and is in the org.cthing domain so it can be consumed as
// a third party dependency.

plugins {
    `java-library`
    checkstyle
    jacoco
    `maven-publish`
    signing
    id("com.github.spotbugs") version "1.6.9"
}

val isCIServer = System.getenv("CTHING_CI") != null
val isSnapshot = property("cthing.build.type") == "snapshot"
val buildNumber = if (isCIServer) System.currentTimeMillis().toString() else "0"
val semver = property("cthing.version") as String
version = if (isSnapshot) "$semver-$buildNumber" else semver
group = property("cthing.group") as String
description = property("cthing.description") as String

val checkstyleVersion = "8.17"

dependencies {
    api("com.puppycrawl.tools:checkstyle:$checkstyleVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.4.0")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testCompileOnly("org.apiguardian:apiguardian-api:1.0.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.0")

    spotbugsPlugins("com.mebigfatguy.fb-contrib:fb-contrib:7.4.3.sb")
}

checkstyle {
    toolVersion = checkstyleVersion
    isIgnoreFailures = false
    configFile = file("dev/checkstyle/checkstyle.xml")
    configDir = file("dev/checkstyle")
    isShowViolations = true
}

spotbugs {
    toolVersion = "3.1.11"
    isIgnoreFailures = false
    effort = "max"
    reportLevel = "medium"
    excludeFilter = file("dev/spotbugs/suppressions.xml")
    sourceSets = listOf(project.sourceSets["main"])
}

jacoco {
    toolVersion = "0.8.3"
}

tasks {
    withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf("--release", "8", "-Xlint:all", "-Xlint:-options", "-Werror"))
    }

    withType<Jar>().configureEach {
        manifest.attributes(mapOf("Implementation-Title" to project.name,
                                  "Implementation-Vendor" to project.property("cthing.organization.name"),
                                  "Implementation-Version" to project.version))
    }

    withType<Javadoc>().configureEach {
        with(options as StandardJavadocDocletOptions) {
            breakIterator(false)
            encoding("UTF-8")
            bottom("Copyright &copy; ${SimpleDateFormat("yyyy", Locale.ENGLISH).format(Date())} ${project.property("cthing.organization.name")}. All rights reserved.")
            memberLevel = JavadocMemberLevel.PUBLIC
            outputLevel = JavadocOutputLevel.QUIET
        }
    }

    withType<SpotBugsTask>().configureEach {
        with(reports) {
            xml.isEnabled = false
            html.isEnabled = true
        }
    }

    withType<JacocoReport>().configureEach {
        dependsOn("test")
        with(reports) {
            xml.isEnabled = false
            csv.isEnabled = false
            html.isEnabled = true
            html.destination = File(buildDir, "reports/jacoco")
        }
    }

    withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

val sourceJar by tasks.registering(Jar::class) {
    from(project.sourceSets["main"].allSource)
    archiveClassifier.set("sources")
}

val javadocJar by tasks.registering(Jar::class) {
    from("javadoc")
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])

            artifact(sourceJar.get())
            artifact(javadocJar.get())

            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://bitbucket.org/cthing/${project.name}")
                licenses {
                    license {
                        name.set(property("cthing.license.name") as String)
                        url.set(property("cthing.license.url") as String)
                    }
                }
                developers {
                    developer {
                        id.set(property("cthing.developer.id") as String)
                        name.set(property("cthing.developer.name") as String)
                        email.set("${property("cthing.developer.id")}@cthing.com")
                        organization.set(property("cthing.organization.name") as String)
                        organizationUrl.set(property("cthing.organization.url") as String)
                    }
                }
                scm {
                    connection.set("scm:git:git://bitbucket.org/cthing/${project.name}.git")
                    developerConnection.set("scm:git:ssh://bitbucket.org:cthing/${project.name}")
                    url.set("https://bitbucket.org/cthing/${project.name}/src")
                }
            }
        }
    }

    val repoUrl = if (isSnapshot) property("cthing.nexus.snapshotsUrl") else property("cthing.nexus.candidatesUrl")
    if (repoUrl != null) {
        repositories {
            maven {
                setUrl(repoUrl)
                credentials {
                    username = property("cthing.nexus.user") as String
                    password = property("cthing.nexus.password") as String
                }
            }
        }
    }
}

if (hasProperty("signing.keyId") && hasProperty("signing.password") && hasProperty("signing.secretKeyRingFile")) {
    signing {
        sign(publishing.publications["mavenJava"])
    }
}
