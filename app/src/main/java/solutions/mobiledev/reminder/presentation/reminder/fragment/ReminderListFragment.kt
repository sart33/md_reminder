package solutions.mobiledev.reminder.presentation.reminder.fragment

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentReminderListBinding
import solutions.mobiledev.reminder.presentation.BaseFragment
import solutions.mobiledev.reminder.presentation.MenuTypeNotesFragment
import solutions.mobiledev.reminder.presentation.StartAppFragment
import solutions.mobiledev.reminder.presentation.notification.ReminderItemNotificationFragment
import solutions.mobiledev.reminder.presentation.reminder.ReminderListAdapter
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.widget.calendar.ReminderCalendarWidget
import solutions.mobiledev.reminder.presentation.widget.calendarTwo.ReminderCalendarTwoWidget
import solutions.mobiledev.reminder.presentation.widget.list.ReminderListWidget

class ReminderListFragment() : BaseFragment<FragmentReminderListBinding>() {

    private lateinit var viewModel: ReminderViewModel

    private var _binding: FragmentReminderListBinding? = null
    private val binding: FragmentReminderListBinding
        get() = _binding ?: throw RuntimeException("FragmentReminderListBinding == null")

    private lateinit var reminderListAdapter: ReminderListAdapter
    private var notificationPermissionRequested = false
    private lateinit var animatorSetMenu: AnimatorSet


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderListBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this@ReminderListFragment)[ReminderViewModel::class.java]
        checkNotificationPermission()
        topBarMenuTouchAnimation()

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        /**
         * observe - подписываеммся на изменение шоплиста
         */
        setupRecyclerView()
        setupLongClickListener()
        setupSwipeListener(rvRemList)
        ivMainMenu.setOnClickListener {
            animatorSetMenu.start()
            launchMenuTypeNotesFragment()
        }



        viewModel.reminderList.observe(viewLifecycleOwner) {
            /**
             * При вызове submitList - внутри адаптера - запускается новый поток, -
             * который и выполняет - все вычисления.
             */
            reminderListAdapter.submitList(it)
        }


        fabAddRem.setOnClickListener() {
            launchReminderAddFragment()
        }
    }

    private fun setupSwipeListener(rvRemList: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = reminderListAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteReminderAll(item)
                updateListWidget()
                updateCalendarWidget()
                updateCalendarTwoWidget()
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvRemList)
    }

    private fun setupOnClickListener() {

        reminderListAdapter.onEditClickListener = {
            launchReminderItemNotificationFragment(it.id)
        }
    }

    private fun setupLongClickListener() {
        reminderListAdapter.onReminderItemLongClickListener = {
            viewModel.changeEnableState(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                    StartAppFragment.NOTIFICATION_SEND_REQUEST_CODE
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
                StartAppFragment.NOTIFICATION_SEND_REQUEST_CODE
            )
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == StartAppFragment.OVERLAY_PERMISSION_REQUEST_CODE) {
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
        if (requestCode == StartAppFragment.NOTIFICATION_SEND_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение получено, показываем оба сообщения
                batteryManagerOptimization()
            }
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
    private fun topBarMenuTouchAnimation() = with(binding)  {
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
        animatorSetMenu.duration = DEFAULT_DURATION // 500мс
    }

    private fun updateListWidget() {
        val widgetManager = AppWidgetManager.getInstance(requireContext())
        val widgetIds = widgetManager.getAppWidgetIds(
            ComponentName(
                requireContext(),
                ReminderListWidget::class.java
            )
        )

        val updateIntent = Intent(requireContext(), ReminderListWidget::class.java)
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        val pendingUpdate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                requireContext(),
                0,
                updateIntent,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                requireContext(),
                0,
                updateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        widgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.lvWidget)
        pendingUpdate.send()
    }

    private fun updateCalendarTwoWidget() {
        val widgetManager = AppWidgetManager.getInstance(requireContext())
        val widgetIds = widgetManager.getAppWidgetIds(
            ComponentName(
                requireContext(), ReminderCalendarTwoWidget::class.java
            )
        )

        val updateIntent = Intent(requireContext(), ReminderCalendarTwoWidget::class.java)
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        val pendingUpdate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                requireContext(), 0, updateIntent, PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                requireContext(), 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        widgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.calendar_grid_view)
        pendingUpdate.send()
    }

    private fun updateCalendarWidget() {
        val widgetManager = AppWidgetManager.getInstance(requireContext())
        val widgetIds = widgetManager.getAppWidgetIds(
            ComponentName(
                requireContext(), ReminderCalendarWidget::class.java
            )
        )

        val updateIntent = Intent(requireContext(), ReminderCalendarWidget::class.java)
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        val pendingUpdate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                requireContext(), 0, updateIntent, PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                requireContext(), 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        widgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.calendar_grid_view)
        pendingUpdate.send()
    }

    companion object {

        const val NAME = "ReminderListFragment"
        const val DEFAULT_DURATION = 500.toLong()


        fun newInstance() = ReminderListFragment()
    }


    private fun launchReminderAddFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderAddFragment.newInstance())
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    private fun launchReminderItemFragment(id: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderItemFragment.newInstance(id))
            .addToBackStack(ReminderItemFragment.NAME)
            .commit()
    }

    private fun launchMenuTypeNotesFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
            .replace(R.id.main_container, MenuTypeNotesFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun launchReminderItemNotificationFragment(id: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
//        setReorderingAllowed(true)
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderItemNotificationFragment.newInstance(id))
            .addToBackStack(null).commitAllowingStateLoss()
    }

    private fun setupRecyclerView() = with(binding) {
        with(rvRemList) {
            reminderListAdapter = ReminderListAdapter()
            adapter = reminderListAdapter
        }
        setupOnClickListener()
    }
}