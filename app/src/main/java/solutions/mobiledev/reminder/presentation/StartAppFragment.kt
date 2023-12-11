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
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
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
    private var notificationPermissionRequested = false

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
        checkNotificationPermission()


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

    private fun checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Разрешение не предоставлено, запрашиваем его
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationPermissionRequested) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_SEND_REQUEST_CODE
                )
                notificationPermissionRequested = true
            }
        } else {
            // Разрешение уже предоставлено, показываем сообщение об ограничениях батареи
            batteryManagerOptimization()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_SEND_REQUEST_CODE
            )
        }
    }

    private fun batteryManagerOptimization() {
        val packageName = requireActivity().packageName
        val powerManager = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager

        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            showBatteryOptimizationInfo()
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showBatteryOptimizationInfo() {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val dialogView = layoutInflater.inflate(R.layout.notification_receive_settings_dialog, null)
        val positiveButton = dialogView.findViewById<Button>(R.id.positive_button)
        val negativeButton = dialogView.findViewById<Button>(R.id.negative_button)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.tv_dialog_title)
        val dialogText = dialogView.findViewById<TextView>(R.id.tv_dialog_text)
        dialogTitle.text =
            HtmlCompat.fromHtml(
                getString(R.string.notification_receive_settings),
                HtmlCompat.FROM_HTML_MODE_COMPACT
            )

        dialogText.text =
            HtmlCompat.fromHtml(
                getString(R.string.disable_batery_optimization),
                HtmlCompat.FROM_HTML_MODE_COMPACT
            )

        positiveButton.text = getString(R.string.application_details_settings)
        positiveButton.setOnClickListener {
            navigateToBatteryOptimizationSettings()
            alertDialog.dismiss()
        }

        negativeButton.text = getString(R.string.cancel)
        negativeButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.setView(dialogView)
        alertDialog.show()
    }


    private fun navigateToBatteryOptimizationSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun initTouchReminderCountAnimation() = with(binding) {
        val bgColorAnimatorMenu = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )

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
        animatorSetReminderCount.playTogether(
            bgColorAnimatorReminderCount,
            textColorAnimatorReminderCount
        )
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
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_reminder_notification, null)

        windowManager.addView(view, params)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_SEND_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение получено, показываем оба сообщения
                batteryManagerOptimization()
            }
        }
    }


    companion object {
        const val NOTIFICATION_SEND_REQUEST_CODE = 127
        const val OVERLAY_PERMISSION_REQUEST_CODE = 128
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