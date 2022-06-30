package com.cosine.customshop.main

import com.cosine.customshop.command.Command
import com.cosine.customshop.event.Event
import com.cosine.customshop.gui.Gui
import com.cosine.customshop.important.Config
import com.cosine.customshop.important.HikariCP
import com.cosine.customshop.important.ItemStackSerializer
import com.cosine.customshop.important.MySQL
import org.bukkit.plugin.java.JavaPlugin
import java.sql.DriverManager
import java.sql.SQLException

class CustomShop: JavaPlugin() {

    private lateinit var cp: HikariCP
    private lateinit var config: Config
    private lateinit var sql: MySQL
    private lateinit var gui: Gui
    private lateinit var item: ItemStackSerializer

    override fun onEnable() {
        logger.info("커스텀 상점 플러그인 활성화")

        config = Config(this, "config.yml")
        config.saveDefaultConfig()

        createDataBase()

        item = ItemStackSerializer()

        cp = HikariCP(this)
        sql = MySQL(this)

        gui = Gui(this)

        server.pluginManager.registerEvents(Event(this), this)
        getCommand("상점").executor = Command(this)
    }

    override fun onDisable() {
        logger.info("커스텀 상점 플러그인 비활성화")
        cp.closeConnection()
    }
    private fun createDataBase() {
        try {
            DriverManager.getConnection(getUrl(), getUser(), getPassword()).use { connection -> connection.prepareStatement(getUrl()).use { ps ->
                val shop = "create database if not exists 상점"
                ps.executeUpdate(shop)
            }}
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
    fun item(): ItemStackSerializer {
        return this.item
    }
    fun gui(): Gui {
        return this.gui
    }
    fun cp(): HikariCP {
        return this.cp
    }
    fun sql(): MySQL {
        return this.sql
    }
    fun getUrl(): String {
        return "jdbc:mysql://" + config.getConfig().getString("MySQL.host") + ":" + config.getConfig().getString("MySQL.port")
    }
    fun getUser(): String {
        return config.getConfig().getString("MySQL.user")
    }
    fun getPassword(): String {
        return config.getConfig().getString("MySQL.password")
    }
}
