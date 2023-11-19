package solutions.mobiledev.reminder.presentation.settings

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentDateFormatBinding
import solutions.mobiledev.reminder.presentation.MenuTypeNotesFragment
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderListFragment
import java.text.SimpleDateFormat
import java.util.*


class DateFormatFragment : BaseSettingsFragment<FragmentDateFormatBinding>() {

    private var _binding: FragmentDateFormatBinding? = null
    private val binding: FragmentDateFormatBinding
        get() = _binding ?: throw RuntimeException("FragmentDateFormatBinding == null")
    private lateinit var viewModel: ReminderViewModel
    private lateinit var animatorSet: AnimatorSet
    private lateinit var animatorSetMenu: AnimatorSet
    private lateinit var animatorSetSetting: AnimatorSet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDateFormatBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        viewModel = ViewModelProvider(this@DateFormatFragment)[ReminderViewModel::class.java]
        super.onViewCreated(view, savedInstanceState)
        topBarTouchAnimation()

        tvCancelButton.setOnClickListener {
            animatorSet.start()
            launchSettingsFragment()
        }
        ivMainMenu.setOnClickListener {
            animatorSetMenu.start()
            launchMenuTypeNotesFragment()
        }
        ivSetting.setOnClickListener {
            animatorSetSetting.start()
            launchSettingsFragment()
        }


        val init = prefs.formatDate
        val cal = GregorianCalendar()

        val dateFormatOne = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateFormatTwo = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateFormatThree = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val dateFormatFour = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val dateFormatFive = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val dateFormatSix = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val dateFormatSeven = SimpleDateFormat("MM.dd.yyyy", Locale.getDefault())
        val dateFormatEight = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val dateFormatNine = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())

        val dateFormatTen = SimpleDateFormat("yyyy MMMM dd", Locale.getDefault())
        val dateFormatEleven = SimpleDateFormat("yyyy dd MMMM", Locale.getDefault())
        val dateFormatTwelve = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val dateFormatThirteen = SimpleDateFormat("MMMM dd yyyy", Locale.getDefault())


        val dateOne = dateFormatOne.format(cal.time)
        val dateTwo = dateFormatTwo.format(cal.time)
        val dateThree = dateFormatThree.format(cal.time)
        val dateFour = dateFormatFour.format(cal.time)
        val dateFive = dateFormatFive.format(cal.time)
        val dateSix = dateFormatSix.format(cal.time)
        val dateSeven = dateFormatSeven.format(cal.time)
        val dateEight = dateFormatEight.format(cal.time)
        val dateNine = dateFormatNine.format(cal.time)
        val dateTen = dateFormatTen.format(cal.time)
        val dateEleven = dateFormatEleven.format(cal.time)
        val dateTwelve = dateFormatTwelve.format(cal.time)
        val dateThirteen = dateFormatThirteen.format(cal.time)
        rbDateFormat1.text = "    $dateOne"
        rbDateFormat2.text = "    $dateTwo"
        rbDateFormat3.text = "    $dateThree"
        rbDateFormat4.text = "    $dateFour"
        rbDateFormat5.text = "    $dateFive"
        rbDateFormat6.text = "    $dateSix"
        rbDateFormat7.text = "    $dateSeven"
        rbDateFormat8.text = "    $dateEight"
        rbDateFormat9.text = "    $dateNine"
        rbDateFormat10.text = "    $dateTen"
        rbDateFormat11.text = "    $dateEleven"
        rbDateFormat12.text = "    $dateTwelve"
        rbDateFormat13.text = "    $dateThirteen"

        when (init) {
            "dd.MM.yyyy" -> rbDateFormat1.isChecked = true
            "dd/MM/yyyy" -> rbDateFormat2.isChecked = true
            "dd-MM-yyyy" -> rbDateFormat3.isChecked = true
            "yyyy.MM.dd" -> rbDateFormat4.isChecked = true
            "yyyy/MM/dd" -> rbDateFormat5.isChecked = true
            "yyyy-MM-dd" -> rbDateFormat6.isChecked = true
            "MM.dd.yyyy" -> rbDateFormat7.isChecked = true
            "MM/dd/yyyy" -> rbDateFormat8.isChecked = true
            "MM-dd-yyyy" -> rbDateFormat9.isChecked = true
            "yyyy MMMM dd" -> rbDateFormat10.isChecked = true
            "yyyy dd MMMM" -> rbDateFormat11.isChecked = true
            "dd MMMM yyyy" -> rbDateFormat12.isChecked = true
            "MMMM dd yyyy" -> rbDateFormat13.isChecked = true
        }
        rbDateFormat1.setOnClickListener {
            prefs.formatDate = "dd.MM.yyyy"
            launchSettingsFragment()
        }
        rbDateFormat2.setOnClickListener {
            prefs.formatDate = "dd/MM/yyyy"
            launchSettingsFragment()
        }
        rbDateFormat3.setOnClickListener {
            prefs.formatDate = "dd-MM-yyyy"
            launchSettingsFragment()
        }
        rbDateFormat4.setOnClickListener {
            prefs.formatDate = "yyyy.MM.dd"
            launchSettingsFragment()
        }
        rbDateFormat5.setOnClickListener {
            prefs.formatDate = "yyyy/MM/dd"
            launchSettingsFragment()
        }
        rbDateFormat6.setOnClickListener {
            prefs.formatDate = "yyyy-MM-dd"
            launchSettingsFragment()
        }

        rbDateFormat7.setOnClickListener {
            prefs.formatDate = "MM.dd.yyyy"
            launchSettingsFragment()
        }
        rbDateFormat8.setOnClickListener {
            prefs.formatDate = "MM/dd/yyyy"
            launchSettingsFragment()
        }
        rbDateFormat9.setOnClickListener {
            prefs.formatDate = "MM-dd-yyyy"
            launchSettingsFragment()
        }

        rbDateFormat10.setOnClickListener {
            prefs.formatDate = "yyyy MMMM dd"
            launchSettingsFragment()
        }
        rbDateFormat11.setOnClickListener {
            prefs.formatDate = "yyyy dd MMMM"
            launchSettingsFragment()
        }
        rbDateFormat12.setOnClickListener {
            prefs.formatDate = "dd MMMM yyyy"
            launchSettingsFragment()
        }
        rbDateFormat13.setOnClickListener {
            prefs.formatDate = "MMMM dd yyyy"
            launchSettingsFragment()
        }
    }

    private fun topBarTouchAnimation() = with(binding)  {
        val bgColorAnimator = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimator = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )

        bgColorAnimator.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvCancelButton.setBackgroundColor(color)
        }

        textColorAnimator.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvCancelButton.setTextColor(color)
        }

        animatorSet = AnimatorSet()
        animatorSet.playTogether(bgColorAnimator, textColorAnimator)
        animatorSet.duration = 500 // 500мс
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


        val bgColorAnimatorSetting = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )

        bgColorAnimatorSetting.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            ivSetting.setBackgroundColor(color)
        }
        animatorSetSetting = AnimatorSet()
        animatorSetSetting.playTogether(bgColorAnimatorSetting)
        animatorSetSetting.duration = DEFAULT_DURATION // 500мс
    }
    companion object {

        const val NAME = "DateFormatFragment"
        const val DEFAULT_DURATION = 500.toLong()

        fun newInstance() = DateFormatFragment()
    }

    private fun launchMenuTypeNotesFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
            .replace(R.id.main_container, MenuTypeNotesFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun launchSettingsFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, SettingsFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }
}