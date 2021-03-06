plugins {
    kotlin("multiplatform") version Vers.kotlin
    `maven-publish`
}

group = "pl.mareklangiewicz.upue"
version = "0.0.07"

repositories {
    mavenCentral()
    maven(Repos.jitpack)
}

kotlin {
    jvm()
//    js {
//        browser()
//    }
//    linuxX64()

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(Deps.junit4)
                implementation(Deps.googleTruth)
            }

        }
    }
}
