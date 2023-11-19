package solutions.mobiledev.reminder.presentation.settings

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentSettingsBinding
import solutions.mobiledev.reminder.presentation.MenuTypeNotesFragment
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderListFragment

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding ?: throw RuntimeException("FragmentSettingsBinding == null")

    private lateinit var animatorSetMenu: AnimatorSet
    private lateinit var animatorSetLanguage: AnimatorSet
    private lateinit var animatorSetDateFormat: AnimatorSet
    private lateinit var animatorSetTimeFormat: AnimatorSet
    private lateinit var animatorSetNotificationSetting: AnimatorSet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        topBarMenuTouchAnimation()
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        ivMainMenu.setOnClickListener {
            animatorSetMenu.start()
            launchMenuTypeNotesFragment()
        }
        tvAppNotificationVisibilitySettings.setOnClickListener {
            animatorSetLanguage.start()
            launchApplicationLanguageFragment()
        }
        tvAppBackgroundImage.setOnClickListener {
            animatorSetDateFormat.start()
            launchDateSettingsFragment()
        }
        tvAppSoundOptions.setOnClickListener {
            animatorSetTimeFormat.start()
            launchTimeSettingsFragment()
        }
        tvAppNotificationSetting.setOnClickListener {
            animatorSetNotificationSetting.start()
            launchNotificationSettingsFragment()
        }

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

    val bgColorAnimatorLanguage = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )

        bgColorAnimatorLanguage.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvAppNotificationVisibilitySettings.setBackgroundColor(color)
        }
        animatorSetLanguage = AnimatorSet()
        animatorSetLanguage.playTogether(bgColorAnimatorLanguage)
        animatorSetLanguage.duration = DEFAULT_DURATION // 500мс

    val bgColorAnimatorDateFormat = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )

        bgColorAnimatorDateFormat.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvAppBackgroundImage.setBackgroundColor(color)
        }
        animatorSetDateFormat = AnimatorSet()
        animatorSetDateFormat.playTogether(bgColorAnimatorDateFormat)
        animatorSetDateFormat.duration = DEFAULT_DURATION // 500мс

    val bgColorAnimatorTimeFormat = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )

        bgColorAnimatorTimeFormat.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvAppSoundOptions.setBackgroundColor(color)
        }
        animatorSetTimeFormat = AnimatorSet()
        animatorSetTimeFormat.playTogether(bgColorAnimatorTimeFormat)
        animatorSetTimeFormat.duration = DEFAULT_DURATION // 500мс

    val bgColorAnimatorNotificationSetting = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )

        bgColorAnimatorNotificationSetting.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvAppNotificationSetting.setBackgroundColor(color)
        }
        animatorSetNotificationSetting = AnimatorSet()
        animatorSetNotificationSetting.playTogether(bgColorAnimatorNotificationSetting)
        animatorSetNotificationSetting.duration = DEFAULT_DURATION // 500мс


    }
    companion object {

        const val NAME = "SettingsFragment"
        const val DEFAULT_DURATION = 500.toLong()

        fun newInstance() = SettingsFragment()
    }

    private fun launchMenuTypeNotesFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
            .replace(R.id.main_container, MenuTypeNotesFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun launchTimeSettingsFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, TimeFormatFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }


    private fun launchDateSettingsFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, DateFormatFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun launchApplicationLanguageFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ApplicationLanguageFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun launchNotificationSettingsFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, NotificationSettingsFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }


}
