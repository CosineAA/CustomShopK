package com.cosine.customshop.important

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets

class Config(plugin: JavaPlugin, fileName: String) {

    private var fileName: String
    private val plugin: JavaPlugin
    private var file: File
    private lateinit var config: FileConfiguration

    init {
        this.plugin = plugin
        this.fileName = fileName
        val dataFolder: File = plugin.dataFolder
        this.file = File(dataFolder.toString() + File.separatorChar + this.fileName)
        reloadConfig()
    }

    private fun reloadConfig() {
        config = YamlConfiguration.loadConfiguration(InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8))
        val defConfigStream: Reader = InputStreamReader(plugin.getResource(this.fileName))
        if (defConfigStream != null) {
            val defConfig: YamlConfiguration = YamlConfiguration.loadConfiguration(defConfigStream)
            config.defaults = defConfig
        }
    }
    fun getConfig(): FileConfiguration {
        return this.config
    }
    fun saveDefaultConfig() {
        if (!file.exists()) {
            plugin.saveResource(this.fileName, false)
        }
    }
}