package solutions.mobiledev.reminder.data.reminder.addPhoneNumber

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import solutions.mobiledev.reminder.data.AppDatabase
import solutions.mobiledev.reminder.domain.entity.PhoneNumberItem
import solutions.mobiledev.reminder.domain.repository.PhoneNumberRepository

class PhoneNumberRepositoryImpl(application: Application) : PhoneNumberRepository {

    private val phoneNumberListDao = AppDatabase.getInstance(application).phoneNumberListDao()
    private val mapper = PhoneNumberListMapper()


    override fun getPhoneNumberList(): LiveData<List<PhoneNumberItem>> =
        MediatorLiveData<List<PhoneNumberItem>>()
            .apply {
                addSource(phoneNumberListDao.getPhoneNumberList()) {
                    value = mapper.mapListDbModelToListEntity(it)
                }
            }

    override fun getPhoneNumberWithRemId(remId: Int): LiveData<List<PhoneNumberItem>> =
        MediatorLiveData<List<PhoneNumberItem>>()
            .apply {
                addSource(phoneNumberListDao.getPhoneNumberWithRemId(remId)) {
                    value = mapper.mapListDbModelToListEntity(it)
                }
            }

    override suspend fun getPhoneNumberItem(phoneNumberItemId: Int): PhoneNumberItem {
        val dbModel = phoneNumberListDao.getPhoneNumberItem(phoneNumberItemId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override suspend fun addPhoneNumberItem(phoneNumberItem: PhoneNumberItem) {
        phoneNumberListDao.addPhoneNumberItem(mapper.mapEntityToDbModel(phoneNumberItem))
    }

    override suspend fun editPhoneNumberItem(phoneNumberItem: PhoneNumberItem) {
        phoneNumberListDao.addPhoneNumberItem(mapper.mapEntityToDbModel(phoneNumberItem))
    }

    override suspend fun deletePhoneNumberItem(phoneNumberItem: PhoneNumberItem) {
        phoneNumberListDao.deletePhoneNumberItem(phoneNumberItem.id)
    }

    override suspend fun deletePhoneNumberFromRemId(remId: Int) {
        phoneNumberListDao.deletePhoneNumberFromRemId(remId)
    }

    override suspend fun bindCurrentPhoneNumberToReminder(remId: Int) {
        phoneNumberListDao.bindCurrentPhoneNumberToReminder(remId)
    }

}