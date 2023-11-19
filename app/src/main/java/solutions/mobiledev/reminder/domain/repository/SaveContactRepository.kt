package solutions.mobiledev.reminder.domain.repository

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.SaveContactItem

interface SaveContactRepository {

    fun getSaveContactList(): LiveData<List<SaveContactItem>>

    fun getSaveContactWithRemId(remId: Int): LiveData<List<SaveContactItem>>

    suspend fun getSaveContactItem(saveContactItemId: Int): SaveContactItem

    suspend fun addSaveContactItem(saveContactItem: SaveContactItem)

    suspend fun editSaveContactItem(saveContactItem: SaveContactItem)

    suspend fun deleteSaveContactItem(saveContactItem: SaveContactItem)

    suspend fun deleteSaveContactFromRemId(remId: Int)

    suspend fun bindCurrentSaveContactToReminder(remId: Int)
}