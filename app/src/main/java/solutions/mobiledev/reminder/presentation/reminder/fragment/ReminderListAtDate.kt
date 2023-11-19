package solutions.mobiledev.reminder.presentation.reminder.fragment

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentReminderListAtDateBinding
import solutions.mobiledev.reminder.presentation.MenuTypeNotesFragment
import solutions.mobiledev.reminder.presentation.notification.ReminderItemNotificationFragment
import solutions.mobiledev.reminder.presentation.reminder.ReminderListAdapter
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.widget.calendar.ReminderCalendarWidget
import solutions.mobiledev.reminder.presentation.widget.calendarTwo.ReminderCalendarTwoWidget
import solutions.mobiledev.reminder.presentation.widget.list.ReminderListWidget


    private lateinit var viewModel: ReminderViewModel

    @SuppressLint("StaticFieldLeak")
    private var _binding: FragmentReminderListAtDateBinding? = null
    private val binding: FragmentReminderListAtDateBinding
        get() = _binding ?: throw RuntimeException("FragmentReminderListAtDateBinding == null")

    private lateinit var reminderListAdapter: ReminderListAdapter
    private var remaindersDate: String = "10-05-2023"
    private lateinit var animatorSetMenu: AnimatorSet


    class ReminderListAtDate : Fragment() {


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            parseParams()

        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            // Inflate the layout for this fragment
            _binding = FragmentReminderListAtDateBinding.inflate(inflater, container, false)
            return binding.root
        }


        override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
            super.onViewCreated(view, savedInstanceState)
            viewModel = ViewModelProvider(this@ReminderListAtDate)[ReminderViewModel::class.java]
            topBarMenuTouchAnimation()
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)

            setupRecyclerView()
            setupLongClickListener()
            setupSwipeListener(rvRemList)
            ivMainMenu.setOnClickListener {
                animatorSetMenu.start()
                launchMenuTypeNotesFragment()
            }
            viewModel.getReminderViewItem(remaindersDate)
            viewModel.remindersAtDate.observe(viewLifecycleOwner) {

                /**
                 * При вызове submitList - внутри адаптера - запускается новый поток, -
                 * который и выполняет - все вычисления.
                 */
                reminderListAdapter.submitList(it)
            }


            fabAddRem.setOnClickListener() {
                launchReminderAddFragment(remaindersDate)
            }
    //        }
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

        private fun topBarMenuTouchAnimation() = with(binding) {
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
            animatorSetMenu.duration = ReminderListFragment.DEFAULT_DURATION // 500мс
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


        private fun parseParams() {
            val args = requireArguments()

            if (!args.containsKey(SELECTED_DATE)) {
                throw RuntimeException("Param reminders item id is absent")
            }
            remaindersDate = args.getString(SELECTED_DATE, "")
        }

        companion object {

            const val NAME = "ReminderListAtDate"
            private const val DATE = "date"
            private const val OPEN_REMINDERS_LIST_OF_DATE_FRAGMENT = "reminders_list_of_date_fragment"
            private const val SELECTED_DATE = "selected_date"
            const val DEFAULT_DURATION = 500.toLong()


            fun newInstance(date: String?): ReminderListAtDate {
                return ReminderListAtDate().apply {
                    arguments = Bundle().apply {
                        putString(SELECTED_DATE, date)

                    }
                }
            }
        }


        private fun launchReminderAddFragment(remaindersDate: String) {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.main_container, ReminderAddFragment.newInstance(remaindersDate))
                .addToBackStack(ReminderAddFragment.NAME)
                .commit()
        }

        private fun launchReminderItemFragment(id: Int) {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.main_container, ReminderItemFragment.newInstance(id))
                .addToBackStack(ReminderItemFragment.NAME)
                .commit()
        }

        private fun launchReminderItemNotificationFragment(id: Int) {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.main_container, ReminderItemNotificationFragment.newInstance(id))
                .addToBackStack(null).commit()
        }

        private fun launchMenuTypeNotesFragment() {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(R.id.main_container, MenuTypeNotesFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        private fun setupRecyclerView() = with(binding) {
            with(rvRemList) {
                reminderListAdapter = ReminderListAdapter()
                adapter = reminderListAdapter
            }
            setupOnClickListener()
        }
}
