package solutions.mobiledev.reminder.presentation

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentMenuTypeNotesBinding
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderListFragment
import solutions.mobiledev.reminder.presentation.settings.SettingsFragment


class MenuTypeNotesFragment : Fragment() {


    private var _binding: FragmentMenuTypeNotesBinding? = null
    private val binding: FragmentMenuTypeNotesBinding
        get() = _binding ?: throw RuntimeException("FragmentMenuTypeNotesBinding == null")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuTypeNotesBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
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
        val bgColorAnimator2 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimator2 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        val bgColorAnimator3 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimator3 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        val bgColorAnimator4 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimator4 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        val bgColorAnimator5 = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )

        bgColorAnimator.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            cvSettings.setBackgroundColor(color)
        }

        textColorAnimator.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvSettings.setTextColor(color)
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(bgColorAnimator, textColorAnimator)
        animatorSet.duration = DEFAULT_DURATION // 500мс

        bgColorAnimator2.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clHome.setBackgroundColor(color)
        }

        textColorAnimator2.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvSettings.setTextColor(color)
        }

        val animatorSet2 = AnimatorSet()
        animatorSet2.playTogether(bgColorAnimator2, textColorAnimator2)
        animatorSet2.duration = DEFAULT_DURATION // 500мс

        bgColorAnimator3.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            cvContacts.setBackgroundColor(color)
        }

        textColorAnimator3.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvContacts.setTextColor(color)
        }

        val animatorSet3 = AnimatorSet()
        animatorSet3.playTogether(bgColorAnimator3, textColorAnimator3)
        animatorSet3.duration = DEFAULT_DURATION // 500мс


        bgColorAnimator4.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clReminder.setBackgroundColor(color)
        }

        textColorAnimator4.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvReminders.setTextColor(color)
        }

        val animatorSet4 = AnimatorSet()
        animatorSet4.playTogether(bgColorAnimator4, textColorAnimator4)
        animatorSet4.duration = DEFAULT_DURATION // 500мс

        bgColorAnimator5.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            ivCloseMainMenu.setBackgroundColor(color)
        }
        val animatorSet5 = AnimatorSet()
        animatorSet5.playTogether(bgColorAnimator5)
        animatorSet5.duration = DEFAULT_DURATION // 500мс

        ivCloseMainMenu.setOnClickListener {
            animatorSet5.start()
            goToPrev()
        }
        clReminder.setOnClickListener {
            animatorSet4.start()
            launchReminderListFragment()
        }
        cvSettings.setOnClickListener {
            animatorSet.start()
            launchSettingsFragment()
        }

        clHome.setOnClickListener {
            animatorSet2.start()
            launchStartFragment()
        }
        ivDeleteSms1.setOnClickListener {
            launchContactsFragment()
        }
        cvContacts.setOnClickListener {
            animatorSet3.start()
            launchContactsFragment()
        }
    }


    private fun goToPrev() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
        fragmentManager.popBackStack()
        fragmentTransaction.commit()

    }

    companion object {

        const val NAME = "MenuTypeNotesFragment"
        const val DEFAULT_DURATION = 500.toLong()

        fun newInstance() = MenuTypeNotesFragment()
    }

    private fun launchSettingsFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, SettingsFragment.newInstance())
            .addToBackStack(SettingsFragment.NAME)
            .commit()
    }

    private fun launchStartFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, StartAppFragment.newInstance())
            .addToBackStack(StartAppFragment.NAME)
            .commit()
    }

    private fun launchReminderListFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderListFragment.newInstance())
            .addToBackStack(ReminderListFragment.NAME)
            .commit()
    }

    private fun launchContactsFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ContactFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}