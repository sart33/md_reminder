package solutions.mobiledev.reminder.data.reminder.addEmail

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import solutions.mobiledev.reminder.data.AppDatabase
import solutions.mobiledev.reminder.domain.entity.EmailItem
import solutions.mobiledev.reminder.domain.repository.EmailRepository

class EmailRepositoryImpl(application: Application) : EmailRepository {
    private val emailListDao = AppDatabase.getInstance(application).emailListDao()
    private val mapper = EmailListMapper()

    override fun getEmailList(): LiveData<List<EmailItem>> = MediatorLiveData<List<EmailItem>>()
        .apply {
            addSource(emailListDao.getEmailList()) {
                value = mapper.mapListDbModelToListEntity(it)
            }
        }

    override suspend fun getEmailItem(emailItemId: Int): EmailItem {
        val dbModel = emailListDao.getEmailItem(emailItemId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override fun getEmailWithRemId(remId: Int): LiveData<List<EmailItem>> =
        MediatorLiveData<List<EmailItem>>()
            .apply {
                addSource(emailListDao.getEmailWithRemId(remId)) {
                    value = mapper.mapListDbModelToListEntity(it)
                }
            }

    override suspend fun addEmailItem(emailItem: EmailItem) {
        emailListDao.addEmailItem(mapper.mapEntityToDbModel(emailItem))
    }

    override suspend fun editEmailItem(emailItem: EmailItem) {
        emailListDao.addEmailItem(mapper.mapEntityToDbModel(emailItem))
    }

    override suspend fun deleteEmailItem(emailItem: EmailItem) {
        emailListDao.deleteEmailItem(emailItem.id)
    }

    override suspend fun bindCurrentEmailsToReminder(remId: Int) {
        emailListDao.bindCurrentEmailsToReminder(remId)
    }

    override suspend fun deleteEmailFromRemId(remId: Int) {
        emailListDao.deleteEmailFromRemId(remId)
    }


}