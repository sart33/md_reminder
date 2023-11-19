package solutions.mobiledev.reminder.presentation.reminder.fragment

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentReminderListBinding
import solutions.mobiledev.reminder.presentation.BaseFragment
import solutions.mobiledev.reminder.presentation.MenuTypeNotesFragment
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