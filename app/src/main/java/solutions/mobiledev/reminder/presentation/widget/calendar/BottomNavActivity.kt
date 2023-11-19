package solutions.mobiledev.reminder.presentation.widget.calendar

import org.koin.androidx.viewmodel.ext.android.viewModel
import solutions.mobiledev.reminder.databinding.ActivityBottomNavBinding
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.widget.BindingActivity

class BottomNavActivity : BindingActivity<ActivityBottomNavBinding>() {

    private val viewModel by viewModel<ReminderViewModel>()

    override fun inflateBinding() = ActivityBottomNavBinding.inflate(layoutInflater)



    private fun processResult(matches: List<String>) {
//        if (matches.isNotEmpty()) {
//            viewModel.parseResults(matches, false, this)
//        }
    }


    companion object {
        const val ARG_DEST = "arg_dest"

        object Dest {
            const val DAY_VIEW = 0
            const val SETTINGS = 1
        }
    }
}