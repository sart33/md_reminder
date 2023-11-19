package solutions.mobiledev.reminder.domain.repository

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.EmailItem
import solutions.mobiledev.reminder.domain.entity.SmsItem

interface SmsRepository {

    fun getSmsList(): LiveData<List<SmsItem>>

    fun getSmsWithRemId(remId: Int): LiveData<List<SmsItem>>

    suspend fun getSmsItem(smsItemId: Int): SmsItem

    suspend fun addSmsItem( smsItem: SmsItem)

    suspend fun editSmsItem(smsItem: SmsItem)

    suspend fun deleteSmsItem(smsItem: SmsItem)

    suspend fun deleteSmsFromRemId(remId: Int)

    suspend fun bindCurrentSmsToReminder(remId: Int)


}
