package solutions.mobiledev.reminder.presentation

import android.content.Context
import androidx.viewbinding.ViewBinding
import solutions.mobiledev.reminder.core.utils.Language
import solutions.mobiledev.reminder.domain.repository.FragmentCallback
import solutions.mobiledev.reminder.presentation.settings.DateTimeManager
import org.koin.android.ext.android.inject

abstract class BaseFragment<B : ViewBinding>: BindingFragment<B>() {

    var callback: FragmentCallback? = null
        private set
    protected val currentStateHolder by inject<CurrentStateHolder>()
    protected val prefs = currentStateHolder.preferences
    protected val language by inject<Language>()
    protected val dateTimeManager by inject<DateTimeManager>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (callback == null) {
            runCatching { callback = context as FragmentCallback? }
        }
    }
}