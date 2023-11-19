package solutions.mobiledev.reminder.presentation.contactSms

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.domain.entity.ContactItem


class ContactSmsListAdapter : ListAdapter<ContactItem, ContactItemViewHolder>(ContactItemDiffCallback()) {


    var onContactItemClickListener: ((ContactItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactItemViewHolder {
        val layout = when (viewType) {
            ContactSmsListAdapter.VIEW_TYPE_LAST -> R.layout.item_sms_contact_last
            ContactSmsListAdapter.VIEW_TYPE_NOT_LAST -> R.layout.item_sms_contact
            else -> throw RuntimeException("Unknown view type: $viewType")
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ContactItemViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ContactItemViewHolder, position: Int) {
        val contactItem = getItem(position)

        viewHolder.tvName.text = contactItem.name
        viewHolder.tvPhone.text = contactItem.number
        viewHolder.chContact.isChecked = contactItem.item

        when {
            contactItem.avatar != null -> {
            }
            else -> {}
        }
        viewHolder.view.setOnClickListener {
            onContactItemClickListener?.invoke(contactItem)
            viewHolder.chContact.isChecked = contactItem.item
        }

    }

    override fun getItemViewType(position: Int) : Int {
        val item = getItem(position)
        return if (item.lastNumber) VIEW_TYPE_LAST
        else VIEW_TYPE_NOT_LAST
    }

    companion object {
        const val VIEW_TYPE_LAST = 100
        const val VIEW_TYPE_NOT_LAST = 101

    }
}