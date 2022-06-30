package com.cosine.customshop.important

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
// Copyright 2022. 코사인(Cosine_A) all rights reserved.
class ItemStackSerializer {

    fun serialize(item: ItemStack): String {
        val builder = StringBuilder()
        builder.append(item.type.toString())
        if (item.durability.toInt() != 0) builder.append(":" + item.durability)
        builder.append(" " + item.amount)
        for (enchant in item.enchantments.keys) builder.append(" " + enchant.name + ":" + item.enchantments[enchant])
        val name = getName(item)
        if (name != null) builder.append(" name:$name")
        val lore = getLore(item)
        if (lore != null) builder.append(" lore:$lore")
        val color: Color? = getArmorColor(item)
        if (color != null) builder.append(" rgb:" + color.red + "|" + color.green + "|" + color.blue)
        val owner = getOwner(item)
        if (owner != null) builder.append(" owner:$owner")
        return builder.toString()
    }

    fun deserialize(serializedItem: String): ItemStack {
        val strings = serializedItem.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val enchants: MutableMap<Enchantment, Int> = HashMap()
        var args: Array<String>
        val item = ItemStack(Material.AIR)
        for (str in strings) {
            args = str.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (Material.matchMaterial(args[0]) != null && item.type == Material.AIR) {
                item.type = Material.matchMaterial(args[0])
                if (args.size == 2) item.durability = args[1].toShort()
                break
            }
        }
        if (item.type == Material.AIR) {
            Bukkit.getLogger().info("Could not find a valid material for the item in \"$serializedItem\"")
            return ItemStack(Material.AIR)
        }
        for (str in strings) {
            args = str.split(":".toRegex(), limit = 2).toTypedArray()
            if (isNumber(args[0])) item.amount = args[0].toInt()
            if (args.size == 1) continue
            if (args[0].equals("name", ignoreCase = true)) {
                setName(item, ChatColor.translateAlternateColorCodes('&', args[1]))
                continue
            }
            if (args[0].equals("lore", ignoreCase = true)) {
                setLore(item, ChatColor.translateAlternateColorCodes('&', args[1]))
                continue
            }
            if (args[0].equals("rgb", ignoreCase = true)) {
                setArmorColor(item, args[1])
                continue
            }
            if (args[0].equals("owner", ignoreCase = true)) {
                setOwner(item, args[1])
                continue
            }
            if (Enchantment.getByName(args[0].uppercase(Locale.getDefault())) != null) {
                enchants[Enchantment.getByName(args[0].uppercase(Locale.getDefault()))] = args[1].toInt()
            }
        }
        item.addUnsafeEnchantments(enchants)
        return if (item.type == Material.AIR) ItemStack(Material.AIR) else item
    }

    private fun getOwner(item: ItemStack): String? {
        return if (item.itemMeta !is SkullMeta) null else (item.itemMeta as SkullMeta).owner
    }

    private fun setOwner(item: ItemStack, owner: String) {
        try {
            val meta = item.itemMeta as SkullMeta
            meta.owner = owner
            item.itemMeta = meta
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun getName(item: ItemStack): String? {
        if (!item.hasItemMeta()) return null
        return if (!item.itemMeta.hasDisplayName()) null else item.itemMeta.displayName.replace(" ", "_")
            .replace(ChatColor.COLOR_CHAR, '&')
    }

    private fun setName(item: ItemStack, name: String) {
        val name2 = name.replace("_", " ")
        val meta = item.itemMeta
        meta.displayName = name2
        item.itemMeta = meta
    }

    private fun getLore(item: ItemStack): String? {
        if (!item.hasItemMeta()) return null
        if (!item.itemMeta.hasLore()) return null
        val builder = StringBuilder()
        val lore = item.itemMeta.lore
        for (ind in lore.indices) {
            builder.append((if (ind > 0) "|" else "") + lore[ind].replace(" ", "_").replace(ChatColor.COLOR_CHAR, '&'))
        }
        return builder.toString()
    }

    private fun setLore(item: ItemStack, lore: String) {
        val lore2 = lore.replace("_", " ")
        val list: List<String> = lore2.split("\\|")
        val meta = item.itemMeta
        meta.lore = list
        item.itemMeta = meta
    }

    private fun getArmorColor(item: ItemStack): Color? {
        return if (item.itemMeta !is LeatherArmorMeta) null else (item.itemMeta as LeatherArmorMeta).color
    }

    private fun setArmorColor(item: ItemStack, str: String) {
        try {
            val colors = str.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val red = colors[0].toInt()
            val green = colors[1].toInt()
            val blue = colors[2].toInt()
            val meta = item.itemMeta as LeatherArmorMeta
            meta.color = Color.fromRGB(red, green, blue)
            item.itemMeta = meta
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun isNumber(str: String): Boolean {
        try {
            str.toInt()
        } catch (exception: NumberFormatException) {
            return false
        }
        return true
    }
}