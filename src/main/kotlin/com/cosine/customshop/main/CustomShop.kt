package com.cosine.customshop.main

import com.cosine.customshop.command.Command
import com.cosine.customshop.event.Event
import com.cosine.customshop.important.Config
import com.cosine.customshop.important.HikariCP
import com.cosine.customshop.important.MySQL
import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException


class CustomShop: JavaPlugin() {

    private lateinit var cp: HikariCP
    private lateinit var config: Config
    private lateinit var sql: MySQL

    override fun onEnable() {
        logger.info("커스텀 상점 플러그인 활성화")

        config = Config(this, "config.yml")
        config.saveDefaultConfig()

        createDataBase()

        cp = HikariCP(this)

        sql = MySQL(this)

        server.pluginManager.registerEvents(Event(), this)
        getCommand("상점").executor = Command(this)
    }

    override fun onDisable() {
        logger.info("커스텀 상점 플러그인 비활성화")
    }
    fun cp(): HikariCP {
        return this.cp;
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
    private fun createDataBase() {
        var connection: Connection? = null
        var ps: PreparedStatement? = null
        try {
            connection = DriverManager.getConnection(getUrl(), getUrl(), getPassword())
            ps = connection.prepareStatement(getUrl())

            val shop = "create database if not exists 상점"
            ps.executeUpdate(shop)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            ps?.close()
        }
    }
}