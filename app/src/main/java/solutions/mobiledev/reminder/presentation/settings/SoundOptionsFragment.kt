package solutions.mobiledev.reminder.presentation.settings

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentDateFormatBinding
import solutions.mobiledev.reminder.databinding.FragmentSoundOptionsBinding
import solutions.mobiledev.reminder.domain.entity.MelodyItem
import solutions.mobiledev.reminder.presentation.MenuTypeNotesFragment
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderAddFragment
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderListFragment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class SoundOptionsFragment : BaseSettingsFragment<FragmentSoundOptionsBinding>() {

    private var _binding: FragmentSoundOptionsBinding? = null
    private val binding: FragmentSoundOptionsBinding
    get() = _binding ?: throw RuntimeException("SoundOptionsFragmentBinding == null")
    private lateinit var viewModel: ReminderViewModel
    private lateinit var animatorSet: AnimatorSet
    private lateinit var animatorSetMenu: AnimatorSet
    private lateinit var animatorSetSetting: AnimatorSet
    private lateinit var animatorSetSelectSound: AnimatorSet
    private lateinit var animatorSetDelete: AnimatorSet
    private lateinit var melodyList: List<MelodyItem>
    private lateinit var melodyName: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSoundOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        topBarTouchAnimation()
        viewModelSoundItem()
        viewModel =
            ViewModelProvider(this@SoundOptionsFragment)[ReminderViewModel::class.java]

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
            launchNotificationSettingsFragment()
        }
        clSoundBlock.setOnClickListener {
            animatorSetSelectSound.start()
            checkPermissionsAndOpenMelodyPicker()
        }
        tvDelete.setOnClickListener {
            animatorSetDelete.start()
            deleteAddingDefaultMelody()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun viewModelSoundItem() = with(binding) {
        if ((prefs.notificationDefaultSound).isNullOrEmpty()) {
            } else {
            cvMelodyName.visibility = View.VISIBLE
            tvMelodyName.text = prefs.notificationDefaultSound

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        when (requestCode) {

            ReminderAddFragment.ACCESS_NOTIFICATION_POLICY_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение на отправку уведомлений получено
                } else {

                }
                return
            }

            ReminderAddFragment.FOREGROUND_SERVICE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение на отправку уведомлений получено
                } else {

                }
                return
            }
        }
    }



    private val getContentMelody =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        val melodyName = getFileNameFromUri(uri)
                        if (validateAudioFile(melodyName)) {
                            if (saveMelodyToInternalStorage(inputStream, melodyName)) {
                                binding.tvMelodyName.text = melodyName
                                binding.cvMelodyName.visibility = View.VISIBLE
                            } else {
                                // failed to save file
                            }
                        }
                    }
                } catch (_: Exception) {
                }
            } else {
                // user canceled
            }
        }

    private fun saveMelodyToInternalStorage(inputStream: InputStream, fileName: String): Boolean {
        deleteAddingDefaultMelody()
        var success = false
        try {
            val outputStream = FileOutputStream(
                File(
                    requireContext().getExternalFilesDir("Music"), "default_$fileName"
                )
            )
            prefs.notificationDefaultSound = fileName
            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()
            binding.cvMelodyName.visibility = View.VISIBLE
            binding.tvMelodyName.text = fileName
            Toast.makeText(requireContext(), "Melody saved successfully", Toast.LENGTH_SHORT).show()
            success = true


        } catch (e: Exception) {
            Log.e("AddReminder", "Error saving file to internal storage: ${e.message}")
        }
        return success
    }


    private fun deleteAddingDefaultMelody() = with(binding) {
        if ((prefs.notificationDefaultSound).isNullOrEmpty()) {
        } else {
           val fileName = prefs.notificationDefaultSound
            viewModel.deleteMelodyFromInternalStorage(
                requireContext(),  "default_$fileName"
            )
            cvMelodyName.visibility = View.INVISIBLE
            prefs.notificationDefaultSound = ""
        }
    }



    @SuppressLint("Range")
    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = ""
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            val mimeType = requireContext().contentResolver.getType(uri)

            if (!displayName.isNullOrEmpty()) {
                fileName = displayName
            } else {
                val path = uri.path
                fileName = path?.substring(path.lastIndexOf('/') + 1) ?: ""
            }

            // Если MIME тип известен, получаем расширение файла
            if (!mimeType.isNullOrEmpty()) {
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                if (!extension.isNullOrEmpty() && !fileName.endsWith(".$extension")) {
                    fileName += ".$extension"
                }
            }
        }
        return fileName
    }

//    private fun getFileNameFromUri(uri: Uri): String {
//        var fileName = ""
//        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val displayNameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                if (displayNameColumnIndex != -1) {
//                    fileName = it.getString(displayNameColumnIndex)
//                }
//            }
//        }
//        return fileName
//    }
    private fun validateAudioFile(fileName: String): Boolean {
        val validExtensions =
            arrayOf(".mp3", ".wav", ".ogg", ".aac", ".flac", ".wma") // Допустимые расширения файлов
        val fileExtension = getFileExtension(fileName)
        return validExtensions.contains(fileExtension)
    }

    private fun getFileExtension(fileName: String): String {
        val lastDotIndex = fileName.lastIndexOf('.')
        return if (lastDotIndex >= 0) {
            fileName.substring(lastDotIndex)
            fileName.substring(lastDotIndex)
        } else {
            ""
        }
    }

    private fun checkPermissionsAndOpenMelodyPicker() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Разрешения уже есть, открываем галерею
            openMelody()

        } else {
            // Запрашиваем разрешения
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true && permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) {
                // Разрешения получены

            } else {
                // Если пользователь отклонил запрос на разрешение, выводим диалоговое окно
                val dialog =
                    AlertDialog.Builder(requireContext()).setTitle("Разрешение не получено")
                        .setMessage("Для загрузки файлов необходимо дать разрешение на чтение внешнего хранилища.")
                        .setPositiveButton("ОК") { _, _ -> requireActivity() }.create()
                dialog.show()
            }
        }
    private fun openMelody() {
        getContentMelody.launch("audio/*")
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
        animatorSet.duration = DEFAULT_DURATION // 500мс

       val bgColorAnimatorDelete = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorDelete = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )

        bgColorAnimatorDelete.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvDelete.setBackgroundColor(color)
        }

        textColorAnimatorDelete.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvDelete.setTextColor(color)
        }

        animatorSetDelete = AnimatorSet()
        animatorSetDelete.playTogether(bgColorAnimatorDelete, textColorAnimatorDelete)
        animatorSetDelete.duration = DEFAULT_DURATION // 500мс

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

        val bgColorAnimatorSelectSound = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )

        bgColorAnimatorSelectSound.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clSoundBlockWrapper.setBackgroundColor(color)
        }
        animatorSetSelectSound = AnimatorSet()

        animatorSetSelectSound.playTogether(bgColorAnimatorSelectSound)
        animatorSetSelectSound.duration = DEFAULT_DURATION // 500мс
    }
    companion object {
        const val NAME = "SoundOptionsFragment"
        const val DEFAULT_DURATION = 500.toLong()
        fun newInstance() = SoundOptionsFragment()
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

    private fun launchNotificationSettingsFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, NotificationSettingsFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }
}