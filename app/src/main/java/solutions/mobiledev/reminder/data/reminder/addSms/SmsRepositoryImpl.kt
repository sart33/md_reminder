package solutions.mobiledev.reminder.data.reminder.addSms

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import solutions.mobiledev.reminder.data.AppDatabase
import solutions.mobiledev.reminder.domain.entity.SmsItem
import solutions.mobiledev.reminder.domain.repository.SmsRepository

class SmsRepositoryImpl(application: Application) : SmsRepository {

    private val smsListDao = AppDatabase.getInstance(application).smsListDao()
    private val mapper = SmsListMapper()


    override fun getSmsList(): LiveData<List<SmsItem>> = MediatorLiveData<List<SmsItem>>()
        .apply {
            addSource(smsListDao.getSmsList()) {
                value = mapper.mapListDbModelToListEntity(it)
            }
        }

    override fun getSmsWithRemId(remId: Int): LiveData<List<SmsItem>> =
        MediatorLiveData<List<SmsItem>>()
            .apply {
                addSource(smsListDao.getSmsWithRemId(remId)) {
                    value = mapper.mapListDbModelToListEntity(it)
                }
            }

    override suspend fun getSmsItem(smsItemId: Int): SmsItem {
        val dbModel = smsListDao.getSmsItem(smsItemId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override suspend fun addSmsItem(smsItem: SmsItem) {
        smsListDao.addSmsItem(mapper.mapEntityToDbModel(smsItem))
    }

    override suspend fun editSmsItem(smsItem: SmsItem) {
        smsListDao.addSmsItem(mapper.mapEntityToDbModel(smsItem))
    }

    override suspend fun deleteSmsItem(smsItem: SmsItem) {
        smsListDao.deleteSmsItem(smsItem.id)
    }

    override suspend fun deleteSmsFromRemId(remId: Int) {
        smsListDao.deleteSmsFromRemId(remId)
    }
    override suspend fun bindCurrentSmsToReminder(remId: Int) {
        smsListDao.bindCurrentSmsToReminder(remId)
    }

}