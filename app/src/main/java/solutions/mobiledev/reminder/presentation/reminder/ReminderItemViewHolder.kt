package solutions.mobiledev.reminder.presentation.reminder

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import solutions.mobiledev.reminder.R

class ReminderItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    val tvText = view.findViewById<TextView>(R.id.tv_text)
    val tvDate = view.findViewById<TextView>(R.id.tv_date)
    val tvTime = view.findViewById<TextView>(R.id.tv_time)
    val ivEmail = view.findViewById<ImageView>(R.id.iv_mail)
    val ivFile = view.findViewById<ImageView>(R.id.iv_file)
    val ivSms = view.findViewById<ImageView>(R.id.iv_delete_sms1)
    val ivCall = view.findViewById<ImageView>(R.id.iv_call)
    val ivMainMenu = view.findViewById<TextView>(R.id.ivMainMenu)
    val fabAddRem = view.findViewById<FloatingActionButton>(R.id.fab_add_rem)
    val llActive = view.findViewById<LinearLayout>(R.id.llActive)

}