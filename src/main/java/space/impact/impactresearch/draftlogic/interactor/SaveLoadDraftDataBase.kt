package space.impact.impactresearch.draftlogic.interactor

import com.google.gson.GsonBuilder
import space.impact.impactresearch.draftlogic.database.DraftPlayerProgress
import space.impact.impactresearch.draftlogic.ingame.DraftManager
import java.io.File

private const val PROGRESS_DIRECTORY = "progress"

private val GSON = GsonBuilder()
    .setPrettyPrinting()
    .create()

fun loadDraftDataBase() {
    val progressDir = File(DraftInteractor.draftDirectory, PROGRESS_DIRECTORY)
    progressDir.mkdir()
    progressDir.listFiles()?.also { playersProgress ->
        playersProgress.forEach { file ->
            val json = file.readText()
            DraftManager.DRAFT_PROGRESS[file.nameWithoutExtension] = GSON.fromJson(json, DraftPlayerProgress::class.java)
        }
    }
}

fun saveDraftDataBase() {
    val progressDir = File(DraftInteractor.draftDirectory, PROGRESS_DIRECTORY)
    progressDir.mkdir()
    DraftManager.DRAFT_PROGRESS.forEach { (player, progress) ->
        val newFile = File(progressDir, "$player.json")
        newFile.createNewFile()
        val json = GSON.toJson(progress)
        newFile.writeText(json)
    }
}

fun saveSingle(player: String, progress: DraftPlayerProgress) {
    val progressDir = File(DraftInteractor.draftDirectory, PROGRESS_DIRECTORY)
    progressDir.mkdir()
    val newFile = File(progressDir, "$player.json")
    newFile.createNewFile()
    val json = GSON.toJson(progress)
    newFile.writeText(json)
}
