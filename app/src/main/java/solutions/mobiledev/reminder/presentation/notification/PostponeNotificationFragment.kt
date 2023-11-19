package solutions.mobiledev.reminder.presentation.notification

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Insets
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentPostponeNotificationBinding
import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderDialogFragment
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*


class PostponeNotificationFragment : ReminderDialogFragment() {

    private lateinit var viewModel: ReminderViewModel
    private var reminderItemId: Int = UNDEFINED_ID
    private lateinit var dateItem: String
    private lateinit var timeItem: String
    private var remainderId: Int = UNDEFINED_ID
    private lateinit var reminderItem: ReminderItem

    private var _binding: FragmentPostponeNotificationBinding? = null
    private val binding: FragmentPostponeNotificationBinding
        get() = _binding ?: throw RuntimeException("FragmentPostponeNotificationBinding == null")

    private lateinit var animatorSetMin15: AnimatorSet
    private lateinit var animatorSetMin30: AnimatorSet
    private lateinit var animatorSetMin45: AnimatorSet
    private lateinit var animatorSetHourOne: AnimatorSet
    private lateinit var animatorSetDayOne: AnimatorSet
    private lateinit var animatorSetTomorrow9: AnimatorSet
    private lateinit var animatorSetTomorrow13: AnimatorSet
    private lateinit var animatorSetTomorrow18: AnimatorSet
    private lateinit var animatorSetTomorrow21: AnimatorSet
    private lateinit var animatorSetNewDate: AnimatorSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostponeNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics =
                requireActivity().windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            val width = windowMetrics.bounds.width() - insets.left -
                    insets.right
            val height = windowMetrics.bounds.height() - insets.top -
                    insets.bottom
            val window = dialog?.window
            if (window != null) {
                window.setLayout(
                    (width * 1).toInt(), (height *
                            0.7).toInt()
                ) // for width and height to be 90 % of screen
                window.setGravity(Gravity.CENTER)
            }
            super.onResume()
        } else {
            val window = dialog?.window
            val size = Point()
            // Store dimensions of the screen in `size`
            val display = window?.windowManager?.defaultDisplay
            display?.getSize(size)
            // Set the width of the dialog proportional to 90% of the screen width
            window?.setLayout(
                (size.x * 1).toInt(),
                (size.y * 0.7).toInt()
            )
            window?.setGravity(Gravity.CENTER)
            // Call super onResume after sizing
            super.onResume()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        viewModel =
            ViewModelProvider(this@PostponeNotificationFragment)[ReminderViewModel::class.java]
        super.onViewCreated(view, savedInstanceState)
        topBarTouchAnimation()
//        if (LocalDateTime.now().toString() <= DATE_TIME) {
        tvDayOne.text = getText(R.string.tomorrow)
        tvTomorrow9.text =
            "${getText(R.string.tomorrow)} ${dateTimeManager.getTime(LocalTime.parse("09:00"))}"
        tvTomorrow13.text =
            "${getText(R.string.tomorrow)} ${dateTimeManager.getTime(LocalTime.parse("13:00"))}"
        tvTomorrow18.text =
            "${getText(R.string.tomorrow)} ${dateTimeManager.getTime(LocalTime.parse("18:00"))}"
        tvTomorrow21.text =
            "${getText(R.string.tomorrow)} ${dateTimeManager.getTime(LocalTime.parse("21:00"))}"
        viewModel.getReminderViewItem(reminderItemId)
        viewModel.reminderItem.observe(viewLifecycleOwner) {
            if (it != null) {
                reminderItem = it
                var count = reminderItem.menuRepeatCount
                if (count == 0) {
                    count = 1
                }
                val localDateTime = LocalDateTime.now()
                var newLocalDateTime = localDateTime
                clMin15.setOnClickListener {
                    animatorSetMin15.start()
                    newLocalDateTime = localDateTime.plusMinutes(15)
                    val dateTimeArr = newLocalDateTime.toString().split("T")
                    val timeArr = dateTimeArr[1].split(".")
                    editDateAndTimeRem(
                        reminderItem.id,
                        newLocalDateTime.toString(),
                        dateTimeArr[0],
                        timeArr[0],
                        count
                    )
                    dismiss()
                }
                clMin30.setOnClickListener {
                    animatorSetMin30.start()
                    newLocalDateTime = localDateTime.plusMinutes(30)
                    val dateTimeArr = newLocalDateTime.toString().split("T")
                    val timeArr = dateTimeArr[1].split(".")
                    editDateAndTimeRem(
                        reminderItem.id,
                        newLocalDateTime.toString(),
                        dateTimeArr[0],
                        timeArr[0],
                        count
                    )
                    dismiss()
                }
                clMin45.setOnClickListener {
                    animatorSetMin45.start()
                    newLocalDateTime = localDateTime.plusMinutes(45)
                    val dateTimeArr = newLocalDateTime.toString().split("T")
                    val timeArr = dateTimeArr[1].split(".")
                    editDateAndTimeRem(
                        reminderItem.id,
                        newLocalDateTime.toString(),
                        dateTimeArr[0],
                        timeArr[0],
                        count
                    )
                    dismiss()
                }
                clHourOne.setOnClickListener {
                    animatorSetHourOne.start()
                    newLocalDateTime = localDateTime.plusHours(1)
                    val dateTimeArr = newLocalDateTime.toString().split("T")
                    val timeArr = dateTimeArr[1].split(".")
                    editDateAndTimeRem(
                        reminderItem.id,
                        newLocalDateTime.toString(),
                        dateTimeArr[0],
                        timeArr[0],
                        count
                    )
                    dismiss()
                }
                clDayOne.setOnClickListener {
                    animatorSetDayOne.start()
                    newLocalDateTime = localDateTime.plusDays(1)
                    val dateTimeArr = newLocalDateTime.toString().split("T")
                    val timeArr = dateTimeArr[1].split(".")
                    editDateAndTimeRem(
                        reminderItem.id,
                        newLocalDateTime.toString(),
                        dateTimeArr[0],
                        timeArr[0],
                        count
                    )
                    dismiss()
                }
                clTomorrow9.setOnClickListener {
                    animatorSetTomorrow9.start()
                    newLocalDateTime = localDateTime.plusDays(1)
                    val dateTimeList = newLocalDateTime.toString().split("T")
                    newLocalDateTime = LocalDateTime.parse("${dateTimeList[0]}T09:00")
                    val dateTimeArr = newLocalDateTime.toString().split("T")
                    val timeArr = dateTimeArr[1].split(".")
                    editDateAndTimeRem(
                        reminderItem.id,
                        newLocalDateTime.toString(),
                        dateTimeArr[0],
                        timeArr[0],
                        count
                    )
                    dismiss()
                }
                clTomorrow13.setOnClickListener {
                    animatorSetTomorrow13.start()
                    newLocalDateTime = localDateTime.plusDays(1)
                    val dateTimeList = newLocalDateTime.toString().split("T")
                    newLocalDateTime = LocalDateTime.parse("${dateTimeList[0]}T13:00")
                    val dateTimeArr = newLocalDateTime.toString().split("T")
                    val timeArr = dateTimeArr[1].split(".")
                    editDateAndTimeRem(
                        reminderItem.id,
                        newLocalDateTime.toString(),
                        dateTimeArr[0],
                        timeArr[0],
                        count
                    )
                    dismiss()
                }
                clTomorrow18.setOnClickListener {
                    animatorSetTomorrow18.start()
                    newLocalDateTime = localDateTime.plusDays(1)
                    val dateTimeList = newLocalDateTime.toString().split("T")
                    newLocalDateTime = LocalDateTime.parse("${dateTimeList[0]}T18:00")
                    val dateTimeArr = newLocalDateTime.toString().split("T")
                    val timeArr = dateTimeArr[1].split(".")
                    editDateAndTimeRem(
                        reminderItem.id,
                        newLocalDateTime.toString(),
                        dateTimeArr[0],
                        timeArr[0],
                        count
                    )
                    dismiss()
                }
                clTomorrow21.setOnClickListener {
                    animatorSetTomorrow21.start()
                    newLocalDateTime = localDateTime.plusDays(1)
                    val dateTimeList = newLocalDateTime.toString().split("T")
                    newLocalDateTime = LocalDateTime.parse("${dateTimeList[0]}T21:00")
                    val dateTimeArr = newLocalDateTime.toString().split("T")
                    val timeArr = dateTimeArr[1].split(".")
                    editDateAndTimeRem(
                        reminderItem.id,
                        newLocalDateTime.toString(),
                        dateTimeArr[0],
                        timeArr[0],
                        count
                    )
                    dismiss()

                }
                clNewDate.setOnClickListener {
                    animatorSetNewDate.start()
                    datePictureDialogPopUp()
                }
            }
        }
//        } else {
//        }
    }

    private fun topBarTouchAnimation() = with(binding) {
        val bgColorAnimatorMin15 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorMin15 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )

        bgColorAnimatorMin15.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clMin15.setBackgroundColor(color)
        }

        textColorAnimatorMin15.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvMin15.setTextColor(color)
        }

        animatorSetMin15 = AnimatorSet()
        animatorSetMin15.playTogether(bgColorAnimatorMin15, textColorAnimatorMin15)
        animatorSetMin15.duration = 500 // 500мс

        val bgColorAnimatorMin30 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorMin30 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )

        bgColorAnimatorMin30.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clMin30.setBackgroundColor(color)
        }

        textColorAnimatorMin30.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvMin30.setTextColor(color)
        }

        animatorSetMin30 = AnimatorSet()
        animatorSetMin30.playTogether(bgColorAnimatorMin30, textColorAnimatorMin30)
        animatorSetMin30.duration = 500 // 500мс

        val bgColorAnimatorMin45 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorMin45 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )

        bgColorAnimatorMin45.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clMin45.setBackgroundColor(color)
        }

        textColorAnimatorMin45.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvMin45.setTextColor(color)
        }

        animatorSetMin45 = AnimatorSet()
        animatorSetMin45.playTogether(bgColorAnimatorMin45, textColorAnimatorMin45)
        animatorSetMin45.duration = 500 // 500мс

        val bgColorAnimatorHourOne = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorHourOne = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )

        bgColorAnimatorHourOne.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clHourOne.setBackgroundColor(color)
        }

        textColorAnimatorHourOne.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvHourOne.setTextColor(color)
        }

        animatorSetHourOne = AnimatorSet()
        animatorSetHourOne.playTogether(bgColorAnimatorHourOne, textColorAnimatorHourOne)
        animatorSetHourOne.duration = 500 // 500мс


        val bgColorAnimatorDayOne = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorDayOne = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )

        bgColorAnimatorDayOne.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clDayOne.setBackgroundColor(color)
        }

        textColorAnimatorDayOne.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvDayOne.setTextColor(color)
        }

        animatorSetDayOne = AnimatorSet()
        animatorSetDayOne.playTogether(bgColorAnimatorDayOne, textColorAnimatorDayOne)
        animatorSetDayOne.duration = 500 // 500мс

        val bgColorAnimatorTomorrow9 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorTomorrow9 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )

        bgColorAnimatorTomorrow9.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clTomorrow9.setBackgroundColor(color)
        }

        textColorAnimatorTomorrow9.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvTomorrow9.setTextColor(color)
        }

        animatorSetTomorrow9 = AnimatorSet()
        animatorSetTomorrow9.playTogether(bgColorAnimatorTomorrow9, textColorAnimatorTomorrow9)
        animatorSetTomorrow9.duration = 500 // 500мс

        val bgColorAnimatorTomorrow13 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorTomorrow13 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )

        bgColorAnimatorTomorrow13.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clTomorrow13.setBackgroundColor(color)
        }

        textColorAnimatorTomorrow13.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvTomorrow13.setTextColor(color)
        }

        animatorSetTomorrow13 = AnimatorSet()
        animatorSetTomorrow13.playTogether(bgColorAnimatorTomorrow13, textColorAnimatorTomorrow13)
        animatorSetTomorrow13.duration = 500 // 500мс

        val bgColorAnimatorTomorrow18 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorTomorrow18 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )

        bgColorAnimatorTomorrow18.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clTomorrow18.setBackgroundColor(color)
        }

        textColorAnimatorTomorrow18.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvTomorrow18.setTextColor(color)
        }

        animatorSetTomorrow18 = AnimatorSet()
        animatorSetTomorrow18.playTogether(bgColorAnimatorTomorrow18, textColorAnimatorTomorrow18)
        animatorSetTomorrow18.duration = 500 // 500мс

        val bgColorAnimatorTomorrow21 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorTomorrow21 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )

        bgColorAnimatorTomorrow21.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clTomorrow21.setBackgroundColor(color)
        }

        textColorAnimatorTomorrow21.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvTomorrow21.setTextColor(color)
        }

        animatorSetTomorrow21 = AnimatorSet()
        animatorSetTomorrow21.playTogether(bgColorAnimatorTomorrow21, textColorAnimatorTomorrow21)
        animatorSetTomorrow21.duration = 500 // 500мс

        val bgColorAnimatorNewDate = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorNewDate = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )

        bgColorAnimatorNewDate.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clNewDate.setBackgroundColor(color)
        }

        textColorAnimatorNewDate.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvNewDate.setTextColor(color)
        }

        animatorSetNewDate = AnimatorSet()
        animatorSetNewDate.playTogether(bgColorAnimatorNewDate, textColorAnimatorNewDate)
        animatorSetNewDate.duration = 500 // 500мс

    }

    private fun editDateTimeRem(id: Int, firstDateTime: String, count: Int) {
        viewModel.editDateTimeFirstRemindingAndCount(id, firstDateTime, count)
//        dismiss()
    }

    private fun editDateAndTimeRem(
        id: Int,
        firstDateTime: String,
        date: String,
        time: String,
        count: Int
    ) {
        viewModel.editDateAndTimeFirstReminding(id, firstDateTime, date, time, count)
//        dismiss()
    }


    private fun parseParams() {
        val args = this@PostponeNotificationFragment.arguments
        if (args != null) {
            if (!args.containsKey(REMINDER_ITEM_ID)) {
                throw RuntimeException("Param reminder item id is absent")
            }
        }
        if (args != null) {
            reminderItemId = args.getInt(REMINDER_ITEM_ID, remainderId)
        }
    }


    private fun saveReminderFragment() = with(binding) {
        viewModel.reminderItem.observe(viewLifecycleOwner) {
            if (it != null) {
                reminderItem = it
                if (this@PostponeNotificationFragment::reminderItem.isInitialized) {
                }
            }
        }
        viewModel.tempLastReminderRecords()
        viewModel.reminderItem.observe(viewLifecycleOwner) {
            if (it != null) {
                reminderItem = it


            }
        }
    }


    private fun datePictureDialogPopUp() {
        // on below line we are getting
        // the instance of our calendar.
        val c = Calendar.getInstance()

        // on below line we are getting
        // our day, month and year.
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // on below line we are creating a
        // variable for date picker dialog.
        val datePickerDialog = DatePickerDialog(
            // on below line we are passing context.
            requireContext(),
            { view, year, monthOfYear, dayOfMonth ->
                // on below line we are setting
                // date to our edit text.
//                viewModel.dateItem.observe(viewLifecycleOwner) {
                var year = year.toString()
                var selectMonth = "${(monthOfYear + 1)}"
                var selectDay = dayOfMonth.toString()
                if (selectMonth.length < 2) {
                    selectMonth = "0${(monthOfYear + 1)}"
                }
                if (dayOfMonth.toString().length < 2) {
                    selectDay = "0$dayOfMonth"
                }
                dateItem = "$year-${selectMonth}-$selectDay"
                if (this@PostponeNotificationFragment::dateItem.isInitialized)
                    viewModel.saveDateItem(dateItem)
                val localDateTimeList = LocalDateTime.now().toString().split("T")
                val newLocalDateTime = LocalDateTime.parse("${dateItem}T${localDateTimeList[1]}")
                viewModel.getReminderViewItem(reminderItemId)
                viewModel.reminderItem.observe(viewLifecycleOwner) {
                    if (it != null) {
                        reminderItem = it
                        var count = reminderItem.menuRepeatCount
                        if (count == 0) {
                            count = 1
                        }
                        val dateTimeArr = newLocalDateTime.toString().split("T")
                        val timeArr = dateTimeArr[1].split(".")
                        editDateAndTimeRem(
                            reminderItem.id,
                            newLocalDateTime.toString(),
                            dateTimeArr[0],
                            timeArr[0],
                            count
                        )

                    }
                }
                timePictureDialogPopUp()


            },
            // on below line we are passing year, month
            // and day for the selected date in our date picker.
            year,
            month,
            day
        )
        // at last we are calling show
        // to display our date picker dialog.
        datePickerDialog.show()
    }


    private fun timePictureDialogPopUp() {
        // on below line we are getting
        // the instance of our calendar.
        val c = Calendar.getInstance()

        // on below line we are getting
        // our day, month and year.
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        var time24true = prefs.hourFormat != 1

        // on below line we are creating a
        // variable for date picker dialog.
        val timePickerDialog =
            TimePickerDialog(requireContext(), { timePicker, hourOfDay, minuteOfHour ->
                c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                c.set(Calendar.MINUTE, minuteOfHour)
                timeItem = SimpleDateFormat("HH:mm").format(c.time)
                if (this@PostponeNotificationFragment::timeItem.isInitialized)
                    viewModel.saveTimeItem(timeItem)
                val localDateTimeList = LocalDateTime.now().toString().split("T")
                val newLocalDateTime = LocalDateTime.parse("${localDateTimeList[0]}T${timeItem}")
                viewModel.getReminderViewItem(reminderItemId)
                viewModel.reminderItem.observe(viewLifecycleOwner) {
                    if (it != null) {
                        reminderItem = it
                        var count = reminderItem.menuRepeatCount
                        if (count == 0) {
                            count = 1
                        }
                        val dateTimeArr = newLocalDateTime.toString().split("T")
                        val timeArr = dateTimeArr[1].split(".")
                        editDateAndTimeRem(
                            reminderItem.id,
                            newLocalDateTime.toString(),
                            dateTimeArr[0],
                            timeArr[0],
                            count
                        )
                    }
                }
                dismiss()
//                binding.tvTime.text = timeItem
            }, hour, minute, time24true)
        timePickerDialog.show()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        const val NAME = "PostponeNotificationFragment"

        //        private const val DATE_TIME = "2023-08-11T13:00"
        const val UNDEFINED_ID = 0
        const val REMINDER_ITEM_ID = "reminder_item_id"

        fun newInstance(remainderId: Int): PostponeNotificationFragment {
            return PostponeNotificationFragment().apply {
                arguments = Bundle().apply {
//                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(REMINDER_ITEM_ID, remainderId)
                }
            }
        }
    }
}