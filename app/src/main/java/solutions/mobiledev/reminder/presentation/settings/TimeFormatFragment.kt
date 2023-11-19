package solutions.mobiledev.reminder.presentation.settings

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentTimeFormatBinding
import solutions.mobiledev.reminder.presentation.MenuTypeNotesFragment
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderListFragment


class TimeFormatFragment : BaseSettingsFragment<FragmentTimeFormatBinding>() {

    private var _binding: FragmentTimeFormatBinding? = null
    private val binding: FragmentTimeFormatBinding
        get() = _binding ?: throw RuntimeException("FragmentTimeFormatBinding == null")

    private lateinit var animatorSet: AnimatorSet
    private lateinit var animatorSetMenu: AnimatorSet
    private lateinit var animatorSetSetting: AnimatorSet


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimeFormatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        topBarTouchAnimation()
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

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

        swTimeFormat.isChecked = prefs.hourFormat == 0
        swTimeFormat.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                prefs.hourFormat = 0

            } else {
                prefs.hourFormat = 1


            }
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

        const val NAME = "TimeFormatFragment"
        const val DEFAULT_DURATION = 500.toLong()
        fun newInstance() = TimeFormatFragment()
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