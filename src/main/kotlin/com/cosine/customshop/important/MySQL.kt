package com.cosine.customshop.important

import com.cosine.customshop.main.CustomShop
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException


class MySQL(plugin: CustomShop) {

    private val plugin: CustomShop
    private val cp: HikariCP
    private val item: ItemStackSerializer

    init {
        this.plugin = plugin
        cp = plugin.cp()
        item = plugin.item()
    }

    fun createShop(shop: String) {
        var connection: Connection? = null
        var ps: PreparedStatement? = null
        try {
            connection = cp.getConnection()
            ps = connection.prepareStatement(plugin.getUrl())

            val create = "create table if not exists $shop (슬롯 int, 구매가격 int, 판매가격 int, 구매수량 int, 판매수량 int, 아이템 varchar(1000))"
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
        val create = "select 1 from information_schema.tables where table_schema = '상점' and table_name = '$shop' "
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
    fun getShopItem(slot: Int, shop: String): ItemStack {
        var connection: Connection? = null
        var ps: PreparedStatement? = null
        var rs: ResultSet? = null
        val create = "select 슬롯 from $shop where 슬롯 = '$slot'"
        try {
            connection = cp.getConnection()
            ps = connection.prepareStatement(plugin.getUrl())
            rs = ps.executeQuery(create)

            if (rs.next()) {
                return item.deserialize(rs.getString("아이템"))
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            ps?.close()
            rs?.close()
        }
        return ItemStack(Material.AIR)
    }
    fun getShopValue(column: String, slot: Int, shop: String): Int {
        var connection: Connection? = null
        var ps: PreparedStatement? = null
        var rs: ResultSet? = null
        val create = "select $column from $shop where 슬롯 = '$slot'"
        try {
            connection = cp.getConnection()
            ps = connection.prepareStatement(plugin.getUrl())
            rs = ps.executeQuery(create)

            if (rs.next()) {
                return rs.getInt(column)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            ps?.close()
            rs?.close()
        }
        return 0
    }
}