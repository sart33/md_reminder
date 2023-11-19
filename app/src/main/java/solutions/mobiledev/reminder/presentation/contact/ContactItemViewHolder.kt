package solutions.mobiledev.reminder.presentation.contact

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import solutions.mobiledev.reminder.R

class ContactItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val tvName = view.findViewById<TextView>(R.id.tv_name)
    val tvPhone = view.findViewById<TextView>(R.id.tv_email_address)
    val chContact = view.findViewById<CheckBox>(R.id.ch_contact)
    val ivAvatar = view.findViewById<ImageView>(R.id.iv_avatar)

}