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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentAddSmsReminderBinding
import solutions.mobiledev.reminder.domain.entity.ContactItem
import solutions.mobiledev.reminder.domain.entity.SmsItem
import solutions.mobiledev.reminder.domain.entity.SmsItem.Companion.UNDEFINED_ID
import solutions.mobiledev.reminder.presentation.contactSms.ContactsSmsFragment
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel


class AddSmsReminderFragment : Fragment() {

    private val REQUEST_CODE_READ_CONTACTS = 1
    private var READ_CONTACTS_GRANTED = false
    private lateinit var viewModel: ReminderViewModel
    private lateinit var contactItem: ContactItem
    private lateinit var smsMessage: String
    private lateinit var smsItem: SmsItem
    private var reminderItemId: Int = UNDEFINED_ID

    private lateinit var animatorSetSave: AnimatorSet
    private lateinit var animatorSetCancel: AnimatorSet
    private lateinit var animatorSetContact: ObjectAnimator

    private var _binding: FragmentAddSmsReminderBinding? = null
    private val binding: FragmentAddSmsReminderBinding
        get() = _binding ?: throw RuntimeException("FragmentAddSmsReminderBinding == null")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()

    }

    private fun observeViewModelSms() = with(binding) {
        viewModel.errorInputMessage.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_message)
            } else {
                null
            }
            tilSmsMessage.error = message
        }
        viewModel.errorInputPhoneNumber.observe(viewLifecycleOwner) {
            val number = if (it) {
                getString(R.string.error_input_phone_number)
            } else {
                null
            }
            tilPhoneNumber.error = number
        }
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            goToPrev()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSmsReminderBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        viewModel =
            ViewModelProvider(this@AddSmsReminderFragment)[ReminderViewModel::class.java]
        var avatar = "phone"
        var name = ""
        observeViewModelSms()
        super.onViewCreated(view, savedInstanceState)
        initTouchAnimation()
        if (this@AddSmsReminderFragment::smsMessage.isInitialized) {
            etSmsMessage.text = this@AddSmsReminderFragment.smsMessage.toEditable()

        }
        if (this@AddSmsReminderFragment::contactItem.isInitialized) {
            etPhoneNumber.text = this@AddSmsReminderFragment.contactItem.number.toEditable()
            avatar = if (contactItem.avatar != null) {
                contactItem.avatar!!
            } else {
                "null"
            }
            name = contactItem.name
        }
        ivContacts.setOnClickListener {
            animatorSetContact.start()
            smsMessage = etSmsMessage.text.toString()
            viewModel.smsMessage.observe(viewLifecycleOwner) {
                smsMessage = etSmsMessage.text.toString()
                viewModel.saveSmsMessage(smsMessage)
            }
            val hasReadContactPermission =
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_CONTACTS
                )
            // если устройство до API 23, устанавливаем разрешение
            if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
                READ_CONTACTS_GRANTED = true
                if (this@AddSmsReminderFragment::smsMessage.isInitialized) {
                    launchContactsSmsFragment(smsMessage, reminderItemId)
                } else {
                    launchContactsSmsFragment(reminderItemId)
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
                launchAddSmsReminderFragment(reminderItemId)
            }
        }

        laSave.setOnClickListener {
            animatorSetSave.start()
            observeViewModelSms()
            viewModel.addSmsItem(
                smsId = UNDEFINED_ID,
                smsMessage = etSmsMessage.text.toString(),
                phoneNumber = etPhoneNumber.text.toString(),
                contactName = name,
                contactAvatar = avatar,
                reminderItemId = reminderItemId

            )
            viewModel.smsItem.observe(viewLifecycleOwner) {
                smsItem = it
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

    private fun goToPrev() {
        requireActivity().supportFragmentManager.popBackStack()
        if (reminderItemId == 0) {
            launchReminderAddFragment()
        } else {
            launchReminderEditFragment(reminderItemId)

        }
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

    private fun launchContactsSmsFragment(reminderItemId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ContactsSmsFragment.newInstance(reminderItemId))
            .addToBackStack(null)
            .commit()
    }


    private fun launchContactsSmsFragment(smsMessage: String, reminderItemId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ContactsSmsFragment.newInstanceSmsMessage(smsMessage, reminderItemId))
            .addToBackStack(null)
            .commit()
    }


    private fun launchAddSmsReminderFragment(reminderItemId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, newInstance(reminderItemId))
            .addToBackStack(null)
            .commit()
    }

    private fun launchReminderAddFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderAddFragment.newInstance())
            .addToBackStack(ReminderAddFragment.NAME)
            .commit()
    }

    private fun launchReminderEditFragment(reminderItemId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderEditFragment.newInstance(reminderItemId))
            .addToBackStack(ReminderEditFragment.NAME).commit()
    }

    private fun parseArgs() {
        val args = requireArguments()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            args.getParcelable("contactSmsItem", ContactItem::class.java)
                ?.let {
                    contactItem = it
                }
        } else {
            args.getParcelable<ContactItem>("contactSmsItem")?.let {
                contactItem = it
            }
        }
        args.getString("smsMessage")?.let {
            smsMessage = it
        }

        reminderItemId = if (!args.containsKey(REMINDER_ITEM_ID)) {
            0
        } else {
            args.getInt(REMINDER_ITEM_ID)
        }
    }

    companion object {
        const val NAME = "AddSmsReminderFragment"
        const val REMINDER_ITEM_ID = "reminder_item_id"
        const val DEFAULT_DURATION = 500.toLong()


        fun newInstance(reminderItemId: Int): AddSmsReminderFragment {
            return AddSmsReminderFragment().apply {
                arguments = Bundle().apply {
                    putInt(REMINDER_ITEM_ID, reminderItemId)
                }
            }
        }

        fun newInstanceTwo(contactItem: ContactItem, reminderItemId: Int): AddSmsReminderFragment {
            return AddSmsReminderFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("contactSmsItem", contactItem)
                    putInt(REMINDER_ITEM_ID, reminderItemId)

                }
            }
        }

        fun newInstanceThree(contactItem: ContactItem, smsMessage: String, reminderItemId: Int): AddSmsReminderFragment {
            return AddSmsReminderFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("contactSmsItem", contactItem)
                    putString("smsMessage", smsMessage)
                    putInt(REMINDER_ITEM_ID, reminderItemId)
                }
            }
        }
        fun newInstanceFour(smsMessage: String, reminderItemId: Int): AddSmsReminderFragment {
            return AddSmsReminderFragment().apply {
                arguments = Bundle().apply {
                    putString("smsMessage", smsMessage)
                    putInt(REMINDER_ITEM_ID, reminderItemId)
                }
            }
        }
    }
}

