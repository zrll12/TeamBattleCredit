package cc.vastsea.zrll.teamBattleCredit.commands

import cc.vastsea.zrll.teamBattleCredit.TeamBattleCredit
import cc.vastsea.zrll.teamBattleCredit.data.CreditRecord
import cc.vastsea.zrll.teamBattleCredit.data.DataManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class CreditCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("§c用法: /credit <give|remove|reload>")
            return true
        }

        when (args[0].lowercase()) {
            "reload" -> {
                if (!sender.hasPermission("teambattlecredit.reload")) {
                    sender.sendMessage("§c你没有权限执行此命令")
                    return true
                }
                DataManager.loadData()
                sender.sendMessage("§a配置已重新加载")
                return true
            }
            "give" -> {
                if (!sender.hasPermission("teambattlecredit.give")) {
                    sender.sendMessage("§c你没有权限执行此命令")
                    return true
                }
                if (args.size < 3) {
                    sender.sendMessage("§c用法: /credit give <队伍> <数量>")
                    return true
                }
                val team = args[1]
                val amount = args[2].toIntOrNull()
                if (amount == null) {
                    sender.sendMessage("§c数量必须是一个整数")
                    return true
                }

                val newCredit = DataManager.addCredit(team, amount)
                sender.sendMessage("§a已为队伍 $team 添加 $amount 积分，当前积分: $newCredit")
                return true
            }
            "remove" -> {
                if (!sender.hasPermission("teambattlecredit.remove")) {
                    sender.sendMessage("§c你没有权限执行此命令")
                    return true
                }
                if (args.size < 3) {
                    sender.sendMessage("§c用法: /credit remove <队伍> <数量>")
                    return true
                }
                val team = args[1]
                val amount = args[2].toIntOrNull()
                if (amount == null) {
                    sender.sendMessage("§c数量必须是一个整数")
                    return true
                }

                val newCredit = DataManager.removeCredit(team, amount)
                sender.sendMessage("§a已从队伍 $team 移除 $amount 积分，当前积分: $newCredit")
                return true
            }
            else -> {
                sender.sendMessage("§c未知的子命令: ${args[0]}")
                return true
            }
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            val subCommands = mutableListOf<String>()
            if (sender.hasPermission("teambattlecredit.reload")) subCommands.add("reload")
            if (sender.hasPermission("teambattlecredit.give")) subCommands.add("give")
            if (sender.hasPermission("teambattlecredit.remove")) subCommands.add("remove")
            return subCommands.filter { it.startsWith(args[0].lowercase()) }
        }

        if (args.size == 2 && (args[0].equals("give", ignoreCase = true) || args[0].equals("remove", ignoreCase = true))) {
            // 获取所有队伍名称用于Tab补全
            val teams = DataManager.credit.map { it.team }
            // 获取LuckPerms中的所有组名
            val groups = TeamBattleCredit.luckPerms.groupManager.loadedGroups.map { it.name }
            return (teams + groups).distinct().filter { it.startsWith(args[1], ignoreCase = true) }
        }

        return emptyList()
    }
}