import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "gg.hjk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
}

bukkit {
    apiVersion = "1.21.7"
    main = "gg.hjk.simplegreylist.SimpleGreylist"
    depend = listOf("LuckPerms")

    commands {
        register("vouch") {
            description = "A trusted player can vouch for an unknown player."
            permission = "simplegreylist.vouch"
            usage = "/vouch <player>"
        }
    }

    permissions {
        register("simplegreylist.vouch") {
            description = "Allows this user to vouch for other players"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}
