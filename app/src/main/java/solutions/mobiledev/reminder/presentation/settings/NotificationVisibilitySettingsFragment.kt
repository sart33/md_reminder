package solutions.mobiledev.reminder.presentation.settings

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentNotificationSettingsBinding
import solutions.mobiledev.reminder.databinding.FragmentNotificationVisibilitySettingsBinding
import solutions.mobiledev.reminder.presentation.MenuTypeNotesFragment
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderListFragment
import java.util.*


@Suppress("DEPRECATION")
class NotificationVisibilitySettingsFragment : BaseSettingsFragment<FragmentNotificationVisibilitySettingsBinding>() {

    private var _binding: FragmentNotificationVisibilitySettingsBinding? = null
    private val binding: FragmentNotificationVisibilitySettingsBinding
        get() = _binding ?: throw RuntimeException("FragmentNotificationVisibilitySettingsBinding == null")

    private lateinit var animatorSet: AnimatorSet
    private lateinit var animatorSetMenu: AnimatorSet
    private lateinit var animatorSetSetting: AnimatorSet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationVisibilitySettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        topBarTouchAnimation()
        val packageName = requireContext().packageName
        if ("xiaomi" == Build.MANUFACTURER.toLowerCase(Locale.ROOT)) {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            intent.putExtra("extra_pkgname", packageName)
            startActivity(intent)
        }


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

        swTurnOnFullScreenOnHome.isChecked = prefs.homeScreenFull == 0
        swTurnOnFullScreenOnHome.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                prefs.homeScreenFull = 0
                if (!Settings.canDrawOverlays(requireContext())) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    intent.data = Uri.parse("package:$packageName")
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)

                }
            } else {
                prefs.homeScreenFull = 1
            }
        }


        swTurnOnFullScreenOnLock.isChecked = prefs.lockScreenFull == 0
        swTurnOnFullScreenOnLock.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                prefs.lockScreenFull = 0
            } else {
                prefs.lockScreenFull = 1
            }

        }


        btLockScreenSettings.setOnClickListener {
            val intent = Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", requireActivity().packageName, null)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("android.provider.extra.APP_PACKAGE", requireActivity().packageName)
            startActivity(intent)
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
        fun newInstance() = NotificationVisibilitySettingsFragment()
        const val NAME = "NotificationVisibilitySettingsFragment"
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 128
        const val DEFAULT_DURATION = 500.toLong()

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