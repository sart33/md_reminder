package solutions.mobiledev.reminder.presentation.contactsEmail

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import solutions.mobiledev.reminder.R

class ContactEmailItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val tvName = view.findViewById<TextView>(R.id.tv_name)
    val tvEmail = view.findViewById<TextView>(R.id.tv_email_address)
    val chContact = view.findViewById<CheckBox>(R.id.ch_contact)



}