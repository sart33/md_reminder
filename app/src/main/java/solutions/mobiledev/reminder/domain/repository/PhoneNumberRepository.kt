package solutions.mobiledev.reminder.domain.repository

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.PhoneNumberItem
import solutions.mobiledev.reminder.domain.entity.SmsItem

interface PhoneNumberRepository {

    fun getPhoneNumberList(): LiveData<List<PhoneNumberItem>>

    fun getPhoneNumberWithRemId(remId: Int): LiveData<List<PhoneNumberItem>>

    suspend fun getPhoneNumberItem(phoneNumberItemId: Int): PhoneNumberItem

    suspend fun addPhoneNumberItem( phoneNumberItem: PhoneNumberItem)

    suspend fun editPhoneNumberItem(phoneNumberItem: PhoneNumberItem)

    suspend fun deletePhoneNumberItem(phoneNumberItem: PhoneNumberItem)

    suspend fun deletePhoneNumberFromRemId(remId: Int)

    suspend fun bindCurrentPhoneNumberToReminder(remId: Int)
}