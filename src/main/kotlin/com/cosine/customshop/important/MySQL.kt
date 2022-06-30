package com.cosine.customshop.important

import com.cosine.customshop.main.CustomShop
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.sql.SQLException
// Copyright 2022. 코사인(Cosine_A) all rights reserved.
class MySQL(plugin: CustomShop) {

    private val plugin: CustomShop
    private val cp: HikariCP
    private val items: ItemStackSerializer

    init {
        this.plugin = plugin
        cp = plugin.cp()
        items = plugin.item()
    }

    fun createShop(shop: String) {
        try {
            cp.getConnection().use { connection ->  connection.prepareStatement(plugin.getUrl()).use { ps ->
                val create = "create table if not exists $shop (슬롯 int, 구매가격 int, 판매가격 int, 아이템 varchar(1000));"
                ps.execute(create)
            }}
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
    fun deleteShop(shop: String) {
        try {
            cp.getConnection().use { connection ->  connection.prepareStatement(plugin.getUrl()).use { ps ->
                val create = "drop table $shop"
                ps.execute(create)
            }}
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
    fun existShop(shop: String): Boolean {
        val create = "select 1 from information_schema.tables where table_schema = '상점' and table_name = '$shop';"
        try {
            cp.getConnection().use { connection ->  connection.prepareStatement(plugin.getUrl()).use { ps -> ps.executeQuery(create).use { rs ->
                if (rs.next()) {
                    return true
                }
            }}}
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }
    fun setShopItem(shop: String, slot: Int, item: ItemStack) {
        val serializer = items.serialize(item)
        val insert = "insert into $shop (슬롯, 구매가격, 판매가격, 구매수량, 판매수량, 아이템) select ($slot, 0, 0, 1, 1, $serializer) from dual " +
                "where not exists (select 슬롯 from $shop where 슬롯 = '$slot');"
        try {
            cp.getConnection().use { connection ->  connection.prepareStatement(plugin.getUrl()).use { ps ->
                ps.executeUpdate(insert)
            }}
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
    fun updateShopItem(shop: String, slot: Int, item: ItemStack) {
        val serializer = items.serialize(item)
        val update = "update $shop set 아이템 = '$serializer' where 슬롯 = '$slot';"
        try {
            cp.getConnection().use { connection ->  connection.prepareStatement(plugin.getUrl()).use { ps ->
                ps.executeUpdate(update)
            }}
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
    fun deleteShopItem(shop: String, slot: Int) {
        val delete = "delete from $shop where 슬롯 = '$slot';"
        try {
            cp.getConnection().use { connection ->  connection.prepareStatement(plugin.getUrl()).use { ps ->
                ps.executeUpdate(delete)
            }}
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
    fun existShopItem(shop: String, slot: Int): Boolean {
        val create = "select 1 from $shop where 슬롯 = '$slot';"
        try {
            cp.getConnection().use { connection ->  connection.prepareStatement(plugin.getUrl()).use { ps -> ps.executeQuery(create).use { rs ->
                if (rs.next()) {
                    return true
                }
            }}}
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }
    fun getShopItem(shop: String, slot: Int): ItemStack {
        val select = "select 슬롯 from $shop where 슬롯 = '$slot';"
        try {
            cp.getConnection().use { connection ->  connection.prepareStatement(plugin.getUrl()).use { ps -> ps.executeQuery(select).use { rs ->
                if (rs.next()) {
                    return items.deserialize(rs.getString("아이템"))
                }
            }}}
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return ItemStack(Material.AIR)
    }
    fun getShopValue(column: String, shop: String, slot: Int): Int {
        val select = "select $column from $shop where 슬롯 = '$slot';"
        try {
            cp.getConnection().use { connection ->  connection.prepareStatement(plugin.getUrl()).use { ps -> ps.executeQuery(select).use { rs ->
                if (rs.next()) {
                    return rs.getInt(column)
                }
            }}}
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return 0
    }
    fun setShopValue(value: Int, column: String, shop: String, slot: Int) {
        val update = "update $shop set $column = '$value' where 슬롯 = '$slot';"
        try {
            cp.getConnection().use { connection ->  connection.prepareStatement(plugin.getUrl()).use { ps ->
                ps.executeUpdate(update)
            }}
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}