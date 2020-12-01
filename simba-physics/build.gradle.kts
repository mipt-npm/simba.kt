plugins {
    id("ru.mipt.npm.mpp")
}


val kmathVersion: String by rootProject.extra
val dataforgeVersion: String by rootProject.extra


kscience{
    useCoroutines()
    useSerialization()
}


kotlin {
    sourceSets {
        commonMain {
            dependencies {
            }
        }
        jvmMain{
            dependencies {
                implementation("org.apache.commons:commons-math3:3.6.1")
                implementation("org.apache.commons:commons-csv:1.8")

                implementation("io.github.microutils:kotlin-logging:1.8.3")
                implementation("org.slf4j:slf4j-simple:1.7.29")

                implementation("hep.dataforge:dataforge-context-jvm:$dataforgeVersion")
                implementation("hep.dataforge:dataforge-data-jvm:$dataforgeVersion")
                implementation("hep.dataforge:dataforge-io-jvm:$dataforgeVersion")
                implementation("hep.dataforge:dataforge-meta-jvm:$dataforgeVersion")
                implementation("hep.dataforge:dataforge-tables-jvm:$dataforgeVersion")
                implementation("hep.dataforge:dataforge-workspace-jvm:$dataforgeVersion")

                api("scientifik:kmath-core-jvm:$kmathVersion")
                api("scientifik:kmath-prob-jvm:$kmathVersion")

                api(project(":simba-core"))
            }
        }
    }
}
