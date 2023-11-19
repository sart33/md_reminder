package solutions.mobiledev.reminder.presentation.contactsEmail

import androidx.recyclerview.widget.DiffUtil
import solutions.mobiledev.reminder.domain.entity.ContactItem

class ContactItemDiffCallback : DiffUtil.ItemCallback<ContactItem>() {


    /**
     * сравнение по id
     */
    override fun areItemsTheSame(oldItem: ContactItem, newItem: ContactItem): Boolean {
        return oldItem.id == newItem.id
    }

    /**
     * Сравнение свех свойств объектов data класса
     */
    override fun areContentsTheSame(oldItem: ContactItem, newItem: ContactItem): Boolean {
        return oldItem == newItem
    }
}