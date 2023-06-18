package space.impact.impactresearch.draftlogic.database

import com.google.gson.annotations.SerializedName
import space.impact.impactresearch.draftlogic.ingame.DraftData

data class DraftDataBase(
    @SerializedName("line") val line: DataBaseLine,
    @SerializedName("properties") val properties: DataBaseProperties,
)

data class DataBaseLine(
    @SerializedName("drafts") val drafts: List<DataBaseDraftData>
)

data class DataBaseDraftData(
    @SerializedName("draft") val draft: DraftData,
)

data class DataBaseProperties(
    @SerializedName("version") val version: Int,
)
