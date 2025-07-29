package cc.vastsea.zrll.teamBattleCredit.data

import kotlinx.serialization.Serializable

@Serializable
data class CreditRecord (
    val team: String,
    val credit: Int,
)