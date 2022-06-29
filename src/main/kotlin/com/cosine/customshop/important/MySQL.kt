package com.cosine.customshop.important

import com.cosine.customshop.main.CustomShop
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException


class MySQL(plugin: CustomShop) {

    private val plugin: CustomShop
    private val cp: HikariCP

    init {
        this.plugin = plugin
        cp = plugin.cp()
    }

    fun createShop(shop: String) {
        var connection: Connection? = null
        var ps: PreparedStatement? = null
        try {
            connection = cp.getConnection()
            ps = connection.prepareStatement(plugin.getUrl())

            val create = "create table if not exists $shop (슬롯 int, 구매가격 int, 판매가격 int, 수량 int, 아이템 varchar(1000))"
            ps.execute(create)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            ps?.close()
        }
    }
    fun deleteShop(shop: String) {
        var connection: Connection? = null
        var ps: PreparedStatement? = null
        try {
            connection = cp.getConnection()
            ps = connection.prepareStatement(plugin.getUrl())

            val create = "drop table $shop"
            ps.execute(create)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            ps?.close()
        }
    }
    fun existShop(shop: String): Boolean {
        var connection: Connection? = null
        var ps: PreparedStatement? = null
        var rs: ResultSet? = null
        val create = "select count(*) from information_schema.tables where table_schema = '상점' and table_name = '$shop' "
        try {
            connection = cp.getConnection()
            ps = connection.prepareStatement(plugin.getUrl())
            rs = ps.executeQuery(create)

            if (rs.next()) {
                return true
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            ps?.close()
            rs?.close()
        }
        return false
    }
}