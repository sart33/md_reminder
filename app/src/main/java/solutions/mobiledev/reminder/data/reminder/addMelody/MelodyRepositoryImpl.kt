package solutions.mobiledev.reminder.data.reminder.addMelody

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import solutions.mobiledev.reminder.data.AppDatabase
import solutions.mobiledev.reminder.domain.entity.MelodyItem
import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.repository.MelodyRepository

class MelodyRepositoryImpl(application: Application) : MelodyRepository {
    private val melodyListDao = AppDatabase.getInstance(application).melodyListDao()
    private val mapper = MelodyListMapper()

    override suspend fun getMelodyItem(melodyItemId: Int): MelodyItem {
        return mapper.mapDbModelToEntity(melodyListDao.getMelodyItem(melodyItemId))
    }

    override fun getMelodyWithRemId(remId: Int): LiveData<List<MelodyItem>> =
        MediatorLiveData<List<MelodyItem>>()
            .apply {
                addSource(melodyListDao.getMelodyWithRemId(remId)) {
                    value = mapper.mapListDbModelToListEntity(it)
                }
            }


    override suspend fun getMelodyUseRemId(remId: Int): MelodyItem {
        return mapper.mapDbModelToEntity(melodyListDao.getMelodyUseRemId(remId))

    }

    override suspend fun addMelodyItem(melodyItem: MelodyItem) {
        melodyListDao.addMelodyItem(mapper.mapEntityToDbModel(melodyItem))
    }

    override suspend fun editMelodyItem(melodyItem: MelodyItem) {
        melodyListDao.addMelodyItem(mapper.mapEntityToDbModel(melodyItem))
    }

    override suspend fun deleteMelodyItem(melodyItem: MelodyItem) {
        melodyListDao.deleteMelodyItem(melodyItem.id)
    }

    override suspend fun bindCurrentMelodiesToReminder(remId: Int) {
        melodyListDao.bindCurrentMelodiesToReminder(remId)
    }

    override suspend fun deleteMelodyFromRemId(remId: Int) {
        melodyListDao.deleteMelodyFromRemId(remId)
    }


}