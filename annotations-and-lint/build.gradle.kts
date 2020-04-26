import org.gradle.jvm.tasks.Jar

plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.dokka-android")
    `maven-publish`
    signing
}

object Version {
    const val name = "1.1.0"
    const val code = 2
}

android {
    compileSdkVersion(AndroidSdk.compile)

    defaultConfig {
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)

        versionCode = Version.code
        versionName = Version.name
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("androidx.annotation:annotation:1.1.0")

    lintPublish(project(":lint-checks"))
}

tasks {
    dokka {
        outputFormat = "html"
        outputDirectory = "$buildDir/javadoc"
    }
}

tasks.register("sourcesJar", Jar::class.java) {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

tasks.register("javadocJar", Jar::class.java) {
    dependsOn.add("dokka")
    archiveClassifier.set("javadoc")
    from("$buildDir/javadoc")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.esentsov"
            artifactId = "kotlin-visibility"
            version = Version.name
            afterEvaluate {
                artifact(tasks["bundleReleaseAar"])
                artifact(tasks["sourcesJar"])
                artifact(tasks["javadocJar"])
            }

            pom {
                name.set("Kotlin Visibility Modifiers")
                description.set("Provides FilePrivate and PackagePrivate annotations to use in kotlin code as well as lint checks needed to validate their usage")
                url.set("https://github.com/esentsov/kotlin-visibility-modifiers")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("esentsov")
                        name.set("Evgeny Sentsov")
                        email.set("eugeny.sentsov@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/esentsov/kotlin-visibility-modifiers.git")
                    developerConnection.set("scm:git:ssh://github.com/esentsov/kotlin-visibility-modifiers.git")
                    url.set("https://github.com/esentsov/kotlin-visibility-modifiers/tree/master")
                }

                repositories {
                    maven {
                        setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                        authentication {
                            credentials {
                                username = project.properties["ossrhUsername"] as? String
                                password = project.properties["ossrhPassword"] as? String
                            }
                        }
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

