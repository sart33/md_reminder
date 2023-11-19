package solutions.mobiledev.reminder.presentation

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import org.koin.android.ext.android.inject

abstract class BindingFragment<B : ViewBinding> : Fragment() {

    protected val dialogues by inject<Dialogues>()

    protected fun withContext(action: (Context) -> Unit) {
        context?.let {
            action.invoke(it)
        }
    }
}