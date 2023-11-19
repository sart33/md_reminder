package solutions.mobiledev.reminder.domain.repository

import androidx.lifecycle.MutableLiveData
import solutions.mobiledev.reminder.domain.entity.ContactItem

interface ContactRepository {

    fun getContactList(): MutableLiveData<List<ContactItem>>

    fun getContactSelectList(): MutableLiveData<List<ContactItem>>

    fun getContactItem(contactItemId: Int): ContactItem

     fun addContactItem(contactItem: ContactItem)

    fun editContactItem(contactItem: ContactItem)

    fun deleteContactItem(contactItem: ContactItem)

     fun addContactItemToSelectList(contactItem: ContactItem)

    fun editContactItemOfSelectList(contactItem: ContactItem)

    fun deleteContactItemOfSelectList(contactItem: ContactItem)
}