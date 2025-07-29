package cc.vastsea.zrll.teamBattleCredit.events

import cc.vastsea.zrll.teamBattleCredit.TeamBattleCredit
import cc.vastsea.zrll.teamBattleCredit.data.DataManager
import net.luckperms.api.model.user.User
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerDeath : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player: User = TeamBattleCredit.luckPerms.userManager.getUser(event.player.uniqueId)!!
        DataManager.recordDeath(player.primaryGroup, player.uniqueId, event.deathMessage)

        TeamBattleCredit.instance.logger.info("${player.primaryGroup} player ${event.player.name} has died.")
    }
}