package solutions.mobiledev.reminder.presentation.reminder.fragment

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentAddEmailReminderBinding
import solutions.mobiledev.reminder.domain.entity.ContactItem
import solutions.mobiledev.reminder.domain.entity.EmailItem
import solutions.mobiledev.reminder.presentation.contactsEmail.ContactsEmailFragment
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel


class AddEmailReminderFragment : DialogFragment() {

    private val REQUEST_CODE_READ_CONTACTS = 1
    private var READ_CONTACTS_GRANTED = false
    private lateinit var viewModel: ReminderViewModel
    private var reminderItemId: Int = UNDEFINED_ID
    private lateinit var emailItem: EmailItem
    private lateinit var emailMessage: String
    private lateinit var selectedContacts: List<ContactItem>
    private var contactNames: String = ""

    private var _binding: FragmentAddEmailReminderBinding? = null
    private val binding: FragmentAddEmailReminderBinding
        get() = _binding ?: throw RuntimeException("FragmentAddEmailReminderBinding == null")

    private lateinit var animatorSetSave: AnimatorSet
    private lateinit var animatorSetCancel: AnimatorSet
    private lateinit var animatorSetContact: ObjectAnimator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEmailReminderBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        viewModel = ViewModelProvider(this@AddEmailReminderFragment)[ReminderViewModel::class.java]

        var avatars = ""
        var names = ""

        super.onViewCreated(view, savedInstanceState)
        observeViewModelEmail()
        initTouchAnimation()
        if (this@AddEmailReminderFragment::emailMessage.isInitialized) {
            etEmailMessage.text = this@AddEmailReminderFragment.emailMessage.toEditable()
        }
        if (this@AddEmailReminderFragment::selectedContacts.isInitialized) {
            selectedContacts.forEach {
                contactNames = contactNames.plus("${it.email}, ")
                avatars = avatars.plus("${it.avatar}, ")
                names = names.plus("${it.name}, ")
//                messages = messages.plus("${it.}, ")
            }
            etPhoneNumber.text = contactNames.trimEnd().trimEnd(',').toEditable()
            avatars = avatars.trimEnd().trimEnd(',')
            names = names.trimEnd().trimEnd(',')
        }
        ivContacts.setOnClickListener {
            animatorSetContact.start()
            emailMessage = etEmailMessage.text.toString()
            viewModel.emailMessage.observe(viewLifecycleOwner) {
                emailMessage = etEmailMessage.text.toString()
                viewModel.saveEmailMessage(emailMessage)
            }
            val hasReadContactPermission =
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_CONTACTS
                )
            // если устройство до API 23, устанавливаем разрешение
            if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
                READ_CONTACTS_GRANTED = true
                if (this@AddEmailReminderFragment::emailMessage.isInitialized) {
                    launchContactsEmailFragment(emailMessage)
                } else {
                    launchContactsEmailFragment()
                }

            } else {
                // вызываем диалоговое окно для установки разрешений
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                    ),
                    REQUEST_CODE_READ_CONTACTS
                )
                launchAddEmailReminderFragment()
            }
        }
        laSave.setOnClickListener {
            animatorSetSave.start()
            observeViewModelEmail()
            viewModel.addEmailItem(
                emailId = EmailItem.UNDEFINED_ID,
                emailMessage = etEmailMessage.text.toString(),
                emailAddress = etPhoneNumber.text.toString(),
                contactName = names,
                contactAvatar = avatars,
                remId = reminderItemId

            )
            viewModel.emailItem.observe(viewLifecycleOwner) {
                emailItem = it
            }
        }

        laCancel.setOnClickListener {
            animatorSetCancel.start()
            goToPrev()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initTouchAnimation() = with(binding) {
        val bgColorAnimatorSave = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorSave = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        bgColorAnimatorSave.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvSaveButton.setBackgroundColor(color)
        }
        textColorAnimatorSave.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvSaveButton.setTextColor(color)
        }

        animatorSetSave = AnimatorSet()
        animatorSetSave.playTogether(bgColorAnimatorSave, textColorAnimatorSave)
        animatorSetSave.duration = ReminderAddFragment.DEFAULT_DURATION // 500мс

        val bgColorAnimatorCancel = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorCancel = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        bgColorAnimatorCancel.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvCancelButton.setBackgroundColor(color)
        }
        textColorAnimatorCancel.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvCancelButton.setTextColor(color)
        }

        animatorSetCancel = AnimatorSet()
        animatorSetCancel.playTogether(bgColorAnimatorCancel, textColorAnimatorCancel)
        animatorSetCancel.duration = DEFAULT_DURATION // 500мс

        val startColor = ContextCompat.getColor(requireContext(), R.color.background_click)
        val endColor = ContextCompat.getColor(requireContext(), R.color.background_start_click)

        animatorSetContact = ObjectAnimator.ofInt(ivContacts, "backgroundColor", startColor, endColor)
        animatorSetContact.setEvaluator(ArgbEvaluator())
        animatorSetContact.duration = DEFAULT_DURATION
    }

    private fun observeViewModelEmail() = with(binding) {
        viewModel.errorInputMessage.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_message)
            } else {
                null
            }
            tilSmsMessage.error = message
        }
        viewModel.errorInputMail.observe(viewLifecycleOwner) {
            val number = if (it) {
                getString(R.string.error_input_email_address)
            } else {
                null
            }
            tilPhoneNumber.error = number
        }
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            goToPrev()
//            activity?.onBackPressed()
        }

    }


    private fun goToPrev() {
        requireActivity().supportFragmentManager.popBackStack()
        if (reminderItemId == 0) {
            launchReminderAddFragment()
        } else {
            launchReminderEditFragment(reminderItemId)

        }
    }

    private fun launchContactsEmailFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ContactsEmailFragment.newInstance(reminderItemId))
            .addToBackStack(null)
            .commit()
    }

    private fun launchContactsEmailFragment(emailMessage: String) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(
                R.id.main_container,
                ContactsEmailFragment.newInstanceEmailMessage(emailMessage, reminderItemId)
            )
            .addToBackStack(null)
            .commit()
    }

    private fun launchAddEmailReminderFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, newInstance(reminderItemId))
            .addToBackStack(null)
            .commit()
    }

    private fun launchReminderEditFragment(reminderItemId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderEditFragment.newInstance(reminderItemId))
            .addToBackStack(ReminderEditFragment.NAME).commit()
    }

    private fun launchReminderAddFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderAddFragment.newInstance())
            .addToBackStack(null).commit()
    }

    private fun parseArgs() {
        val args = requireArguments()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            args.getParcelableArrayList("selectContactEmailList", ContactItem::class.java)
                ?.let {
                    selectedContacts = it.toList()
                }

        } else {

            args.getParcelableArrayList<ContactItem>("selectContactEmailList")?.let {
                selectedContacts = it.toList()
            }
        }
        args.getString("emailMessage")?.let {
            emailMessage = it
        }
        reminderItemId = if (!args.containsKey(AddSmsReminderFragment.REMINDER_ITEM_ID)) {
            0
        } else {
            args.getInt(AddSmsReminderFragment.REMINDER_ITEM_ID)
        }
    }

    companion object {
        const val NAME = "AddEmailReminderFragment"
        const val UNDEFINED_ID = 0
        const val REMINDER_ITEM_ID = "reminder_item_id"
        const val DEFAULT_DURATION = 500.toLong()


        fun newInstance(reminderItemId: Int): AddEmailReminderFragment {
            return AddEmailReminderFragment().apply {
                arguments = Bundle().apply {
                    putInt(REMINDER_ITEM_ID, reminderItemId)
                }
            }

        }

        fun newInstanceTwo(
            selectedContacts: MutableList<ContactItem>,
            reminderItemId: Int
        ): AddEmailReminderFragment {
            return AddEmailReminderFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(
                        "selectContactEmailList",
                        selectedContacts as ArrayList<ContactItem>
                    )
                    putInt(REMINDER_ITEM_ID, reminderItemId)
                }
            }
        }

        fun newInstanceThree(
            selectedContacts: MutableList<ContactItem>,
            emailMessage: String, reminderItemId: Int
        ): AddEmailReminderFragment {
            return AddEmailReminderFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(
                        "selectContactEmailList",
                        selectedContacts as ArrayList<ContactItem>
                    )
                    putString("emailMessage", emailMessage)
                    putInt(REMINDER_ITEM_ID, reminderItemId)
                }
            }
        }

        fun newInstanceFour(emailMessage: String, reminderItemId: Int
        ): AddEmailReminderFragment {
            return AddEmailReminderFragment().apply {
                arguments = Bundle().apply {
                    putString("emailMessage", emailMessage)
                    putInt(REMINDER_ITEM_ID, reminderItemId)
                }
            }
        }

    }
}

