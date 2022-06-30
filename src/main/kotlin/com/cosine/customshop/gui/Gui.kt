package com.cosine.customshop.gui

import com.cosine.customshop.important.MySQL
import com.cosine.customshop.main.CustomShop
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.material.MaterialData
// Copyright 2022. 코사인(Cosine_A) all rights reserved.
class Gui(plugin: CustomShop) {

    private val sql: MySQL

    init {
        sql = plugin.sql()
    }

    fun openShopMainSetting(player: Player, shop: String) {
        val inventory: Inventory = Bukkit.createInventory(null, 9, "$shop 설정§a§a§a")

        val a = "§f각 아이템의 가격을 설정합니다."
        val b = "§f좌클릭 시 §a구매 §f가격을 설정합니다."
        val c = "§f우클릭 시 §c판매 §f가격을 설정합니다."
        val d = "§f쉬프트 좌클릭 시 §a구매 §f가격 수량을 정합니다."
        val e = "§f쉬프트 우클릭 시 §c판매 §f가격 수량을 정합니다."

        set("§6§l아이템 설정", listOf("§f아이템을 넣고 닫으면 저장됩니다."), 54, 0, 1, 2, inventory)
        set("§b§l가격 설정", listOf(a, " ", b, c, " ", d, e), 58, 0, 1, 6, inventory)
        player.openInventory(inventory)
    }
    fun openShopItemSetting(player: Player, shop: String) {
        val inventory: Inventory = Bukkit.createInventory(null, 54, "$shop 설정§b§b§b")
        for (loop: Int in 0..54) {
            val item: ItemStack = sql.getShopItem(shop, loop)
            if (item.type == Material.AIR) { continue }
            inventory.setItem(loop, item)
        }
        player.openInventory(inventory)
    }
    fun openShopPriceSetting(player: Player, shop: String) {
        val inventory: Inventory = Bukkit.createInventory(null, 54, "$shop 설정§c§c§c")
        setLore(inventory, shop)
        player.openInventory(inventory)
    }
    fun openShop(player: Player, shop: String) {
        val inventory: Inventory = Bukkit.createInventory(null, 54, "$shop 상점§d§d§d")
        setLore(inventory, shop)
        player.openInventory(inventory)
    }
    private fun setLore(inventory: Inventory, shop: String) {
        for (loop: Int in 0..54) {
            val item = ItemStack(sql.getShopItem(shop, loop))
            if (item.type == Material.AIR) { return }
            val meta: ItemMeta = item.itemMeta
            val lore: MutableList<String> = meta.lore

            val buy: String = getPrice("구매", loop, shop)
            val sell: String = getPrice("판매", loop, shop)

            lore.add("§a§l[ §f구매 §a§l] §e§l[§f 구매가격 §e§l] §f: $buy")
            lore.add(" §7§l[§f 좌클릭 : 1개 구매 §8|§f 쉬프트+좌클릭 : 64개 구매 §7§l]")
            lore.add("§c§l[ §f판매 §c§l] §e§l[§f 판매가격 §e§l] §f: $sell")
            lore.add(" §7§l[§f 우클릭 : 1개 판매 §8|§f 쉬프트+우클릭 : 전체 판매 §7§l]")

            meta.lore = lore
            item.itemMeta = meta

            inventory.setItem(loop, item)
        }
    }
    private fun getPrice(choice: String, loop: Int, shop: String): String {
        val price: Int = sql.getShopValue(choice + "가격", shop, loop)
        if (price == 0) {
            return choice + "불가"
        }
        return price.toString()
    }
    private fun set(display: String?, lore: List<String?>?, ID: Int, data: Int, stack: Int, loc: Int, inv: Inventory) {
        val item = MaterialData(ID, data.toByte()).toItemStack(stack)
        val meta = item.itemMeta
        meta.displayName = display
        meta.lore = lore
        item.itemMeta = meta
        inv.setItem(loc, item)
    }
}