package solutions.mobiledev.reminder.presentation.reminder.fragment

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentAddPhoneNumberReminderBinding
import solutions.mobiledev.reminder.domain.entity.ContactItem
import solutions.mobiledev.reminder.domain.entity.PhoneNumberItem
import solutions.mobiledev.reminder.domain.entity.SmsItem
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel


class AddPhoneNumberReminderFragment : BottomSheetDialogFragment() {
    private lateinit var viewModel: ReminderViewModel
    private lateinit var phoneNumberItem: PhoneNumberItem

    private var _binding: FragmentAddPhoneNumberReminderBinding? = null
    private val binding: FragmentAddPhoneNumberReminderBinding
        get() = _binding ?: throw RuntimeException("FragmentAddPhoneNumberReminderBinding == null")

    private var reminderItemId: Int = UNDEFINED_ID

    private lateinit var animatorSetSave: AnimatorSet
    private lateinit var animatorSetCancel: AnimatorSet


    private fun observeViewModelPhoneNumber() = with(binding) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        parseArgs()
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPhoneNumberReminderBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        viewModel = ViewModelProvider(this@AddPhoneNumberReminderFragment)[ReminderViewModel::class.java]
        super.onViewCreated(view, savedInstanceState)
        initTouchAnimation()
            laCancel.setOnClickListener {
                animatorSetCancel.start()
                goToPrev()
            }
            laSave.setOnClickListener {
                animatorSetSave.start()
                observeViewModelPhoneNumber()
                viewModel.addPhoneNumberItem(
                    id = PhoneNumberItem.UNDEFINED_ID,
                    phoneNumber = etPhoneNumber.text.toString(),
                    remId = reminderItemId
                )
                viewModel.phoneNumberItem.observe(viewLifecycleOwner) {
                    phoneNumberItem = it
                }
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
        animatorSetCancel.duration = ReminderAddFragment.DEFAULT_DURATION // 500мс
    }

    private fun goToPrev() {
//        requireActivity().supportFragmentManager.popBackStack()
        if (reminderItemId == 0) {
            launchReminderAddFragment()
        } else {
            launchReminderEditFragment(reminderItemId)

        }
    }

    private fun parseArgs() {
        val args = requireArguments()

        reminderItemId = if (!args.containsKey(AddSmsReminderFragment.REMINDER_ITEM_ID)) {
            0
        } else {
            args.getInt(AddSmsReminderFragment.REMINDER_ITEM_ID)
        }
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

    companion object {
        const val NAME = "AddPhoneNumberReminderFragment"
        const val REMINDER_ITEM_ID = "reminder_item_id"
        const val UNDEFINED_ID = 0


        fun newInstance(reminderItemId: Int): AddPhoneNumberReminderFragment {
            return AddPhoneNumberReminderFragment().apply {
                arguments = Bundle().apply {
                    putInt(AddSmsReminderFragment.REMINDER_ITEM_ID, reminderItemId)
                }
            }
        }

    }
}