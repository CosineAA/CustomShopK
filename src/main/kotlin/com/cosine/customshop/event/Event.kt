package com.cosine.customshop.event

import com.cosine.customshop.gui.Gui
import com.cosine.customshop.important.MySQL
import com.cosine.customshop.main.CustomShop
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

class Event(plugin: CustomShop): Listener {

    private val plugin: CustomShop
    private val sql: MySQL
    private val gui: Gui

    init {
        this.plugin = plugin
        sql = plugin.sql()
        gui = plugin.gui()
    }

    private val option: String = "§6§l[ 상점 ] §f§l"

    private val price: HashMap<UUID, MutableList<Any>> = HashMap()
    private val amount: HashMap<UUID, MutableList<Any>> = HashMap()

    @EventHandler
    fun setShop(event: InventoryClickEvent) {
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
        if (inventory.name.contains("§c§c§c")) {
            if (event.currentItem == null) return
            event.isCancelled = true

            val shop = replace(inventory.name)

            if (event.isLeftClick) {
                if (event.isShiftClick) {
                    amount[player.uniqueId] = getList(player, "구매 수량", shop, event.rawSlot, true)
                    return
                }
                price[player.uniqueId] = getList(player, "구매 가격", shop, event.rawSlot, true)
                return
            }
            if (event.isRightClick) {
                if (event.isShiftClick) {
                    amount[player.uniqueId] = getList(player, "판매 수량", shop, event.rawSlot, false)
                    return
                }
                price[player.uniqueId] = getList(player, "판매 가격", shop, event.rawSlot, false)
            }
        }
    }
    @EventHandler
    fun setShop2(event: AsyncPlayerChatEvent) {
        val player: Player = event.player
        val chat = event.message
        if (price.contains(player.uniqueId)) {
            val list: MutableList<Any>? = price[player.uniqueId]
            short(event, player, chat, list, "가격")
            price.remove(player.uniqueId)
        }
        if (amount.contains(player.uniqueId)) {
            val list: MutableList<Any>? = amount[player.uniqueId]
            short(event, player, chat, list, "수량")
            amount.remove(player.uniqueId)
        }
    }
    @EventHandler
    fun setShopItem(event: InventoryCloseEvent) {
        val player: Player = event.player as Player
        val inventory: Inventory = player.openInventory.topInventory
        // 상점 저장 - 아이템
        if (inventory.name.contains("§b§b§b")) {
            val shop = replace(inventory.name)

            for (loop: Int in 0..54) {
                val item: ItemStack? = event.inventory.getItem(loop)
                val sqlItem = sql.getShopItem(shop, loop)
                if (item == null) {
                    if (sql.existShopItem(shop, loop)) {
                        sql.deleteShopItem(shop, loop)
                    }
                    continue
                }
                if (item != sqlItem) {
                    sql.updateShopItem(shop, loop, item)
                } else {
                    sql.setShopItem(shop, loop, item)
                }
            }
        }
    }
    private fun short(event: AsyncPlayerChatEvent, player: Player, chat: String, list: MutableList<Any>?, choice: String) {
        if (!isInt(chat)) {
            player.sendMessage(option + "정수만 입력 가능합니다.")
            return
        }
        event.isCancelled = true
        val check: Boolean = list?.get(0) as Boolean
        val shop: String = list[1] as String
        val slot: Int = list[2] as Int

        if (check) {
            sql.setShopValue(chat.toInt(), "구매$choice", shop, slot)
            player.sendMessage(option + "구매 " + choice + "을 설정하였습니다.")
        } else {
            sql.setShopValue(chat.toInt(), "판매$choice", shop, slot)
            player.sendMessage(option + "판매 " + choice + "을 설정하였습니다.")
        }
    }
    private fun getList(player: Player, choice: String, shop: String, slot: Int, boolean: Boolean): MutableList<Any> {
        player.closeInventory()
        player.sendMessage(option + choice + "을 입력해주세요.")
        val list: MutableList<Any> = ArrayList()
        list.add(boolean)
        list.add(shop)
        list.add(slot)
        return list
    }
    private fun isInt(str: String): Boolean {
        return try {
            str.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
    private fun replace(value: String): String {
        val color = ChatColor.stripColor(value)
        return color.replace(" ", "").replace("설정", "")
    }
}