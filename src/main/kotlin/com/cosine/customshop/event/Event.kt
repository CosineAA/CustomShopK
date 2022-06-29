package com.cosine.customshop.event

import com.cosine.customshop.gui.Gui
import com.cosine.customshop.main.CustomShop
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class Event(plugin: CustomShop): Listener {

    private val plugin: CustomShop
    private val gui: Gui

    init {
        this.plugin = plugin
        gui = plugin.gui()
    }

    @EventHandler
    fun shop(event: InventoryClickEvent) {
        val player: Player = event.whoClicked as Player
        val inventory: Inventory = player.openInventory.topInventory

        // 상점 설정 - 메인
        if (inventory.name.contains("§a§a§a")) {
            if (event.currentItem == null) return
            event.isCancelled = true

            val shop = replace(inventory.name)

            when (event.rawSlot) {
                2 -> {
                    gui.openShopItemSetting(player, shop)
                }
                6 -> {
                    gui.openShopPriceSetting(player, shop)
                }
            }
        }
    }
    @EventHandler
    fun shop2(event: InventoryCloseEvent) {
        val player: Player = event.player as Player
        val inventory: Inventory = player.openInventory.topInventory

        // 상점 저장 - 아이템
        if (inventory.name.contains("§b§b§b")) {
            val shop = replace(inventory.name)

        }
    }
    private fun replace(value: String): String {
        val color = ChatColor.stripColor(value)
        return color.replace(" ", "").replace("설정", "")
    }
}