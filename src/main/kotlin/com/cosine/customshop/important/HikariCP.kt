package com.cosine.customshop.important

import com.cosine.customshop.main.CustomShop
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

class HikariCP(plugin: CustomShop) {

    private val config: HikariConfig = HikariConfig()
    private val ds: HikariDataSource

    init {
        config.jdbcUrl = plugin.getUrl() + "/상점"
        config.username = plugin.getUser()
        config.password = plugin.getPassword()
        config.maximumPoolSize = 50
        config.minimumIdle = 20
        ds = HikariDataSource(config)
    }

    fun getConnection(): Connection {
        return ds.connection;
    }
    fun closeConnection() {
        ds.close()
    }
}