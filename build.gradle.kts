plugins {
	id("fabric-loom") version "1.7-SNAPSHOT"
	`maven-publish`
	id("org.jetbrains.kotlin.jvm") version "2.0.0"
}

val port_lib_modules: String by extra


version = "${property("minecraft_version")}-${property("mod_version")}-fabric"

base {
	archivesName.set("${property("archives_base_name")}")
}

loom {
	accessWidenerPath = file("src/main/resources/voidbound.accesswidener")
}

sourceSets {
	named("main") {
		resources {
			srcDir("src/generated/resources")
		}
	}
}

repositories {
	flatDir {
		dirs("libs")
	}
	mavenCentral()

	maven ("https://maven.theillusivec4.top/")
	maven("https://dvs1.progwml6.com/files/maven")
	maven("https://maven.tterrag.com/")
	maven("https://maven.blamejared.com/")
	maven("https://cursemaven.com")
	maven("https://api.modrinth.com/maven")
	maven("https://jitpack.io")
	maven("https://maven.ladysnake.org/releases")
	maven("https://maven.terraformersmc.com/")

	maven("https://maven.parchmentmc.org")
	maven("https://mvn.devos.one/snapshots/")
	maven("https://mvn.devos.one/releases/")
	maven("https://maven.jamieswhiteshirt.com/libs-release")
	maven("https://maven.shedaniel.me/")
	maven("https://dl.cloudsmith.io/public/tslat/sbl/maven/")
	maven("https://maven.nucleoid.xyz/")
}

dependencies {
	minecraft("com.mojang:minecraft:${property("minecraft_version")}")

	mappings(loom.layered {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-1.20.1:2023.09.03@zip")
	})

	modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")

	modImplementation("net.fabricmc:fabric-language-kotlin:1.11.0+kotlin.2.0.0")

	modImplementation("com.github.Chocohead:Fabric-ASM:v2.3")

	modImplementation("me.shedaniel.cloth:cloth-config-fabric:11.1.118")

	//EMI
	modCompileOnly("dev.emi:emi-fabric:${property("emi_version")}:api")
	modRuntimeOnly("maven.modrinth:just-enough-effect-descriptions-jeed:${property("jeed_version")}")
	modLocalRuntime("dev.emi:emi-fabric:${property("emi_version")}")

	// Trinkets Dependency
	modImplementation("dev.emi:trinkets:${property("trinkets_version")}")

	modImplementation("team.lodestar.lodestone:lodestone:${property("minecraft_version")}-${property("lodestone_version")}-fabric")

	modImplementation("maven.modrinth:malum:${property("malum_version")}")

	modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-base:${property("cca_version")}")
	modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:${property("cca_version")}")
	modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-world:${property("cca_version")}")

	modImplementation("com.terraformersmc.terraform-api:terraform-wood-api-v1:${property("terraform_api_version")}")

	port_lib_modules.split(",").forEach { module ->
		include(("io.github.fabricators_of_create.Porting-Lib:$module:${property("port_lib_version")}"))
		modImplementation(("io.github.fabricators_of_create.Porting-Lib:$module:${property("port_lib_version")}"))
	}

	modImplementation("com.jamieswhiteshirt:reach-entity-attributes:${property("reach_entity_attributes_version")}")
	modImplementation("maven.modrinth:fusion-connected-textures:${property("fusion_version")}-fabric-mc${property("minecraft_version")}")
	modImplementation("net.tslat.smartbrainlib:SmartBrainLib-fabric-${property("minecraft_version")}:${property("smart_brain_lib_version")}")
	include("net.tslat.smartbrainlib:SmartBrainLib-fabric-${property("minecraft_version")}:${property("smart_brain_lib_version")}")

	//modImplementation ("maven.modrinth:Revelationary:${property("revelationary_version")}")

	modImplementation("eu.pb4:common-protection-api:${property("protection_api_version")}")
	include("eu.pb4:common-protection-api:${property("protection_api_version")}")
	//modImplementation("vectorwing:FarmersDelight:${property("farmers_delight_version")}")
	//modRuntimeOnly("com.simibubi.create:create-fabric-1.20.1:0.5.1-f-build.1417+mc1.20.1")
}

tasks {

	processResources {
		inputs.property("version", project.version)
		filesMatching("fabric.mod.json") {
			expand(getProperties())
			expand(mutableMapOf("version" to project.version))
		}
	}

	jar {
		from("LICENSE")
	}

	compileJava{
		targetCompatibility = "17"
	}
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}