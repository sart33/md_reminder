package solutions.mobiledev.reminder.data

import androidx.lifecycle.MutableLiveData
import solutions.mobiledev.reminder.domain.entity.ContactItem
import solutions.mobiledev.reminder.domain.repository.ContactRepository

object ContactRepositoryImpl : ContactRepository {

    var contactListLD = MutableLiveData<List<ContactItem>>()
    var contactSelectListLD = MutableLiveData<List<ContactItem>>()
    var contactList = sortedSetOf<ContactItem>({ o1, o2 -> o1.id.compareTo(o2.id) })
    var selectedContacts = MutableList(
        2,
        init = { ContactItem(4, "Volodja", "0636456469", "volodja@mail.com","null",false)
            ContactItem(2, "Volodjda", "0636456ddd469", "volodjadd@mail.com","null",false) })
    private var autoIncrementId = 0


    override fun getContactList(): MutableLiveData<List<ContactItem>> {
        return contactListLD
    }

    override fun getContactSelectList(): MutableLiveData<List<ContactItem>> {
        return contactSelectListLD
    }


    override fun getContactItem(contactItemId: Int): ContactItem {
        return contactListLD.value?.find { it.id == contactItemId }
            ?: throw RuntimeException("Contact with id $contactItemId not found")
    }

    override fun addContactItem(contactItem: ContactItem) {
        if (contactItem.id == ContactItem.UNDEFINED_ID) {
            contactItem.id = autoIncrementId++
        }
        contactList.add(contactItem)
        updateList()
    }

    override fun editContactItem(contactItem: ContactItem) {
        val oldElement = getContactItem(contactItem.id)
        contactList.remove(oldElement)
        addContactItem(contactItem)

    }

    override fun deleteContactItem(contactItem: ContactItem) {
        contactList.remove(contactItem)
        addContactItem(contactItem)
    }

    private fun updateList() {
        contactListLD.value = contactList.toList()
    }

    override fun addContactItemToSelectList(contactItem: ContactItem) {
        if (contactItem.id == ContactItem.UNDEFINED_ID) {
            contactItem.id = autoIncrementId++
        }
        selectedContacts.add(contactItem)
        updateSelectList()
    }

    override fun editContactItemOfSelectList(contactItem: ContactItem) {
        val oldElement = getContactItem(contactItem.id)
        selectedContacts.remove(oldElement)
        addContactItem(contactItem)

    }

    override fun deleteContactItemOfSelectList(contactItem: ContactItem) {
        selectedContacts.remove(contactItem)
        addContactItem(contactItem)
    }

    private fun updateSelectList() {
        contactSelectListLD.value = selectedContacts
    }

}