package solutions.mobiledev.reminder.presentation.contactsEmail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.domain.entity.ContactItem


class ContactEmailListAdapter : ListAdapter<ContactItem, ContactEmailItemViewHolder>(ContactItemDiffCallback()) {


    var onContactItemClickListener: ((ContactItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactEmailItemViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_LAST -> R.layout.item_email_contact_last
            VIEW_TYPE_NOT_LAST -> R.layout.item_email_contact
            else -> throw RuntimeException("Unknown view type: $viewType")
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ContactEmailItemViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ContactEmailItemViewHolder, position: Int) {
        val contactItem = getItem(position)

        viewHolder.tvName.text = contactItem.name
        viewHolder.tvEmail.text = contactItem.email
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