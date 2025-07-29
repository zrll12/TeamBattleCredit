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

    /**
     * 增加队伍积分
     * @param team 队伍名称
     * @param amount 增加的积分数量
     * @param player 可选的玩家UUID
     * @param message 可选的消息
     * @return 更新后的积分
     */
    fun addCredit(team: String, amount: Int, player: UUID? = null, message: String? = null): Int {
        val currentCredit = credit.find { it.team == team }
        val newCreditValue = if (currentCredit == null) {
            defaultCredit + amount
        } else {
            currentCredit.credit + amount
        }
        
        // 更新积分记录
        if (currentCredit == null) {
            credit = credit + CreditRecord(team, newCreditValue)
        } else {
            val updatedCredit = CreditRecord(team, newCreditValue)
            credit = credit.filter { it.team != team } + updatedCredit
        }
        
        // 记录活动
        if (player != null) {
            val newActivity = ActivityRecord(
                team = team,
                player = player.toString(),
                deathMessage = message,
                amount = amount,
                deathTime = System.currentTimeMillis().toString()
            )
            activity = activity + newActivity
        }
        
        TeamBattleCredit.instance.logger.info("Added credit: $amount for team: $team, new credit: $newCreditValue")
        saveData()
        return newCreditValue
    }
    
    /**
     * 减少队伍积分
     * @param team 队伍名称
     * @param amount 减少的积分数量
     * @param player 可选的玩家UUID
     * @param message 可选的消息
     * @return 更新后的积分
     */
    fun removeCredit(team: String, amount: Int, player: UUID? = null, message: String? = null): Int {
        val currentCredit = credit.find { it.team == team }
        val newCreditValue = if (currentCredit == null) {
            defaultCredit - amount
        } else {
            currentCredit.credit - amount
        }
        
        // 更新积分记录
        if (currentCredit == null) {
            credit = credit + CreditRecord(team, newCreditValue)
        } else {
            val updatedCredit = CreditRecord(team, newCreditValue)
            credit = credit.filter { it.team != team } + updatedCredit
        }
        
        // 记录活动
        if (player != null) {
            val newActivity = ActivityRecord(
                team = team,
                player = player.toString(),
                deathMessage = message,
                amount = -amount, // 使用负值表示减少
                deathTime = System.currentTimeMillis().toString()
            )
            activity = activity + newActivity
        }
        
        TeamBattleCredit.instance.logger.info("Removed credit: $amount from team: $team, new credit: $newCreditValue")
        saveData()
        return newCreditValue
    }
    
    fun recordDeath(team: String, player: UUID, deathMessage: String?) {
        var deathAmount = TeamBattleCredit.instance.config.getInt("defaultCost")
        try {
            deathAmount = cost.last {
                it.time <= Date().time / 1000
            }.cost
        } catch (_: Exception) {
        }

        // 使用removeCredit函数减少积分并记录活动
        removeCredit(team, deathAmount, player, deathMessage)
        
        TeamBattleCredit.instance.logger.info("Applied death cost: $deathAmount for team: $team, player: $player")
    }

    fun canRespawn(team: String): Boolean {
        val currentCredit = credit.find { it.team == team } ?: return true
        return currentCredit.credit >= 0
    }
}