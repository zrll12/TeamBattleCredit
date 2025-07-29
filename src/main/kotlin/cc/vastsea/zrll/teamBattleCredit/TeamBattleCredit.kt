package cc.vastsea.zrll.teamBattleCredit

import cc.vastsea.zrll.teamBattleCredit.events.PlayerDeath
import cc.vastsea.zrll.teamBattleCredit.events.PlayerRespawn
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin


class TeamBattleCredit : JavaPlugin() {

    override fun onEnable() {
        if (!server.pluginManager.isPluginEnabled("Kotlin")) {
            logger.severe("Kotlin plugin is required")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }
        luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)?.provider ?: run {
            logger.severe("LuckPerms plugin is required")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }
        instance = this

        Bukkit.getPluginManager().registerEvents(PlayerDeath(), this)
        Bukkit.getPluginManager().registerEvents(PlayerRespawn(), this)

        logger.info("Enabled ${this.name}")
    }

    override fun onDisable() {
        logger.info("Disabled ${this.name}")
    }

    companion object {
        lateinit var instance: TeamBattleCredit
        lateinit var luckPerms: LuckPerms
    }
}
