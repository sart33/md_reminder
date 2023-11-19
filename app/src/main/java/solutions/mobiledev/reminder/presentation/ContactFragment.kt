package solutions.mobiledev.reminder.presentation

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentContactBinding
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderListFragment


class ContactFragment : Fragment() {
    private var _binding: FragmentContactBinding? = null
    private val binding: FragmentContactBinding
        get() = _binding ?: throw RuntimeException("FragmentContactBinding == null")

    private lateinit var animatorSetMenu: AnimatorSet


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        topBarMenuTouchAnimation()
        ivMainMenu.setOnClickListener {
            animatorSetMenu.start()
            launchMenuTypeNotesFragment()
        }
    }

    companion object {

        const val NAME = "ContactFragment"

        fun newInstance() = ContactFragment()

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
        animatorSetMenu.duration = ReminderListFragment.DEFAULT_DURATION // 500мс
    }
    private fun launchMenuTypeNotesFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
            .replace(R.id.main_container, MenuTypeNotesFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }
}