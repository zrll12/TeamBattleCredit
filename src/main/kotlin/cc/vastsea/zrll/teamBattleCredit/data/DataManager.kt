package cc.vastsea.zrll.teamBattleCredit.data

import cc.vastsea.zrll.teamBattleCredit.TeamBattleCredit
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.util.*


object DataManager {
    init {
        loadData()
    }

    lateinit var credit: List<CreditRecord>
    lateinit var activity: List<ActivityRecord>
    lateinit var cost: List<DeathCost>
    var defaultCredit: Int = 1000

    @OptIn(ExperimentalSerializationApi::class)
    fun loadData() {
        val folder = TeamBattleCredit.instance.dataFolder
        if (!folder.exists()) {
            folder.mkdirs()
        }

        credit = try {
            val creditFile = folder.resolve("credit.json")
            if (creditFile.exists()) {
                Json.decodeFromStream(creditFile.inputStream())
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            TeamBattleCredit.instance.logger.severe("Failed to load credit data: ${e.message}")
            emptyList()
        }

        activity = try {
            val activityFile = folder.resolve("activity.json")
            if (activityFile.exists()) {
                Json.decodeFromStream(activityFile.inputStream())
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            TeamBattleCredit.instance.logger.severe("Failed to load activity data: ${e.message}")
            emptyList()
        }

        defaultCredit = TeamBattleCredit.instance.config.getInt("defaultCredit", 1000)

        val costs = TeamBattleCredit.instance.config
            .getConfigurationSection("deathCost")?.getKeys(false) ?: emptySet()
        cost = costs.map { time ->
            val costValue = TeamBattleCredit.instance.config.getInt("deathCost.$time")
            DeathCost(time.toInt(), costValue)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun saveData() {
        val folder = TeamBattleCredit.instance.dataFolder
        if (!folder.exists()) {
            folder.mkdirs()
        }

        folder.resolve("credit.json").outputStream().use { outputStream ->
            Json.encodeToStream(credit, outputStream)
        }
        folder.resolve("activity.json").outputStream().use { outputStream ->
            Json.encodeToStream(activity, outputStream)
        }
    }

    fun recordDeath(team: String, player: UUID, deathMessage: String?) {
        var deathAmount = TeamBattleCredit.instance.config.getInt("defaultCost")
        try {
            deathAmount = cost.last {
                it.time <= Date().time / 1000
            }.cost
        } catch (_: Exception) {
        }

        val newActivity = ActivityRecord(
            team = team,
            player = player.toString(),
            deathMessage = deathMessage,
            amount = deathAmount,
            deathTime = System.currentTimeMillis().toString()
        )
        activity = activity + newActivity

        val currentCredit = credit.find { it.team == team }
        if (currentCredit == null) {
            credit = credit + CreditRecord(team, defaultCredit - deathAmount)
        } else {
            val updatedCredit = CreditRecord(team, currentCredit.credit - deathAmount)
            credit = credit.filter { it.team != team } + updatedCredit
        }

        TeamBattleCredit.instance.logger.info("Applied death cost: $deathAmount for team: $team, player: $player")

        saveData()
    }

    fun canRespawn(team: String): Boolean {
        val currentCredit = credit.find { it.team == team } ?: return true
        return currentCredit.credit >= 0
    }
}