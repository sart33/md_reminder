package solutions.mobiledev.reminder.data.reminder.addSaveContact

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import solutions.mobiledev.reminder.data.AppDatabase
import solutions.mobiledev.reminder.domain.entity.SaveContactItem
import solutions.mobiledev.reminder.domain.repository.SaveContactRepository

class SaveContactRepositoryImpl(application: Application) : SaveContactRepository {

    private val saveContactListDao =
        AppDatabase.getInstance(application).saveContactListDao()
    private val mapper = SaveContactListMapper()

    override fun getSaveContactList(): LiveData<List<SaveContactItem>> =
        MediatorLiveData<List<SaveContactItem>>()
            .apply {
                addSource(saveContactListDao.getSaveContactList()) {
                    value = mapper.mapListDbModelToListEntity(it)
                }
            }

    override fun getSaveContactWithRemId(remId: Int): LiveData<List<SaveContactItem>> = MediatorLiveData<List<SaveContactItem>>()
        .apply {
            addSource(saveContactListDao.getSaveContactWithRemId(remId)) {
                value = mapper.mapListDbModelToListEntity(it)
            }
        }

    override suspend fun getSaveContactItem(saveContactItemId: Int): SaveContactItem {
        val dbModel = saveContactListDao.getSaveContactItem(saveContactItemId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override suspend fun addSaveContactItem(saveContactItem: SaveContactItem) {
        saveContactListDao.addSaveContactItem(mapper.mapEntityToDbModel(saveContactItem))
    }


    override suspend fun editSaveContactItem(saveContactItem: SaveContactItem) {
        saveContactListDao.addSaveContactItem(mapper.mapEntityToDbModel(saveContactItem))
    }

    override suspend fun deleteSaveContactItem(saveContactItem: SaveContactItem) {
        saveContactListDao.deleteSaveContactItem(saveContactItem.id)

    }
    override suspend fun deleteSaveContactFromRemId(remId: Int) {
        saveContactListDao.deleteSaveContactFromRemId(remId)
    }
    override suspend fun bindCurrentSaveContactToReminder(remId: Int) {
        saveContactListDao.bindCurrentSaveContactToReminder(remId)
    }

}