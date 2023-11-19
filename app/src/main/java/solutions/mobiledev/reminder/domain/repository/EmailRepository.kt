package solutions.mobiledev.reminder.domain.repository

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.EmailItem
import solutions.mobiledev.reminder.domain.entity.SmsItem

interface EmailRepository {

    fun getEmailList(): LiveData<List<EmailItem>>

    fun getEmailWithRemId(remId: Int): LiveData<List<EmailItem>>

    suspend fun getEmailItem(emailItemId: Int): EmailItem

    suspend fun addEmailItem(emailItem: EmailItem)

    suspend fun editEmailItem(emailItem: EmailItem)

    suspend fun deleteEmailItem(emailItem: EmailItem)

    suspend fun deleteEmailFromRemId(remId: Int)

    suspend fun bindCurrentEmailsToReminder(remId: Int)
}