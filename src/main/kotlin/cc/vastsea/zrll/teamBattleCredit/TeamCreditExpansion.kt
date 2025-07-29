package cc.vastsea.zrll.teamBattleCredit

import cc.vastsea.zrll.teamBattleCredit.data.DataManager
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player


class TeamCreditExpansion : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "TeamBattleCredit"
    }

    override fun getAuthor(): String {
        return "zrll"
    }

    override fun getVersion(): String {
        return "0.1"
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (player == null) return null
        
        // 获取玩家所在队伍
        val user = TeamBattleCredit.luckPerms.userManager.getUser(player.uniqueId) ?: return null
        val playerTeam = user.primaryGroup
        
        // 查看自己队伍分数
        if (params == "self_team") {
            return playerTeam
        }
        
        if (params == "self_credit") {
            val teamCredit = DataManager.credit.find { it.team == playerTeam }
            return teamCredit?.credit?.toString() ?: DataManager.defaultCredit.toString()
        }
        
        // 查看其他队伍分数
        if (params.startsWith("other_credit_")) {
            val teamName = params.substring("other_credit_".length)
            val teamCredit = DataManager.credit.find { it.team == teamName }
            return teamCredit?.credit?.toString() ?: DataManager.defaultCredit.toString()
        }
        
        return null
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        return onRequest(player, params)
    }
}