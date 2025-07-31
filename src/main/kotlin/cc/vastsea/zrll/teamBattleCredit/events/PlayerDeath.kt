package cc.vastsea.zrll.teamBattleCredit.events

import cc.vastsea.zrll.teamBattleCredit.TeamBattleCredit
import cc.vastsea.zrll.teamBattleCredit.data.DataManager
import net.luckperms.api.model.user.User
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerDeath : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player: User = TeamBattleCredit.luckPerms.userManager.getUser(event.player.uniqueId)!!
        DataManager.recordDeath(player.primaryGroup, player.uniqueId, event.deathMessage)

        if (!DataManager.canRespawn(player.primaryGroup)) {
            val causingPlayer = event.entity.killer
            if (causingPlayer != null) {
                Bukkit.getOnlinePlayers().forEach {
                    it.sendMessage("§c${event.player.name} 成为了 ${causingPlayer.name} 的最终击杀.")
                }
            }
        }

        TeamBattleCredit.instance.logger.info("Player ${event.player.name} of team ${player.primaryGroup} has died.")
    }
}