package com.cosine.customshop.command

import com.cosine.customshop.gui.Gui
import com.cosine.customshop.important.MySQL
import com.cosine.customshop.main.CustomShop
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
// Copyright 2022. 코사인(Cosine_A) all rights reserved.
class Command(plugin: CustomShop): CommandExecutor {

    private val plugin: CustomShop
    private val sql: MySQL
    private val gui: Gui

    init {
        this.plugin = plugin
        sql = plugin.sql()
        gui = plugin.gui()
    }

    private val option: String = "§6§l[ 상점 ] §f§l"

    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender is Player) {
            val player: Player = sender
            if (!player.isOp) {
                return false
            }
            if (args?.size == 0) {
                help(player)
                return false
            }
            when (args?.get(0)) {
                "생성" -> {
                    if (args.size == 1) {
                        player.sendMessage(option + "상점 이름을 적어주세요.")
                        return false
                    }
                    if (args.size == 2) {
                        if (sql.existShop(args[1])) {
                            player.sendMessage(option + "이미 존재하는 상점입니다.")
                            return false
                        }
                        if (args[1].contains("상점") || args[1].contains("설정")) {
                            player.sendMessage(option + "금지어가 포함되어 있습니다.")
                            return false
                        }
                        sql.createShop(args[1])
                        player.sendMessage(option + "상점을 생성하였습니다.")
                    }
                }
                "삭제" -> {
                    if (args.size == 1) {
                        player.sendMessage(option + "상점 이름을 적어주세요.")
                        return false
                    }
                    if (args.size == 2) {
                        if (!sql.existShop(args[1])) {
                            player.sendMessage(option + "존재하지 않는 상점입니다.")
                            return false
                        }
                        sql.deleteShop(args[1])
                        player.sendMessage(option + "상점을 삭제하였습니다.")
                    }
                }
                "설정" -> {
                    if (args.size == 1) {
                        player.sendMessage(option + "상점 이름을 적어주세요.")
                        return false
                    }
                    if (args.size == 2) {
                        if (!sql.existShop(args[1])) {
                            player.sendMessage(option + "존재하지 않는 상점입니다.")
                            return false
                        }
                        gui.openShopMainSetting(player, args[1])
                    }
                }
                "열기" -> {
                    if (args.size == 1) {
                        player.sendMessage(option + "상점 이름을 적어주세요.")
                        return false
                    }
                    if (args.size == 2) {
                        if (!sql.existShop(args[1])) {
                            player.sendMessage(option + "존재하지 않는 상점입니다.")
                            return false
                        }
                        gui.openShop(player, args[1])
                    }
                }
                "목록" -> {}
                else -> { help(player) }
            }
        }
        return false
    }
    private fun help(player: Player) {
        player.sendMessage(option + "상점 시스템 도움말")
        player.sendMessage(" ");
        player.sendMessage(option + "§f/상점 생성 [이름]")
        player.sendMessage(option + "§f/상점 삭제 [이름]")
        player.sendMessage(option + "§f/상점 설정 [이름]")
        player.sendMessage(option + "§f/상점 열기 [이름]")
        player.sendMessage(option + "§f/상점 목록")
        player.sendMessage(option + "§f/상점 리로드")
    }
}