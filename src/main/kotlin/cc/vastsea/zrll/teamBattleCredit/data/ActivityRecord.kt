package cc.vastsea.zrll.teamBattleCredit.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ActivityRecord(
    val team: String,
    val player: String,
    val deathMessage: String?,
    val amount: Int,
    val deathTime: String,
)
