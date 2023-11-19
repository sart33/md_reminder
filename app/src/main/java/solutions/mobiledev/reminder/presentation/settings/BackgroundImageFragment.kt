package solutions.mobiledev.reminder.presentation.settings

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentBackgroundImageBinding
import solutions.mobiledev.reminder.databinding.FragmentSoundOptionsBinding
import solutions.mobiledev.reminder.domain.entity.ImageItem
import solutions.mobiledev.reminder.presentation.MenuTypeNotesFragment
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderAddFragment
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class BackgroundImageFragment : BaseSettingsFragment<FragmentBackgroundImageBinding>() {

    private var _binding: FragmentBackgroundImageBinding? = null
    private val binding: FragmentBackgroundImageBinding
        get() = _binding ?: throw RuntimeException("FragmentBackgroundImageBinding == null")

    private lateinit var viewModel: ReminderViewModel
    private lateinit var animatorSet: AnimatorSet
    private lateinit var animatorSetDelete: AnimatorSet
    private lateinit var animatorSetMenu: AnimatorSet
    private lateinit var animatorSetSetting: AnimatorSet
    private lateinit var animatorSetImage: AnimatorSet
    private lateinit var imageList: List<ImageItem>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBackgroundImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        topBarTouchAnimation()
        viewModel =
            ViewModelProvider(this@BackgroundImageFragment)[ReminderViewModel::class.java]

        val adRequest = AdRequest.Builder().build()
        viewModelImageItem()
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
        tvDelete.setOnClickListener {
            animatorSetDelete.start()
            deleteAddingDefaultImage()
        }
        clAddImage.setOnClickListener {
            animatorSetImage.start()
            checkPermissionsAndOpenImagePicker()
        }
    }

    private fun viewModelImageItem() = with(binding) {
        if (prefs.notificationBackgroundImage == 1) {
            val file = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                DEFAULT_IMAGE
            )
            val bitmap = BitmapFactory.decodeFile(file.path)
            ivDefaultBackgroundImage.visibility = View.VISIBLE
            ivDefaultBackgroundImage.setImageBitmap(bitmap)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun checkPermissionsAndOpenImagePicker() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Разрешения уже есть, открываем галерею
            openGallery()

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
                openGallery()
            } else {
                // Если пользователь отклонил запрос на разрешение, выводим диалоговое окно
                val dialog =
                    AlertDialog.Builder(requireContext()).setTitle("Разрешение не получено")
                        .setMessage("Для загрузки файлов необходимо дать разрешение на чтение внешнего хранилища.")
                        .setPositiveButton("ОК") { _, _ -> requireActivity() }.create()
                dialog.show()
            }
        }


    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { imageUri ->
                // Вызываем метод processImage для обработки изображения
                //                    deleteAddingImageByRemId(0)
                saveImage(imageUri)
            }
        }

    private fun saveImage(uri: Uri) = with(binding) {
        deleteAddingDefaultImage()
        // Check if the image is valid
        val selectedImageBitmap = getBitmapFromUri(uri)
        val isValidImage = isValidImage(uri)
        val fileName = DEFAULT_IMAGE
        if (isValidImage) {
            // Save the image to external storage
            saveImageToExternalStorage(selectedImageBitmap!!, fileName)
            // Save the image to the database
            prefs.notificationBackgroundImage = 1
            val file = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName
            )
            val bitmap = BitmapFactory.decodeFile(file.path)
            ivDefaultBackgroundImage.visibility = View.VISIBLE
            ivDefaultBackgroundImage.setImageBitmap(bitmap)
            Toast.makeText(requireContext(), "Image saved successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Image to small", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidImage(uri: Uri): Boolean {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFileDescriptor(
            requireActivity().contentResolver.openFileDescriptor(uri, "r")?.fileDescriptor,
            null,
            options
        )

        val imageWidth = options.outWidth
        val imageHeight = options.outHeight

        return imageWidth >= ReminderAddFragment.MIN_IMAGE_WIDTH && imageHeight >= ReminderAddFragment.MIN_IMAGE_HEIGHT
    }

    private fun deleteAddingDefaultImage() = with(binding) {
        val fileName = DEFAULT_IMAGE
        viewModel.deleteImageFromInternalStorage(
            requireContext(), fileName
        )
        prefs.notificationBackgroundImage = 0
        ivDefaultBackgroundImage.visibility = View.INVISIBLE
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        val parcelFileDescriptor = requireActivity().contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor?.close()
        return image
    }

    private fun saveImageToExternalStorage(selectedImageBitmap: Bitmap, fileName: String) {
        val displayMetrics = requireContext().resources.displayMetrics
        val newWidth = displayMetrics.widthPixels
        val outputStream = FileOutputStream(
            File(
                requireContext().getExternalFilesDir("Pictures"), fileName
            )
        )
        val scaledBitmap = viewModel.resizeImageWithAspectRatio(selectedImageBitmap, newWidth)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.flush()
        outputStream.close()
    }

    private fun openGallery() {
        getContent.launch("image/*")
    }


    private fun topBarTouchAnimation() = with(binding) {
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
        animatorSetMenu.duration = SoundOptionsFragment.DEFAULT_DURATION // 500мс


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
        animatorSetSetting.duration = SoundOptionsFragment.DEFAULT_DURATION // 500мс


        val bgColorAnimatorImage = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )

        bgColorAnimatorImage.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clAddImageWrapper.setBackgroundColor(color)
        }
        animatorSetImage = AnimatorSet()
        animatorSetImage.playTogether(bgColorAnimatorImage)
        animatorSetImage.duration = DEFAULT_DURATION // 500мс
    }

    companion object {

        const val NAME = "BackgroundImageFragment"
        const val DEFAULT_DURATION = 500.toLong()
        const val DEFAULT_IMAGE = "default_image.jpg"
        fun newInstance() = BackgroundImageFragment()
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