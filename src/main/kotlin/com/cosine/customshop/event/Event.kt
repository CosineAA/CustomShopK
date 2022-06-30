package com.cosine.customshop.event

import com.cosine.customshop.gui.Gui
import com.cosine.customshop.important.MySQL
import com.cosine.customshop.main.CustomShop
import net.milkbowl.vault.economy.Economy
import org.bukkit.*
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
        // 상점 설정 - 가격
        if (inventory.name.contains("§c§c§c")) {
            if (event.currentItem == null) return
            event.isCancelled = true

            val shop = replace(inventory.name)

            if (event.isLeftClick) {
                price[player.uniqueId] = getList(player, "구매 가격", shop, event.rawSlot, true)
                return
            }
            if (event.isRightClick) {
                price[player.uniqueId] = getList(player, "판매 가격", shop, event.rawSlot, false)
                return
            }
        }
        if (inventory.name.contains("§d§d§d")) {
            if (event.currentItem == null) return
            event.isCancelled = true

            val shop = replace(inventory.name)

            val economy: Economy? = CustomShop.getEconomy()
            val offline: OfflinePlayer = Bukkit.getOfflinePlayer(player.uniqueId)
            val playerBank: Double? = economy?.getBalance(offline)

            val item = ItemStack(sql.getShopItem(shop, event.rawSlot))
            val slot: Int = event.rawSlot

            if (event.isLeftClick) {
                if(inventory.firstEmpty() == -1) {
                    player.sendMessage(option + "인벤토리의 공간이 충분하지 않습니다.")
                    return
                }
                val buyPrice: Int = sql.getShopValue("구매가격", shop, slot)
                if (playerBank == null) {
                    player.sendMessage(option + "플레이어의 금고를 찾을 수 없습니다.")
                    return
                }
                if (event.isShiftClick) {
                    if (playerBank < buyPrice * 64) {
                        player.sendMessage(option + "돈이 부족합니다.")
                        return
                    }
                    buyItem(player, item, 64, buyPrice, economy, offline)
                    return
                }
                if (playerBank < buyPrice) {
                    player.sendMessage(option + "돈이 부족합니다.")
                    return
                }
                buyItem(player, item, 1, buyPrice, economy, offline)
                return
            }
            if (event.isRightClick) {
                val sellPrice: Int = sql.getShopValue("판매가격", shop, slot)
                if (event.isShiftClick) {
                    if (!player.inventory.containsAtLeast(item, 1)) {
                        player.sendMessage(option + "판매할 아이템이 부족합니다.")
                        return
                    }
                    sellItem(player, item, sellPrice, economy, offline, "전체")
                    return
                }
                sellItem(player, item, sellPrice, economy, offline, "1개")
            }
        }
    }
    private fun countItem(player: Player, item: ItemStack): Int {
        var count = 0
        val inventory: Inventory = player.inventory
        for (items: ItemStack in inventory.all(item).values) {
            if (items.type == item.type) {
                count += items.amount
            }
        }
        return count;
    }
    private fun sellItem(player: Player, item: ItemStack, sellPrice: Int, economy: Economy?, offline: OfflinePlayer, choice: String) {
        var count = 0
        count = if (choice == "전체") countItem(player, item) else 1
        item.amount = count
        player.inventory.removeItem(item)

        economy?.depositPlayer(offline, sellPrice.toDouble() * count)

        val money = economy?.format(economy.getBalance(offline))

        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1F, 2F)
        player.sendMessage(option + "아이템을 $choice 판매하였습니다. §7[돈: $money]")
    }
    private fun buyItem(player: Player, item: ItemStack, buyAmount: Int, buyPrice: Int, economy: Economy, offline: OfflinePlayer) {
        item.amount = buyAmount
        player.inventory.addItem(item)

        economy.withdrawPlayer(offline, buyPrice.toDouble() * buyAmount)

        val money = economy.format(economy.getBalance(offline))

        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1F, 2F)
        player.sendMessage(option + "아이템을 " + buyAmount + "개를 구매하였습니다. §7[돈: $money)]");
    }
    @EventHandler
    fun setShop2(event: AsyncPlayerChatEvent) {
        val player: Player = event.player
        val chat = event.message
        if (price.contains(player.uniqueId)) {
            val list: MutableList<Any>? = price[player.uniqueId]
            short(event, player, chat, list)
            price.remove(player.uniqueId)
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
    private fun short(event: AsyncPlayerChatEvent, player: Player, chat: String, list: MutableList<Any>?) {
        if (!isInt(chat)) {
            player.sendMessage(option + "정수만 입력 가능합니다.")
            return
        }
        event.isCancelled = true
        val check: Boolean = list?.get(0) as Boolean
        val shop: String = list[1] as String
        val slot: Int = list[2] as Int

        if (check) {
            sql.setShopValue(chat.toInt(), "구매가격", shop, slot)
            player.sendMessage(option + "구매 가격을 설정하였습니다.")
        } else {
            sql.setShopValue(chat.toInt(), "판매가격", shop, slot)
            player.sendMessage(option + "판매 가격을 설정하였습니다.")
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
        return color.replace(" ", "").replace("설정", "").replace("상점", "")
    }
}