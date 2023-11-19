package solutions.mobiledev.reminder.presentation.reminder

import androidx.recyclerview.widget.DiffUtil
import solutions.mobiledev.reminder.domain.entity.ReminderItem

class ReminderItemDiffCallback : DiffUtil.ItemCallback<ReminderItem>() {

    /**
     * сравнение по id
     */
    override fun areItemsTheSame(oldItem: ReminderItem, newItem: ReminderItem): Boolean {
        return oldItem.id == newItem.id
    }


    /**
     * Сравнение свех свойств объектов data класса
     */
    override fun areContentsTheSame(oldItem: ReminderItem, newItem: ReminderItem): Boolean {
        return oldItem == newItem
    }
}