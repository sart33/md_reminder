package solutions.mobiledev.reminder.domain.repository

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.FileItem
import solutions.mobiledev.reminder.domain.entity.MelodyItem

interface MelodyRepository {


    fun getMelodyWithRemId(remId: Int): LiveData<List<MelodyItem>>

    suspend fun getMelodyUseRemId(remId: Int): MelodyItem

    suspend fun getMelodyItem(melodyItemId: Int): MelodyItem

    suspend fun addMelodyItem(melodyItem: MelodyItem)

    suspend fun editMelodyItem(melodyItem: MelodyItem)

    suspend fun deleteMelodyItem(melodyItem: MelodyItem)

    suspend fun deleteMelodyFromRemId(remId: Int)

    suspend fun bindCurrentMelodiesToReminder(remId: Int)
}