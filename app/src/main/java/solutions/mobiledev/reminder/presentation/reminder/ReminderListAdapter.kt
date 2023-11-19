package solutions.mobiledev.reminder.presentation.reminder

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.presentation.reminder.fragment.DateTimeListAdapter
import java.time.LocalDate
import java.time.LocalTime.parse


class ReminderListAdapter() : DateTimeListAdapter() {

    var onReminderItemLongClickListener: ((ReminderItem) -> Unit)? = null
    var onEditClickListener: ((ReminderItem) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderItemViewHolder {
        val layout =
            when (viewType) {
                VIEW_TYPE_DISABLED -> R.layout.item_remind_check
                VIEW_TYPE_ENABLED -> R.layout.item_remind_enabled
                else -> throw RuntimeException("Unknown view type: $viewType")
            }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ReminderItemViewHolder(view)
    }


    override fun onBindViewHolder(viewHolder: ReminderItemViewHolder, position: Int) {

        val reminderItem = getItem(position)
        viewHolder.tvText.text = reminderItem.body
        val localDate = LocalDate.parse(reminderItem.dateRemaining)
        viewHolder.tvDate.text = dateTimeManager.getDate(localDate)
        val localTime = parse(reminderItem.timeRemaining)
        viewHolder.tvTime.text = dateTimeManager.getTime(localTime)
        if (reminderItem.sms) viewHolder.ivSms.visibility =
            View.VISIBLE else viewHolder.ivSms.visibility = View.GONE
        if (reminderItem.mails) viewHolder.ivEmail.visibility =
            View.VISIBLE else viewHolder.ivEmail.visibility = View.GONE
        if (reminderItem.phoneNumbers || reminderItem.contacts) {
            viewHolder.ivCall.visibility = View.VISIBLE
        } else {
            viewHolder.ivCall.visibility = View.GONE
        }
        if (reminderItem.file) viewHolder.ivFile.visibility =
            View.VISIBLE else
            viewHolder.ivFile.visibility = View.INVISIBLE

        viewHolder.view.setOnClickListener {

                val startColor = ContextCompat.getColor(viewHolder.itemView.context, R.color.background_click)
                val endColor = ContextCompat.getColor(viewHolder.itemView.context, R.color.background_start_click)
                val animatorSetClick = ObjectAnimator.ofInt(viewHolder.llActive, "backgroundColor", startColor, endColor)
                animatorSetClick.setEvaluator(ArgbEvaluator())
                animatorSetClick.duration = 500 // 500мс
                animatorSetClick.start()
            onEditClickListener?.invoke(reminderItem)

        }
        viewHolder.view.setOnLongClickListener {
            onReminderItemLongClickListener?.invoke(reminderItem)
            true
        }
    }


    override fun getItemViewType(position: Int): Int {
        val reminderItem = getItem(position)
        return if (reminderItem.state) {
            VIEW_TYPE_ENABLED
        } else {
            VIEW_TYPE_DISABLED
        }
    }

    companion object {
        const val VIEW_TYPE_DISABLED = 100
        const val VIEW_TYPE_ENABLED = 101
    }
}