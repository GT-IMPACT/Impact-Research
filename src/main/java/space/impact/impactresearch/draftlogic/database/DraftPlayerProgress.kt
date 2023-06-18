package space.impact.impactresearch.draftlogic.database

import com.google.gson.annotations.SerializedName

data class DraftPlayerProgress(
    @SerializedName("line") val line: DraftProgressList = DraftProgressList(emptyList()),
)

data class DraftProgressList(
    @SerializedName("data") val data: List<DraftProgress>,
)

data class DraftProgress(
    @SerializedName("id") val id: Int,
)

fun DraftPlayerProgress?.isConfirm(key: Int): Boolean {
    return this?.line?.data?.find { it.id == key } != null
}
