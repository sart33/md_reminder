package solutions.mobiledev.reminder.presentation.reminder

import androidx.recyclerview.widget.DiffUtil
import solutions.mobiledev.reminder.domain.entity.ReminderItem

class ReminderListDiffCallback(
    private val oldList: List<ReminderItem>,
    private val newList: List<ReminderItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }


    override fun getNewListSize(): Int {
        return newList.size
    }


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].id == oldList[oldItemPosition].id
    }

    /**
     * У дата классов  - мотод equals переопределен и при таком сравнении - будут сравниваться все
     * поля (свойства) классов
     */
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition] == oldList[oldItemPosition]
    }
}