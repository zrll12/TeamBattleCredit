package cc.vastsea.zrll.teamBattleCredit.events

import cc.vastsea.zrll.teamBattleCredit.TeamBattleCredit
import net.luckperms.api.model.user.User
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerRespawn : Listener {
    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player: User = TeamBattleCredit.luckPerms.userManager.getUser(event.player.uniqueId)!!

        TeamBattleCredit.instance.logger.info("${player.primaryGroup} player ${event.player.name} has respawned.")
    }
}