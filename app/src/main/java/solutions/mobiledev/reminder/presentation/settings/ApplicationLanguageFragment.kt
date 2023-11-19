package solutions.mobiledev.reminder.presentation.settings

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentApplicationLanguageBinding
import solutions.mobiledev.reminder.presentation.MainActivity
import solutions.mobiledev.reminder.presentation.MenuTypeNotesFragment
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderListFragment


class ApplicationLanguageFragment : BaseSettingsFragment<FragmentApplicationLanguageBinding>() {


    private var _binding: FragmentApplicationLanguageBinding? = null
    private val binding: FragmentApplicationLanguageBinding
        get() = _binding ?: throw RuntimeException("FragmentApplicationLanguageBinding == null")
    private lateinit var viewModel: ReminderViewModel
    private var mItemSelect: Int = 0
    private lateinit var animatorSet: AnimatorSet
    private lateinit var animatorSetMenu: AnimatorSet
    private lateinit var animatorSetSetting: AnimatorSet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApplicationLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        topBarTouchAnimation()
        viewModel =
            ViewModelProvider(this@ApplicationLanguageFragment)[ReminderViewModel::class.java]

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        ivMainMenu.setOnClickListener {
            animatorSetMenu.start()
            launchMenuTypeNotesFragment()
        }
        ivSetting.setOnClickListener {
            animatorSetSetting.start()
            launchSettingsFragment()
        }
        tvCancelButton.setOnClickListener {
            animatorSet.start()
            launchSettingsFragment()
        }


        val init = prefs.appLanguage


        rbLocale1.text = " ${getString(R.string.english)}"
        rbLocale2.text = " ${getString(R.string.spanish)}"
        rbLocale3.text = " ${getString(R.string.french)}"
        rbLocale4.text = " ${getString(R.string.german)}"
        rbLocale5.text = " ${getString(R.string.sweden)}"
        rbLocale6.text = " ${getString(R.string.ukrainian)}"
        rbLocale7.text = " ${getString(R.string.russian)}"

        when (init) {
            0 -> rbLocale1.isChecked = true
            1 -> rbLocale2.isChecked = true
            2 -> rbLocale3.isChecked = true
            3 -> rbLocale4.isChecked = true
            4 -> rbLocale5.isChecked = true
            5 -> rbLocale6.isChecked = true
            6 -> rbLocale7.isChecked = true
        }

        rbLocale1.setOnClickListener {
            viewModel.setLocale(requireContext(), "en")
            prefs.appLanguage = 0
            launchSettingsFragment()
        }
        rbLocale2.setOnClickListener {
            viewModel.setLocale(requireContext(), "es")
            prefs.appLanguage = 1
            launchSettingsFragment()
        }
        rbLocale3.setOnClickListener {
            viewModel.setLocale(requireContext(), "fr")
            prefs.appLanguage = 2
            launchSettingsFragment()
        }
        rbLocale4.setOnClickListener {
            viewModel.setLocale(requireContext(), "de")
            prefs.appLanguage = 3
            launchSettingsFragment()
        }
        rbLocale5.setOnClickListener {
            viewModel.setLocale(requireContext(), "sv")
            prefs.appLanguage = 4
            launchSettingsFragment()
        }
        rbLocale6.setOnClickListener {
            viewModel.setLocale(requireContext(), "uk")
            prefs.appLanguage = 5
            launchSettingsFragment()
        }
        rbLocale7.setOnClickListener {
            viewModel.setLocale(requireContext(), "ru")
            prefs.appLanguage = 6
            launchSettingsFragment()
        }
    }


    private fun showLanguageDialog() {
        withContext {
            val builder = dialogues.getMaterialDialog(it)
            builder.setCancelable(true)
            builder.setTitle(getString(R.string.application_language))
            val init = prefs.appLanguage
            mItemSelect = init
            builder.setSingleChoiceItems(
                resources.getStringArray(R.array.app_languages),
                mItemSelect
            ) { _, which -> mItemSelect = which }
            builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                prefs.appLanguage = mItemSelect

                dialog.dismiss()
                if (init != mItemSelect) restartApp()
            }
            builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
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
    private fun restartApp() {
        startActivity(Intent(context, MainActivity::class.java))
        activity?.finishAffinity()
    }

    companion object {

        const val NAME = "ApplicationLanguageFragment"
        const val DEFAULT_DURATION = 500.toLong()

        fun newInstance() = ApplicationLanguageFragment()

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