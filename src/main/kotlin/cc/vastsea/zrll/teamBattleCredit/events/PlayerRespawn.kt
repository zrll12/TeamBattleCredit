package cc.vastsea.zrll.teamBattleCredit.events

import cc.vastsea.zrll.teamBattleCredit.TeamBattleCredit
import cc.vastsea.zrll.teamBattleCredit.data.DataManager
import net.luckperms.api.model.user.User
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerRespawn : Listener {
    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player: User = TeamBattleCredit.luckPerms.userManager.getUser(event.player.uniqueId)!!

        if (!DataManager.canRespawn(player.primaryGroup)) {
            event.player.gameMode = GameMode.SPECTATOR
            event.player.sendTitle("您的队伍点数耗尽，无法复活", "当前为观战模式")
        }

        TeamBattleCredit.instance.logger.info("${player.primaryGroup} player ${event.player.name} has respawned.")
    }
}