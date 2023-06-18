package space.impact.impactresearch.draftlogic.interactor

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraftforge.common.DimensionManager
import space.impact.impactresearch.ImpactResearch
import space.impact.impactresearch.draftlogic.database.DraftPlayerProgress
import space.impact.impactresearch.draftlogic.ingame.DraftManager
import java.io.File

object DraftInteractor {

    private const val DRAFT_DIRECTORY_NAME = "draftresearch"

    private var worldDirectory: File? = null

    internal val draftDirectory by lazy {
        File(worldDirectory, DRAFT_DIRECTORY_NAME)
    }

    private val ceh = CoroutineExceptionHandler { _, e ->
        if (ImpactResearch.IS_DEBUG) e.printStackTrace()
    }
    private val scope = CoroutineScope(Dispatchers.IO + ceh)

    fun serverAboutToStart() {
        worldDirectory = DimensionManager.getCurrentSaveRootDirectory()
        draftDirectory.mkdir()
        loadDraftDB()
        DraftManager.DRAFT_PROGRESS.clear()
    }

    fun serverStopped() {
        DraftManager.DRAFT_PROGRESS.clear()
        saveDraftDB()
        worldDirectory = null
    }

    private fun loadDraftDB() {
        scope.launch {
            loadDraftDataBase()
        }
    }

    private fun saveDraftDB() {
        scope.launch {
            saveDraftDataBase()
        }
    }

    internal fun saveSingleDraft(player: String, progress: DraftPlayerProgress) {
        scope.launch {
            saveSingle(player, progress)
        }
    }
}
