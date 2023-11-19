package solutions.mobiledev.reminder.presentation

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentStartAppBinding
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderAddFragment
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderListFragment
import java.util.*


@Suppress("DEPRECATION")
class StartAppFragment() : BaseFragment<FragmentStartAppBinding>() {


    internal var id: Int = 0
    private lateinit var viewModel: ReminderViewModel
    private lateinit var animatorSetReminderCount: AnimatorSet
    private lateinit var animatorSetMenu: AnimatorSet
    private var _binding: FragmentStartAppBinding? = null
    private val binding: FragmentStartAppBinding
        get() = _binding ?: throw RuntimeException("FragmentStartAppBinding == null")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartAppBinding.inflate(inflater, container, false)
        return binding.root

    }


    @SuppressLint("ServiceCast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        initTouchReminderCountAnimation()
        viewModel = ViewModelProvider(this@StartAppFragment)[ReminderViewModel::class.java]

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Разрешение не предоставлено, запрашиваем его
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_SEND_REQUEST_CODE
                )
            }
        } else {
            // Разрешение уже предоставлено, выполняем необходимые действия

        }
        viewModel.getRemindersCountItem()
        viewModel.remindersCount.observe(viewLifecycleOwner) {
            if (it != 0) {
                tvCountOfReminders.text = getString(R.string.number_of_reminders) + ": $it"
            } else {
                tvCountOfReminders.text = getString(R.string.no_reminders_yet)
            }

        }
        ivMainMenu.setOnClickListener {
            animatorSetMenu.start()
            launchMenuTypeNotesFragment()
        }
        ivAdd.setOnClickListener {
            launchReminderAddFragment()
        }
        tvCountOfReminders.setOnClickListener {
            animatorSetReminderCount.start()
            launchReminderListFragment()
        }

    }

    private fun initTouchReminderCountAnimation() = with(binding) {
        val bgColorAnimatorMenu = ValueAnimator.ofObject(ArgbEvaluator(), ContextCompat.getColor(requireContext(), R.color.background_click), ContextCompat.getColor(requireContext(), R.color.background_start_click))

        bgColorAnimatorMenu.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            ivMainMenu.setBackgroundColor(color)
        }
        animatorSetMenu = AnimatorSet()
        animatorSetMenu.playTogether(bgColorAnimatorMenu)
        animatorSetMenu.duration = MenuTypeNotesFragment.DEFAULT_DURATION // 500мс
        val bgColorAnimatorReminderCount = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorReminderCount = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        bgColorAnimatorReminderCount.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvCountOfReminders.setBackgroundColor(color)
        }
        textColorAnimatorReminderCount.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvCountOfReminders.setTextColor(color)
        }

        animatorSetReminderCount = AnimatorSet()
        animatorSetReminderCount.playTogether(bgColorAnimatorReminderCount, textColorAnimatorReminderCount)
        animatorSetReminderCount.duration = DEFAULT_DURATION // 500мс
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(requireContext())) {
                // Разрешение предоставлено, можно отображать оверлеи
                showSystemWindow()
            } else {
                // Разрешение не предоставлено
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showSystemWindow() {
    val params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    )
    val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_reminder_notification, null)

    windowManager.addView(view, params)
}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            NOTIFICATION_SEND_REQUEST_CODE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Разрешение на отправку уведомлений получено
//                }
                return
            }
        }
    }


    companion object {
        private const val NOTIFICATION_SEND_REQUEST_CODE = 127
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 128
        const val DEFAULT_DURATION = 500.toLong()
        const val NAME = "StartAppFragment"

        fun newInstance() = StartAppFragment()
    }


    private fun launchReminderListFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderListFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun launchReminderAddFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderAddFragment.newInstance())
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }


    private fun launchMenuTypeNotesFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
            .replace(R.id.main_container, MenuTypeNotesFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}