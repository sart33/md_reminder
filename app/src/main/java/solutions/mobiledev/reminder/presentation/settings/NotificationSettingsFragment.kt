package solutions.mobiledev.reminder.presentation.settings

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentNotificationSettingsBinding
import solutions.mobiledev.reminder.databinding.FragmentSettingsBinding
import solutions.mobiledev.reminder.presentation.MenuTypeNotesFragment


class NotificationSettingsFragment : Fragment() {
    private var _binding: FragmentNotificationSettingsBinding? = null
    private val binding: FragmentNotificationSettingsBinding
        get() = _binding ?: throw RuntimeException("FragmentNotificationSettingsBinding == null")

    private lateinit var animatorSetMenu: AnimatorSet
    private lateinit var animatorSetSetting: AnimatorSet
    private lateinit var animatorSetSoundOptions: AnimatorSet
    private lateinit var animatorSetBackgroundImage: AnimatorSet
    private lateinit var animatorSetNotificationVisibilitySettings: AnimatorSet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationSettingsBinding.inflate(inflater, container, false)
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
        ivSetting.setOnClickListener {
            animatorSetSetting.start()
            launchSettingFragment()
        }
        tvAppBackgroundImage.setOnClickListener {
            animatorSetBackgroundImage.start()
            launchBackgroundImageFragment()
        }
        tvAppSoundOptions.setOnClickListener {
            animatorSetSoundOptions.start()
            launchSoundOptionsFragment()
        }
        tvAppNotificationVisibilitySettings.setOnClickListener {
            animatorSetNotificationVisibilitySettings.start()
            launchNotificationVisibilitySettingsFragment()
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

        val bgColorAnimatorSoundOptions = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )

        bgColorAnimatorSoundOptions.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvAppSoundOptions.setBackgroundColor(color)
        }
        animatorSetSoundOptions = AnimatorSet()
        animatorSetSoundOptions.playTogether(bgColorAnimatorSoundOptions)
        animatorSetSoundOptions.duration = DEFAULT_DURATION // 500мс

        val bgColorAnimatorBackgroundImage = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )

        bgColorAnimatorBackgroundImage.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvAppBackgroundImage.setBackgroundColor(color)
        }
        animatorSetBackgroundImage = AnimatorSet()
        animatorSetBackgroundImage.playTogether(bgColorAnimatorBackgroundImage)
        animatorSetBackgroundImage.duration = DEFAULT_DURATION // 500мс


        val bgColorAnimatorNotificationVisibilitySettings = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )

        bgColorAnimatorNotificationVisibilitySettings.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvAppNotificationVisibilitySettings.setBackgroundColor(color)
        }
        animatorSetNotificationVisibilitySettings = AnimatorSet()
        animatorSetNotificationVisibilitySettings.playTogether(bgColorAnimatorNotificationVisibilitySettings)
        animatorSetNotificationVisibilitySettings.duration = DEFAULT_DURATION // 500мс

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
        animatorSetSetting.duration = DateFormatFragment.DEFAULT_DURATION // 500мс


    }
    companion object {

        const val NAME = "NotificationSettingsFragment"
        const val DEFAULT_DURATION = 500.toLong()

        fun newInstance() = NotificationSettingsFragment()
    }

    private fun launchMenuTypeNotesFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
            .replace(R.id.main_container, MenuTypeNotesFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun launchBackgroundImageFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, BackgroundImageFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun launchSoundOptionsFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, SoundOptionsFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun launchNotificationVisibilitySettingsFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, NotificationVisibilitySettingsFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun launchSettingFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, SettingsFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }
}