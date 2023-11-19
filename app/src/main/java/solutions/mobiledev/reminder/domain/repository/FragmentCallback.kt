package solutions.mobiledev.reminder.domain.repository

import solutions.mobiledev.reminder.presentation.BaseFragment

interface FragmentCallback {
    fun onTitleChange(title: String)
    fun hideKeyboard()
    fun setCurrentFragment(fragment: BaseFragment<*>)
}