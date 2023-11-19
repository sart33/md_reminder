package solutions.mobiledev.reminder.presentation.reminder.fragment

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.net.Uri.parse
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.text.Editable
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentReminderAddBinding
import solutions.mobiledev.reminder.domain.entity.EmailItem
import solutions.mobiledev.reminder.domain.entity.FileItem
import solutions.mobiledev.reminder.domain.entity.ImageItem
import solutions.mobiledev.reminder.domain.entity.MelodyItem
import solutions.mobiledev.reminder.domain.entity.PhoneNumberItem
import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.entity.ReminderItem.Companion.DEFAULT_VALUE_MENU_REPEAT_COUNT
import solutions.mobiledev.reminder.domain.entity.ReminderItem.Companion.DEFAULT_VALUE_MENU_REPEAT_FREQUENCY
import solutions.mobiledev.reminder.domain.entity.ReminderItem.Companion.DEFAULT_VALUE_MENU_TIMEOUT
import solutions.mobiledev.reminder.domain.entity.SaveContactItem
import solutions.mobiledev.reminder.domain.entity.SmsItem
import solutions.mobiledev.reminder.presentation.BaseFragment
import solutions.mobiledev.reminder.presentation.contact.ContactsFragment
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.widget.calendar.ReminderCalendarWidget
import solutions.mobiledev.reminder.presentation.widget.calendarTwo.ReminderCalendarTwoWidget
import solutions.mobiledev.reminder.presentation.widget.list.ReminderListWidget
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class ReminderAddFragment : BaseFragment<FragmentReminderAddBinding>() {

    private var _binding: FragmentReminderAddBinding? = null
    private val binding: FragmentReminderAddBinding
        get() = _binding ?: throw RuntimeException("FragmentReminderAddBinding == null")

    private lateinit var viewModel: ReminderViewModel
    private lateinit var saveContactList: List<SaveContactItem>
    private lateinit var imageList: List<ImageItem>
    private lateinit var fileList: List<FileItem>
    private lateinit var melodyList: List<MelodyItem>
    private lateinit var smsList: List<SmsItem>
    private lateinit var phoneNumberList: List<PhoneNumberItem>
    private lateinit var emailList: List<EmailItem>
    private lateinit var animatorSetContact: AnimatorSet
    private lateinit var animatorSetEmail: AnimatorSet
    private lateinit var animatorSetSms: AnimatorSet
    private lateinit var animatorSetPhone: AnimatorSet
    private lateinit var animatorSetSave: AnimatorSet
    private lateinit var animatorSetIvSave: AnimatorSet
    private lateinit var animatorSetIvCancel: AnimatorSet
    private lateinit var animatorSetImage: AnimatorSet
    private lateinit var animatorSetMelody: AnimatorSet
    private lateinit var animatorSetFile: AnimatorSet
    private lateinit var animatorSetDate: ObjectAnimator
    private lateinit var animatorSetTime: ObjectAnimator
    private var addContacts: Boolean = false
    private var addSms: Boolean = false
    private var addPhoneNumber: Boolean = false
    private var addEmail: Boolean = false
    private var addImage: Boolean = false
    private var addFile: Boolean = false
    private var addMelody: Boolean = false
    private lateinit var dateItem: String
    private lateinit var timeItem: String
    private var remindDate: String = ""
    private var reminderMenuTimeOut: Int = DEFAULT_VALUE_MENU_TIMEOUT
    private var reminderMenuCount: Int = DEFAULT_VALUE_MENU_REPEAT_COUNT
    private var reminderMenuFrequency: Int = DEFAULT_VALUE_MENU_REPEAT_FREQUENCY
    private var reminderItemId: Int = UNDEFINED_ID
    private lateinit var reminderItem: ReminderItem
    private lateinit var reminderItemTwo: ReminderItem
    private var count = 0
    private var saveCount = 0
    private var reminderText = ""
    private var isReadyForTransition = false
    private var editableTextField = ""


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        when (requestCode) {

            ACCESS_NOTIFICATION_POLICY_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение на отправку уведомлений получено
                } else {

                }
                return
            }

            FOREGROUND_SERVICE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение на отправку уведомлений получено
                } else {

                }
                return
            }
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


    private val getContentFile =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    if (inputStream != null) {

                        val fileName = getFileNameFromUri(uri)
                        if (saveFileToInternalStorage(inputStream, fileName)) {
                            binding.ivAddFile.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.file_added
                                )
                            )
                            binding.tvAddFile.text = fileName
                        } else {
                            // failed to save file
                        }
                    }

                } catch (_: Exception) {
                }
            } else {
                // user canceled
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
                                binding.ivMelody.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.melody
                                    )
                                )
                                binding.tvMelody.text = ""
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

    private val requestPermissionLauncherTwo =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true && permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) {
                // Разрешения получены
                getContentFile.launch("*/*")
            } else {
                // Если пользователь отклонил запрос на разрешение, выводим диалоговое окно
                val dialog =
                    AlertDialog.Builder(requireContext()).setTitle("Разрешение не получено")
                        .setMessage("Для загрузки файлов необходимо дать разрешение на чтение внешнего хранилища.")
                        .setPositiveButton("ОК") { _, _ -> requireActivity() }.create()
                dialog.show()
            }
        }


    private var READ_CONTACTS_GRANTED = false


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { fileUri ->
                getFileNameFromUri(fileUri)
//                saveFileToInternalStorage(fileUri, fileName)
                // Обработка выбранного файла
                // Здесь вы можете выполнить действия с выбранным файлом, например, загрузить его или открыть
                // fileUri - URI выбранного файла
                // ...
            }
        }
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderAddBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onResume() {
        super.onResume()

        if (isReadyForTransition) {
            launchReminderListFragment()
            isReadyForTransition = false // Сбрасываем флаг после перехода
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        viewModel = ViewModelProvider(this@ReminderAddFragment)[ReminderViewModel::class.java]
        super.onViewCreated(view, savedInstanceState)


        // Проверяем, есть ли у нас уже разрешение ACCESS_NOTIFICATION_POLICY
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Разрешение не предоставлено, запрашиваем его у пользователя

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
                ACCESS_NOTIFICATION_POLICY_REQUEST_CODE
            )
        } else {
            // Разрешение уже предоставлено, выполняем необходимые действия
            // ...
        }

        // Проверяем, есть ли у нас уже разрешение FOREGROUND_SERVICE
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.FOREGROUND_SERVICE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Разрешение не предоставлено, запрашиваем его у пользователя

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.FOREGROUND_SERVICE),
                FOREGROUND_SERVICE_REQUEST_CODE
            )
        } else {
            // Разрешение уже предоставлено, выполняем необходимые действия
            // ...

        }
        addMenu()

        if (remindDate != "") {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val remindLocalDate = LocalDate.parse(remindDate, formatter)
            getFormatDate(remindLocalDate)
        } else {
            getFormatDate(LocalDate.now())
        }
        tvTimeTwo.text = currentTime()
        tvTime.text = dateTimeManager.getTime(LocalTime.now())
        viewModel.tempLastReminderRecords()
        viewModel.reminderItem.observe(viewLifecycleOwner) {
            if (it != null) {
                reminderItem = it
                if (this@ReminderAddFragment::reminderItem.isInitialized && reminderItem.temporary) {
                    observeMenuTimeOut()
                    observeMenuCount()
                    observeMenuFrequency()
                    val hours = getString(R.string.hours)
                    val min = getString(R.string.min)
                    val menu1 = getString(R.string.dropdown_menu_timeout)
                    var timeFormatting1: String
                    reminderMenuTimeOut = it.menuTimeout
                    reminderMenuFrequency = it.menuRepeatFrequency
                    reminderMenuCount = it.menuRepeatCount
                    if (it.menuTimeout != 0) {
                        if ((it.menuTimeout / 60) > 1) {
                            timeFormatting1 = "${it.menuTimeout / 60} $hours"
                            if ((it.menuTimeout % 60) != 0) {
                                timeFormatting1 += "${it.menuTimeout % 60} $min"
                            }
                        } else {
                            timeFormatting1 = "${it.menuTimeout} $min"
                        }
                    } else {
                        timeFormatting1 = "${it.menuTimeout} $min"
                    }
                    tvMenu1.text = "$menu1: $timeFormatting1"
                    activity?.resources?.getColor(R.color.color_primary_text)
                        ?.let { it1 -> tvMenu1.setTextColor(it1) }
                    val menu2 = getString(R.string.dropdown_repeat_frequency)
                    var timeFormatting2: String = ""
                    if (it.menuRepeatFrequency != 0) {
                        if ((it.menuRepeatFrequency / 60) > 1) {
                            timeFormatting2 = "${it.menuRepeatFrequency / 60} $hours"
                            if ((it.menuRepeatFrequency % 60) != 0) {
                                timeFormatting2 += "${it.menuRepeatFrequency % 60} $min"
                            }
                        } else {
                            timeFormatting2 = "${it.menuRepeatFrequency} $min"
                        }
                    } else {
                        timeFormatting2 = "${it.menuRepeatFrequency} $min"

                    }
                    tvMenu2.text = "$menu2: $timeFormatting2"
                    activity?.resources?.getColor(R.color.color_primary_text)
                        ?.let { it1 -> tvMenu2.setTextColor(it1) }
                    val menu3 = getString(R.string.dropdown_repeat_count)
                    tvMenu3.text = "$menu3: ${it.menuRepeatCount}"
                    activity?.resources?.getColor(R.color.color_primary_text)
                        ?.let { it1 -> tvMenu3.setTextColor(it1) }
                    tvTimeTwo.text = it.timeRemaining
                    etReminder.text = it.body.toEditable()
                    val localTime = LocalTime.parse(it.timeRemaining)
                    tvTime.text = dateTimeManager.getTime(localTime)
                    val localDate = LocalDate.parse(it.dateRemaining)
                    getFormatDate(localDate)
                }
            }
        }

        initTouchAnimation()

        clEmail3.visibility = View.GONE
        clEmail4.visibility = View.GONE
        clEmail5.visibility = View.GONE

        clSms3.visibility = View.GONE
        clSms4.visibility = View.GONE
        clSms5.visibility = View.GONE

        viewModelSelectContactList()
        viewModelSmsList()
        viewModelEmailList()
        viewModelPhoneNumberList()
        viewModelFileItem()
        viewModelMelodyItem()
        viewModelImageItem()

        ivCalendar.setOnClickListener {
            animatorSetDate.start()
            ivCalendar.animate()
                .scaleX(1.2f) // Увеличиваем масштаб по оси X
                .scaleY(1.2f) // Увеличиваем масштаб по оси Y
                .setDuration(200) // Устанавливаем длительность анимации (в миллисекундах)
                .withEndAction {
                    // По окончании анимации можно добавить дополнительные действия
                    ivCalendar.animate()
                        .scaleX(1.0f) // Возвращаем масштаб по оси X к исходному
                        .scaleY(1.0f) // Возвращаем масштаб по оси Y к исходному
                        .setDuration(200) // Устанавливаем длительность анимации (в миллисекундах)
                        .start()
                }
                .start()

            datePictureDialogPopUp()
        }
        ivTime.setOnClickListener {
            animatorSetTime.start()
            ivTime.animate()
                .scaleX(1.2f) // Увеличиваем масштаб по оси X
                .scaleY(1.2f) // Увеличиваем масштаб по оси Y
                .setDuration(200) // Устанавливаем длительность анимации (в миллисекундах)
                .withEndAction {
                    // По окончании анимации можно добавить дополнительные действия
                    ivTime.animate()
                        .scaleX(1.0f) // Возвращаем масштаб по оси X к исходному
                        .scaleY(1.0f) // Возвращаем масштаб по оси Y к исходному
                        .setDuration(200) // Устанавливаем длительность анимации (в миллисекундах)
                        .start()
                }
                .start()

            timePictureDialogPopUp()
        }

        cvAddEmail.setOnClickListener {
            animatorSetEmail.start()
            temporarySaveReminder()
            launchAddEmailReminderFragment()
        }

        cvAddPhone.setOnClickListener {
            animatorSetPhone.start()
            temporarySaveReminder()
            launchAddPhoneNumberReminderFragment()
        }

        cvAddSms.setOnClickListener {
            animatorSetSms.start()
            temporarySaveReminder()
            launchAddSmsReminderFragment()
        }

        cvAddContact.setOnClickListener {
            animatorSetContact.start()
            temporarySaveReminder()
            val hasReadContactPermission = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_CONTACTS
            )
            // если устройство до API 23, устанавливаем разрешение
            if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
                READ_CONTACTS_GRANTED = true
                launchContactsFragment()
            } else {
                // вызываем диалоговое окно для установки разрешений
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS
                    ), REQUEST_CODE_READ_CONTACTS
                )
            }
        }

        clAddImage.setOnClickListener {
            animatorSetImage.start()
            temporarySaveReminder()
            checkPermissionsAndOpenImagePicker()
        }
        llAddFile.setOnClickListener {
            animatorSetFile.start()
            temporarySaveReminder()
            checkPermissionsAndOpenFilePicker()

        }
        clSoundBlock.setOnClickListener {
            animatorSetMelody.start()
            temporarySaveReminder()
            checkPermissionsAndOpenMelodyPicker()
        }
        tvSaveButton.setOnClickListener {
            animatorSetSave.start()
            saveReminderFragment()
        }
        ivCancel.setOnClickListener {
            animatorSetIvCancel.start()
            deleteNullAttributes()
            launchReminderListFragment()
        }
        ivSave.setOnClickListener {
            animatorSetIvSave.start()
            saveReminderFragment()
        }
    }


    private fun initTouchAnimation() = with(binding) {

        val startColor = ContextCompat.getColor(requireContext(), R.color.background_click)
        val endColor = ContextCompat.getColor(requireContext(), R.color.background_start_click)

        animatorSetDate = ObjectAnimator.ofInt(ivCalendar, "backgroundColor", startColor, endColor)
        animatorSetDate.setEvaluator(ArgbEvaluator())
        animatorSetDate.duration = DEFAULT_DURATION

        animatorSetTime = ObjectAnimator.ofInt(ivTime, "backgroundColor", startColor, endColor)
        animatorSetTime.setEvaluator(ArgbEvaluator())
        animatorSetTime.duration = DEFAULT_DURATION

        val bgColorAnimatorImage = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorImage = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        bgColorAnimatorImage.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clAddImageWrapper.setBackgroundColor(color)
        }
        textColorAnimatorImage.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvAddImage.setTextColor(color)
        }
        animatorSetImage = AnimatorSet()
        animatorSetImage.playTogether(bgColorAnimatorImage, textColorAnimatorImage)
        animatorSetImage.duration = DEFAULT_DURATION // 500мс

        val bgColorAnimatorMelody = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorMelody = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        bgColorAnimatorMelody.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            clSoundBlockWrapper.setBackgroundColor(color)
        }
        textColorAnimatorMelody.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvMelody.setTextColor(color)
        }
        animatorSetMelody = AnimatorSet()
        animatorSetMelody.playTogether(bgColorAnimatorMelody, textColorAnimatorMelody)
        animatorSetMelody.duration = DEFAULT_DURATION // 500мс

        val bgColorAnimatorFile = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorFile = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        bgColorAnimatorFile.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            laAddFile.setBackgroundColor(color)
        }
        textColorAnimatorFile.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvAddFile.setTextColor(color)
        }
        animatorSetFile = AnimatorSet()
        animatorSetFile.playTogether(bgColorAnimatorFile, textColorAnimatorFile)
        animatorSetFile.duration = DEFAULT_DURATION // 500мс

        val bgColorAnimatorContact = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorContact = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        bgColorAnimatorContact.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            cvAddContact.setBackgroundColor(color)
        }
        textColorAnimatorContact.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvContact.setTextColor(color)
        }
        animatorSetContact = AnimatorSet()
        animatorSetContact.playTogether(bgColorAnimatorContact, textColorAnimatorContact)
        animatorSetContact.duration = DEFAULT_DURATION // 500мс

        val bgColorAnimatorPhone = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorPhone = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        bgColorAnimatorPhone.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            cvAddPhone.setBackgroundColor(color)
        }
        textColorAnimatorPhone.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvPhone.setTextColor(color)
        }
        animatorSetPhone = AnimatorSet()
        animatorSetPhone.playTogether(bgColorAnimatorPhone, textColorAnimatorPhone)
        animatorSetPhone.duration = DEFAULT_DURATION // 500мс

        val bgColorAnimatorEmail = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorEmail = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        bgColorAnimatorEmail.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            cvAddEmail.setBackgroundColor(color)
        }
        textColorAnimatorEmail.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvEmail.setTextColor(color)
        }
        animatorSetEmail = AnimatorSet()
        animatorSetEmail.playTogether(bgColorAnimatorEmail, textColorAnimatorEmail)
        animatorSetEmail.duration = DEFAULT_DURATION // 500мс

        val bgColorAnimatorSms = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorSms = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        bgColorAnimatorSms.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            cvAddSms.setBackgroundColor(color)
        }
        textColorAnimatorSms.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvSms.setTextColor(color)
        }
        animatorSetSms = AnimatorSet()
        animatorSetSms.playTogether(bgColorAnimatorSms, textColorAnimatorSms)
        animatorSetSms.duration = DEFAULT_DURATION // 500м


        val bgColorAnimatorSave = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        val textColorAnimatorSave = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.text_click),
            ContextCompat.getColor(requireContext(), R.color.text_start_click)
        )
        bgColorAnimatorSave.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvSaveButton.setBackgroundColor(color)
        }
        textColorAnimatorSave.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            tvSaveButton.setTextColor(color)
        }

        animatorSetSave = AnimatorSet()
        animatorSetSave.playTogether(bgColorAnimatorSave, textColorAnimatorSave)
        animatorSetSave.duration = DEFAULT_DURATION // 500мс

        val bgCoanimatorSetIvSave = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        bgCoanimatorSetIvSave.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            ivSave.setBackgroundColor(color)
        }
        animatorSetIvSave = AnimatorSet()
        animatorSetIvSave.playTogether(bgCoanimatorSetIvSave)
        animatorSetIvSave.duration = DEFAULT_DURATION // 500мс

        val bgColorAnimatorIvCancel = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(requireContext(), R.color.background_click),
            ContextCompat.getColor(requireContext(), R.color.background_start_click)
        )
        animatorSetIvCancel = AnimatorSet()
        animatorSetIvCancel.playTogether(bgColorAnimatorIvCancel)
        animatorSetIvCancel.duration = DEFAULT_DURATION // 500мс
        bgColorAnimatorIvCancel.addUpdateListener { animation ->
            val color = animation.animatedValue as Int
            ivCancel.setBackgroundColor(color)
        }
        animatorSetIvCancel = AnimatorSet()
        animatorSetIvCancel.playTogether(bgColorAnimatorIvCancel)
        animatorSetIvCancel.duration = DEFAULT_DURATION // 500мс
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openGallery() {
        getContent.launch("image/*")
    }

    private fun openMelody() {
        getContentMelody.launch("audio/*")
    }

    private fun saveFileToInternalStorage(inputStream: InputStream, fileName: String): Boolean {
        deleteAddingFileByRemId(UNDEFINED_ID)
        var success = false
        try {
            val outputStream = FileOutputStream(
                File(
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName
                )
            )
            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()
            viewModel.addFileItem(
                UNDEFINED_ID,
                fileName,
                requireContext().filesDir.absolutePath,
                UNDEFINED_ID
            )
            Toast.makeText(requireContext(), "File saved successfully", Toast.LENGTH_SHORT).show()
            success = true


        } catch (e: Exception) {
        }
        return success
    }

    private fun saveMelodyToInternalStorage(inputStream: InputStream, fileName: String): Boolean {
        deleteAddingMelodyByRemId(UNDEFINED_ID)
        var success = false
        try {
            val outputStream = FileOutputStream(
                File(
                    requireContext().getExternalFilesDir("Music"), fileName
                )
            )
            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()
            viewModel.addMelodyItem(
                UNDEFINED_ID,
                fileName,
                requireContext().filesDir.absolutePath,
                UNDEFINED_ID
            )
            Toast.makeText(requireContext(), "Melody saved successfully", Toast.LENGTH_SHORT).show()
            success = true


        } catch (e: Exception) {
            Log.e("AddReminder", "Error saving file to internal storage: ${e.message}")
        }
        return success
    }


    private fun validateAudioFile(fileName: String): Boolean {
        val validExtensions =
            arrayOf(".mp3", ".wav", ".ogg", ".aac", ".flac", ".wma") // Допустимые расширения файлов
        val fileExtension = getFileExtension(fileName)
        return validExtensions.contains(fileExtension)
    }

    private fun validateAudioFileSize(file: File, maxSizeInBytes: Long): Boolean {
        val fileSize = file.length()
        return fileSize <= maxSizeInBytes
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

    private fun checkPermissionsAndOpenFilePicker() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted, open file picker
            getContentFile.launch("*/*")
        } else {
            // Request permission
            requestPermissionLauncherTwo.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
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

        return imageWidth >= MIN_IMAGE_WIDTH && imageHeight >= MIN_IMAGE_HEIGHT
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

    private fun saveImage(uri: Uri) = with(binding) {
        deleteAddingImageByRemId(UNDEFINED_ID)
        // Check if the image is valid
        val selectedImageBitmap = getBitmapFromUri(uri)
        val isValidImage = isValidImage(uri)
        val fileName =
            "${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))}.jpg"
        if (isValidImage) {
            // Save the image to external storage
            saveImageToExternalStorage(selectedImageBitmap!!, fileName)
            // Save the image to the database
            viewModel.addImageItem(
                imageId = ImageItem.UNDEFINED_ID,
                imageName = fileName,
                imagePath = "path",
                remId = ImageItem.UNDEFINED_ID
            )
            val file = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName
            )
            val bitmap = BitmapFactory.decodeFile(file.path)
            ivAddImage.setImageBitmap(bitmap)
            Toast.makeText(requireContext(), "Image saved successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Image to small", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteAddingImageByRemId(id: Int) {
        viewModel.getImageWithRemainderId(id).observe(viewLifecycleOwner) {
            imageList = it
        }
        if (imageList.isNotEmpty()) {
            imageList.forEach {
                val fileName = it.imageName
                viewModel.deleteImageFromInternalStorage(
                    requireContext(), fileName
                )
            }
        }

        viewModel.deleteImageFromRemainderId(id)
    }

    private fun deleteAddingFileByRemId(id: Int) {
        viewModel.getFileWithRemainderId(id).observe(viewLifecycleOwner) {
            fileList = it
        }
        if (fileList.isNotEmpty()) {
            fileList.forEach {
                val fileName = it.name
                viewModel.deleteFileFromInternalStorage(
                    requireContext(), fileName
                )
            }
        }

        viewModel.deleteFileFromRemainderId(id)
    }

    private fun deleteAddingMelodyByRemId(id: Int) {
        viewModel.getMelodyWithRemainderId(id).observe(viewLifecycleOwner) {
            melodyList = it
        }
        if (melodyList.isNotEmpty()) {
            melodyList.forEach {
                val melodyName = it.melodyName
                viewModel.deleteMelodyFromInternalStorage(
                    requireContext(), melodyName
                )
            }
        }

        viewModel.deleteMelodyFromRemainderId(id)
    }

    private fun deleteNullAttributes() = with(viewModel) {
        deleteEmailFromRemainderId(0)
        deleteSaveContactFromRemainderId(0)
        deleteSmsFromRemainderId(0)
        deletePhoneNumberFromRemainderId(0)
        deleteAddingImageByRemId(0)
        deleteAddingFileByRemId(0)
        deleteAddingMelodyByRemId(0)
    }

    private fun bindToReminder() = with(viewModel) {
        viewModel.tempLastReminderRecords()
        viewModel.reminderItem.observe(viewLifecycleOwner) {
            if (it != null) {
                bindCurrentEmailsToLastReminder()
                bindCurrentSaveContactToLastReminder()
                bindCurrentSmsToLastReminder()
                bindCurrentPhoneNumberToLastReminder()
                bindCurrentImageToLastReminder()
                bindCurrentFileToLastReminder()
                bindCurrentMelodyToLastReminder()

            }
        }

    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun addMenu() = with(binding) {
        val popupMenuStyle = R.style.ReminderAddPopupMenuStyle
        // Применяем свой стиль к PopupMenu
        val popupMenuContext = ContextThemeWrapper(context, popupMenuStyle)
        val popupMenu1 = PopupMenu(popupMenuContext, clMenu1)
        popupMenu1.menuInflater.inflate(R.menu.menu_timeout, popupMenu1.menu)
        popupMenu1.menu.setGroupDividerEnabled(true)

        // Установка гравитации
        popupMenu1.gravity = Gravity.START
        // Установка ширины


        val popupMenu2 = PopupMenu(popupMenuContext, clMenu2)
        popupMenu2.menuInflater.inflate(R.menu.menu_repeat_frequency, popupMenu2.menu)
        popupMenu2.menu.setGroupDividerEnabled(true)
        val popupMenu3 = PopupMenu(popupMenuContext, clMenu3)
        popupMenu3.menuInflater.inflate(R.menu.menu_repeat_count, popupMenu3.menu)
        popupMenu3.menu.setGroupDividerEnabled(true)

        popupMenu1.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_0_min_b -> {
                    viewModel.reminderMenuTimeOut(0)
                    observeMenuTimeOut()
                }

                R.id.menu_15_min_b -> {
                    viewModel.reminderMenuTimeOut(15)
                    observeMenuTimeOut()
                }

                R.id.menu_30_min_b -> {
                    viewModel.reminderMenuTimeOut(30)
                    observeMenuTimeOut()
                }

                R.id.menu_45_min_b -> {
                    viewModel.reminderMenuTimeOut(45)
                    observeMenuTimeOut()
                }

                R.id.menu_1_hour_b -> {
                    viewModel.reminderMenuTimeOut(60)
                    observeMenuTimeOut()
                }
            }
            false
        }
        popupMenu2.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_0_min -> {
                    viewModel.reminderMenuFrequency(0)
                    observeMenuFrequency()
                }

                R.id.menu_15_min -> {
                    viewModel.reminderMenuFrequency(15)
                    observeMenuFrequency()
                }

                R.id.menu_hour -> {
                    viewModel.reminderMenuFrequency(60)
                    observeMenuFrequency()
                }

                R.id.menu_4_hour -> {
                    viewModel.reminderMenuFrequency(240)
                    observeMenuFrequency()
                }

                R.id.menu_every_day -> {
                    viewModel.reminderMenuFrequency(1440)
                    observeMenuFrequency()
                }
            }
            false
        }
        popupMenu3.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_1_count -> {
                    viewModel.reminderMenuCount(1)
                    observeMenuCount()
                }

                R.id.menu_2_count -> {
                    viewModel.reminderMenuCount(2)
                    observeMenuCount()
                }

                R.id.menu_3_count -> {
                    viewModel.reminderMenuCount(3)
                    observeMenuCount()
                }

                R.id.menu_4_count -> {
                    viewModel.reminderMenuCount(4)
                    observeMenuCount()
                }

                R.id.menu_5_count -> {
                    viewModel.reminderMenuCount(5)
                    observeMenuCount()
                }

                R.id.menu_6_count -> {
                    viewModel.reminderMenuCount(6)
                    observeMenuCount()
                }

            }
            false
        }
        clMenu1.setOnClickListener {
            popupMenu1.show()
        }
        clMenu2.setOnClickListener {
            popupMenu2.show()
        }
        clMenu3.setOnClickListener {
            popupMenu3.show()
        }
    }

    private fun getTextBodyFromAdds() = with((binding)) {
        viewModel.getSaveContactWithRemainderId(0).observe(viewLifecycleOwner) {
            saveContactList = it
            if (saveContactList.isNotEmpty()) {
                var count = 0
                if (addPhoneNumber || addEmail || addSms) {
                    reminderText += ". "
                    reminderText += if (saveContactList.size > 1) {
                        getString(R.string.call_contacts)
                    } else {
                        getString(R.string.call_contact)
                    }
                } else {
                    reminderText = if (saveContactList.size > 1) {
                        getString(R.string.call_contacts)
                    } else {
                        getString(R.string.call_contact)
                    }
                }
                repeat(saveContactList.size) {
                    if (count == 0) {
                        reminderText += " ${saveContactList[0].name}"
                    }
                    if (count == 1) {
                        reminderText += ", ${saveContactList[1].name}"
                    }
                    if (count == 2) {
                        reminderText += ", ${saveContactList[2].name}"
                    }
                    if (count == 3) {
                        reminderText += ", ${saveContactList[3].name}"
                    }
                    if (count == 4) {
                        reminderText += ", ${saveContactList[4].name}"
                    }
                    count++
                }
                this.etReminder.text =
                    reminderText.trimStart('.').trimStart(',').trimStart().toEditable()
                observeReminderValidateError()
            } else { observeReminderValidateError() }
        }

        viewModel.getPhoneNumberWithRemainderId(0).observe(viewLifecycleOwner) {
            phoneNumberList = it
            if (phoneNumberList.isNotEmpty()) {
                var count = 0
                if (addSms || addContacts || addEmail) {
                    reminderText += ". "
                    reminderText += if (phoneNumberList.size > 1) {
                        getString(R.string.call_numbers)
                    } else {
                        getString(R.string.call_number)
                    }
                } else {
                    reminderText = if (phoneNumberList.size > 1) {
                        getString(R.string.call_numbers)
                    } else {
                        getString(R.string.call_number)
                    }
                }
                repeat(phoneNumberList.size) {
                    if (count == 0) {
                        reminderText += " ${phoneNumberList[0].number}"
                    }
                    if (count == 1) {
                        reminderText += ", ${phoneNumberList[1].number}"
                    }
                    if (count == 2) {
                        reminderText += ", ${phoneNumberList[2].number}"
                    }
                    if (count == 3) {
                        reminderText += ", ${phoneNumberList[3].number}"
                    }
                    if (count == 4) {
                        reminderText += ", ${phoneNumberList[4].number}"
                    }
                    count++
                }
                etReminder.text = reminderText.trimStart('.').trimStart(',').trimStart().toEditable()
                observeReminderValidateError()

            }
        }
        viewModel.getSmsWithRemainderId(0).observe(viewLifecycleOwner) {
            smsList = it
            if (smsList.isNotEmpty()) {
                var count = 0
                if (addPhoneNumber || addContacts || addEmail) {
                    reminderText += ". "
                    reminderText += if (smsList.size > 1) {
                        getString(R.string.send_messages)
                    } else {
                        getString(R.string.send_message)
                    }
                } else {
                    reminderText = if (smsList.size > 1) {
                        getString(R.string.send_messages)
                    } else {
                        getString(R.string.send_message)
                    }
                }
                repeat(smsList.size) {
                    if (count == 0) {
                        if ((smsList[0].name).isNullOrBlank()) {
                            reminderText += " ${smsList[0].smsPhone}"
                        } else {
                            reminderText += " ${smsList[0].name}"
                        }
                    }
                    if (count == 1) {
                        if ((smsList[1].name).isNullOrBlank()) {
                            reminderText += ", ${smsList[1].smsPhone}"
                        } else {
                            reminderText += ", ${smsList[1].name}"
                        }
                    }
                    if (count == 2) {
                        if ((smsList[2].name).isNullOrBlank()) {
                            reminderText += ", ${smsList[2].smsPhone}"
                        } else {
                            reminderText += ", ${smsList[2].name}"
                        }
                    }
                    if (count == 3) {
                        if ((smsList[3].name).isNullOrBlank()) {
                            reminderText += ", ${smsList[3].smsPhone}"
                        } else {
                            reminderText += ", ${smsList[3].name}"
                        }
                    }
                    if (count == 4) {
                        if ((smsList[4].name).isNullOrBlank()) {
                            reminderText += ", ${smsList[4].smsPhone}"
                        } else {
                            reminderText += ", ${smsList[4].name}"
                        }
                    }
                    count++
                }
                etReminder.text =
                    reminderText.trimStart('.').trimStart(',').trimStart().toEditable()
                observeReminderValidateError()

            }
        }
        viewModel.getEmailWithRemainderId(0).observe(viewLifecycleOwner) {
            emailList = it
            if (emailList.isNotEmpty()) {
                var count = 0
                if (addPhoneNumber || addContacts || addSms) {
                    reminderText += ". "
                    reminderText += if (emailList.size > 1) {
                        getString(R.string.send_emails)
                    } else {
                        getString(R.string.send_email)
                    }
                } else {
                    reminderText = if (emailList.size > 1) {
                        getString(R.string.send_emails)
                    } else {
                        getString(R.string.send_email)
                    }
                }
                repeat(emailList.size) {
                    if (count == 0) {
                        if ((emailList[0].name).isNullOrBlank()) {
                            reminderText += " ${emailList[0].emailAddress}"
                        } else {
                            reminderText += " ${emailList[0].name}"
                        }
                    }
                    if (count == 1) {
                        if ((emailList[1].name).isNullOrBlank()) {
                            reminderText += ", ${emailList[1].emailAddress}"
                        } else {
                            reminderText += ", ${emailList[1].name}"
                        }
                    }
                    if (count == 2) {
                        if ((emailList[2].name).isNullOrBlank()) {
                            reminderText += ", ${emailList[2].emailAddress}"
                        } else {
                            reminderText += ", ${emailList[2].name}"
                        }
                    }
                    if (count == 3) {
                        if ((emailList[3].name).isNullOrBlank()) {
                            reminderText += ", ${emailList[3].emailAddress}"
                        } else {
                            reminderText += ", ${emailList[3].name}"
                        }
                    }
                    if (count == 4) {
                        if ((emailList[4].name).isNullOrBlank()) {
                            reminderText += ", ${emailList[4].emailAddress}"
                        } else {
                            reminderText += ", ${emailList[4].name}"
                        }
                    }
                    count++
                }
                etReminder.text =
                    reminderText.trimStart('.').trimStart(',').trimStart().toEditable()
                observeReminderValidateError()

            }
        }

//        Log.d("AddReminderFragment", "getTextBodyFromAdds()")
    }

    private fun getFormatDate(date: LocalDate) = with(binding) {
        val formatterTwo = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDateTwo = date.format(formatterTwo)
        tvCalendar.text = dateTimeManager.getDate(date)
        tvCalendarTwo.text = formattedDateTwo
    }

    private fun temporarySaveReminder() = with(binding) {
        viewModel.tempLastReminderRecords()
        viewModel.reminderItem.observe(viewLifecycleOwner) {
            if (it != null) {
                reminderItem = it
                if (this@ReminderAddFragment::reminderItem.isInitialized && reminderItem.temporary) {
//                    Log.d("AddReminderFragment", "tempEditReminder(1)")
                    editReminder(true)
                } else {
                    if (count == 0) {
//                        Log.d("AddReminderFragment", "tempAddReminder(1)")
                        addReminder(true)
                        count++
                    }
                }
            } else {
                if (count == 0) {
//                    Log.d("AddReminderFragment", "tempAddReminder(2)")
                    addReminder(true)
                    count++
                }
            }
        }
    }

    private fun addReminder(temporary: Boolean) = with(binding) {
        viewModel.addReminderItemWithTimeDate(
            inputBody = (this.etReminder.text.toString()),
            inputTimeRemaining = tvTimeTwo.text.toString(),
            inputDateRemaining = tvCalendarTwo.text.toString(),
            addContacts = addContacts,
            addEmails = addEmail,
            addPhoneNumbers = addPhoneNumber,
            addSms = addSms,
            addImage = addImage,
            addFile = addFile,
            addMelody = addMelody,
            inputState = true,
            reminderMenuFrequency = reminderMenuFrequency,
            reminderMenuRepeatCount = reminderMenuCount,
            reminderMenuTimeOut = reminderMenuTimeOut,
            temporary = temporary
        )
    }

    private fun editReminder(temporary: Boolean) = with(binding) {
        viewModel.editReminderItem(
            inputBody = this.etReminder.text.toString(),
            inputTimeRemaining = tvTimeTwo.text.toString(),
            inputDateRemaining = tvCalendarTwo.text.toString(),
            addContacts = addContacts,
            addEmails = addEmail,
            addPhoneNumbers = addPhoneNumber,
            addSms = addSms,
            addImage = addImage,
            addFile = addFile,
            addMelody = addMelody,
            inputState = true,
            reminderMenuFrequency = reminderMenuFrequency,
            reminderMenuRepeatCount = reminderMenuCount,
            reminderMenuTimeOut = reminderMenuTimeOut,
            temporary = temporary,
            showInWidget = true

        )
    }

    private fun observeMenuTimeOut() = with(binding) {
        viewModel.reminderMenuTimeOut.observe(viewLifecycleOwner) {
            reminderMenuTimeOut = it
            val hours = getString(R.string.hours)
            val min = getString(R.string.min)
            var timeFormatting: String = ""
            val name = getString(R.string.dropdown_menu_timeout)
            if (reminderMenuTimeOut != 0) {
                if ((reminderMenuTimeOut / 60) > 1) {
                    timeFormatting = "${reminderMenuTimeOut / 60} $hours"
                    if ((reminderMenuTimeOut % 60) != 0) {
                        timeFormatting += "${reminderMenuTimeOut % 60} $min"
                    }
                } else {
                    timeFormatting = "${reminderMenuTimeOut} $min"
                }
            } else {
                timeFormatting = "${reminderMenuTimeOut} $min"
            }
            tvMenu1.text = "$name: $timeFormatting"
            ivMenu1.visibility = View.INVISIBLE
            activity?.resources?.getColor(R.color.color_primary_text)
                ?.let { it1 -> tvMenu1.setTextColor(it1) }
        }
    }

    private fun observeMenuFrequency() = with(binding) {
        viewModel.reminderMenuFrequency.observe(viewLifecycleOwner) {
            reminderMenuFrequency = it
            val hours = getString(R.string.hours)
            val min = getString(R.string.min)
            var timeFormatting: String
            val name = getString(R.string.dropdown_repeat_frequency)
            if (reminderMenuFrequency != 0) {
                if ((reminderMenuFrequency / 60) > 1) {
                    timeFormatting = "${reminderMenuFrequency / 60} $hours"
                    if ((reminderMenuFrequency % 60) != 0) {
                        timeFormatting += "${reminderMenuFrequency % 60} $min"
                    }
                } else {
                    timeFormatting = "${reminderMenuFrequency} $min"
                }
            } else {
                timeFormatting = "${reminderMenuFrequency} $min"

            }
            tvMenu2.text = "$name: $timeFormatting"
            ivMenu2.visibility = View.INVISIBLE
            activity?.resources?.getColor(R.color.color_primary_text)
                ?.let { it1 -> tvMenu2.setTextColor(it1) }
        }
    }

    private fun observeMenuCount() = with(binding) {
        viewModel.reminderMenuCount.observe(viewLifecycleOwner) {
            reminderMenuCount = it
            val name = getString(R.string.dropdown_repeat_count)
            tvMenu3.text = "$name: $reminderMenuCount"
            ivMenu3.visibility = View.INVISIBLE
            activity?.resources?.getColor(R.color.color_primary_text)
                ?.let { it1 -> tvMenu3.setTextColor(it1) }
        }
    }

    private fun getBitmap(avatar: String): Bitmap {
        return if (avatar != "null") {
            val cr = requireContext().contentResolver
            val source = ImageDecoder.createSource(cr, parse(avatar))
            ImageDecoder.decodeBitmap(source)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.no_image_icon)
        }
    }

    private fun viewModelSelectContactList() = with(binding) {
        viewModel.getSaveContactWithRemainderId(0).observe(viewLifecycleOwner) {
            saveContactList = it
            if (saveContactList.isNotEmpty()) {
                addContacts = true
                var count = 0
                repeat(it.size) {
                    if (count == 0) {
                        clContactTwo.visibility = View.GONE
                        clContactThree.visibility = View.GONE
                        clContactFour.visibility = View.GONE
                        clContactFive.visibility = View.GONE
                        clContactSix.visibility = View.GONE
                        ivContactOne.setImageBitmap(getBitmap(saveContactList[0].avatar))
                        tvContactOne.text = saveContactList[0].name
                        clContactOne.visibility = View.VISIBLE
                        ivContactOne.visibility = View.VISIBLE
                        ivDeleteContactOne.setOnClickListener {
                            viewModel.deleteSaveContactReminder(saveContactList[0])
//                            clContactOne.visibility = View.GONE
                        }
                    }
                    if (count == 1) {
                        clContactThree.visibility = View.GONE
                        clContactFour.visibility = View.GONE
                        clContactFive.visibility = View.GONE
                        clContactSix.visibility = View.GONE
                        ivContactTwo.setImageBitmap(getBitmap(saveContactList[1].avatar))
                        tvContactTwo.text = saveContactList[1].name
                        clContactTwo.visibility = View.VISIBLE
                        ivDeleteContactTwo.setOnClickListener {
                            viewModel.deleteSaveContactReminder(saveContactList[1])
//                            clContactTwo.visibility = View.GONE
                        }
                    }
                    if (count == 2) {
                        clContactFour.visibility = View.GONE
                        clContactFive.visibility = View.GONE
                        clContactSix.visibility = View.GONE
                        ivContactThree.setImageBitmap(getBitmap(saveContactList[2].avatar))
                        tvContactThree.text = saveContactList[2].name
                        clContactThree.visibility = View.VISIBLE
                        ivDeleteContactThree.setOnClickListener {
                            viewModel.deleteSaveContactReminder(saveContactList[2])
//                            clContactThree.visibility = View.GONE
                        }
                    }
                    if (count == 3) {
                        clContactFive.visibility = View.GONE
                        clContactSix.visibility = View.GONE
                        ivContactFour.setImageBitmap(getBitmap(saveContactList[3].avatar))
                        tvContactFour.text = saveContactList[3].name
                        clContactFour.visibility = View.VISIBLE
                        ivDeleteContactFour.setOnClickListener {
                            viewModel.deleteSaveContactReminder(saveContactList[3])
                            clContactFour.visibility = View.GONE
                        }
                    }
                    if (count == 4) {
                        clContactSix.visibility = View.GONE
                        ivContactFive.setImageBitmap(getBitmap(saveContactList[4].avatar))
                        tvContactFive.text = saveContactList[4].name
                        clContactFive.visibility = View.VISIBLE
                        ivDeleteContactFive.setOnClickListener {
                            viewModel.deleteSaveContactReminder(saveContactList[4])
//                            clContactFive.visibility = View.GONE
                        }
                    }
                    if (count == 5) {
                        ivContactSix.setImageBitmap(getBitmap(saveContactList[5].avatar))
                        tvContactSix.text = saveContactList[5].name
                        clContactSix.visibility = View.VISIBLE
                        ivDeleteContactSix.setOnClickListener {
                            viewModel.deleteSaveContactReminder(saveContactList[5])
//                            clContactSix.visibility = View.GONE
                        }
                    }
                    count++
                }
            } else {
                clContactOne.visibility = View.GONE
                clContactTwo.visibility = View.GONE
                clContactThree.visibility = View.GONE
                clContactFour.visibility = View.GONE
                clContactFive.visibility = View.GONE
                clContactSix.visibility = View.GONE
            }
        }
    }

    private fun viewModelSmsList() = with(binding) {
        viewModel.getSmsWithRemainderId(0).observe(viewLifecycleOwner) {
            smsList = it
            if (smsList.isNotEmpty()) {
                addSms = true
                var count = 0
                repeat(it.size) {
                    if (count == 0) {
                        clSms3.visibility = View.GONE
                        clSms4.visibility = View.GONE
                        clSms5.visibility = View.GONE
                        tvSms1.text = smsList[0].smsMessage
                        tvSms2.text = null
                        ivDeleteSms2.visibility = View.GONE

                        tvSms1.visibility = View.VISIBLE
                        ivDeleteSms1.visibility = View.VISIBLE
                        ivDeleteSms1.setOnClickListener {
                            viewModel.deleteSmsReminder(smsList[0])
                        }
                    }
                    if (count == 1) {
                        clSms3.visibility = View.GONE
                        clSms4.visibility = View.GONE
                        clSms5.visibility = View.GONE
                        tvSms2.text = smsList[1].smsMessage
                        tvSms2.visibility = View.VISIBLE
                        ivDeleteSms2.visibility = View.VISIBLE
                        ivDeleteSms2.setOnClickListener {
                            viewModel.deleteSmsReminder(smsList[1])
                        }
                    }
                    if (count == 2) {
                        clSms4.visibility = View.GONE
                        clSms5.visibility = View.GONE

                        tvSms3.text = smsList[2].smsMessage
                        clSms3.visibility = View.VISIBLE
                        tvSms3.visibility = View.VISIBLE
                        ivDeleteSms3.visibility = View.VISIBLE
                        ivDeleteSms3.setOnClickListener {
                            viewModel.deleteSmsReminder(smsList[2])
                        }
                    }
                    if (count == 3) {
                        clSms5.visibility = View.GONE
                        tvSms4.text = smsList[3].smsMessage
                        clSms4.visibility = View.VISIBLE
                        tvSms4.visibility = View.VISIBLE
                        ivDeleteSms4.visibility = View.VISIBLE
                        ivDeleteSms4.setOnClickListener {
                            viewModel.deleteSmsReminder(smsList[3])
                        }
                    }
                    if (count == 4) {
                        tvSms5.text = smsList[4].smsMessage
                        clSms5.visibility = View.VISIBLE
                        tvSms5.visibility = View.VISIBLE
                        ivDeleteSms5.visibility = View.VISIBLE
                        ivDeleteSms5.setOnClickListener {
                            viewModel.deleteSmsReminder(smsList[4])
                        }
                    }
                    count++
                }
            } else {
                tvSms1.text = null
                tvSms1.visibility = View.VISIBLE
                ivDeleteSms1.visibility = View.GONE
            }
        }
    }

    private fun viewModelPhoneNumberList() = with(binding) {
        viewModel.getPhoneNumberWithRemainderId(0).observe(viewLifecycleOwner) {
            phoneNumberList = it
            if (phoneNumberList.isNotEmpty()) {
                addPhoneNumber = true
                var count = 0
                repeat(it.size) {
                    if (count == 0) {
                        tvPhone2.text = null
                        ivDeletePhone2.visibility = View.GONE
                        tvPhone1.text = phoneNumberList[0].number
                        tvPhone1.visibility = View.VISIBLE
                        ivDeletePhone1.visibility = View.VISIBLE
                        ivDeletePhone1.setOnClickListener {
                            viewModel.deletePhoneNumberReminder(phoneNumberList[0])
                        }
                    }
                    if (count == 1) {
                        tvPhone2.text = phoneNumberList[1].number
                        tvPhone2.visibility = View.VISIBLE
                        ivDeletePhone2.visibility = View.VISIBLE
                        ivDeletePhone2.setOnClickListener {
                            viewModel.deletePhoneNumberReminder(phoneNumberList[1])
                        }
                    }
                    if (count == 2) {
                        tvPhone3.text = phoneNumberList[2].number
                        clPhone3.visibility = View.VISIBLE
                        ivDeletePhone3.visibility = View.VISIBLE
                        ivDeletePhone3.setOnClickListener {
                            viewModel.deletePhoneNumberReminder(phoneNumberList[2])
                        }
                    }
                    if (count == 3) {
                        tvPhone4.text = phoneNumberList[3].number
                        clPhone4.visibility = View.VISIBLE
                        ivDeletePhone4.visibility = View.VISIBLE
                        ivDeletePhone4.setOnClickListener {
                            viewModel.deletePhoneNumberReminder(phoneNumberList[3])
                        }
                    }
                    if (count == 4) {
                        tvPhone5.text = phoneNumberList[4].number
                        clPhone5.visibility = View.VISIBLE
                        ivDeletePhone5.visibility = View.VISIBLE
                        ivDeletePhone5.setOnClickListener {
                            viewModel.deletePhoneNumberReminder(phoneNumberList[4])
                        }
                    }
                    count++
                }
            } else {
                tvPhone1.text = null
                tvPhone1.visibility = View.VISIBLE
                ivDeletePhone1.visibility = View.GONE
                clPhone3.visibility = View.GONE
                clPhone4.visibility = View.GONE
                clPhone5.visibility = View.GONE
            }
        }
    }

    private fun viewModelEmailList() = with(binding) {
        viewModel.getEmailWithRemainderId(0).observe(viewLifecycleOwner) {
            emailList = it
            if (emailList.isNotEmpty()) {
                addEmail = true
                var count = 0
                repeat(it.size) {
                    if (count == 0) {
                        clEmail3.visibility = View.GONE
                        clEmail4.visibility = View.GONE
                        clEmail5.visibility = View.GONE
                        ivDeleteEmail2.visibility = View.GONE
                        tvEmail1.text = emailList[0].emailAddress
                        tvEmail1.visibility = View.VISIBLE
                        tvEmail2.text = null
                        clEmail1.visibility = View.VISIBLE
                        ivDeleteEmail1.visibility = View.VISIBLE
                        ivDeleteEmail1.setOnClickListener {
                            viewModel.deleteEmailReminder(emailList[0])

                        }
                    }
                    if (count == 1) {
                        clEmail3.visibility = View.GONE
                        clEmail4.visibility = View.GONE
                        clEmail5.visibility = View.GONE
                        tvEmail2.text = emailList[1].emailAddress
                        tvEmail2.visibility = View.VISIBLE
                        clEmail2.visibility = View.VISIBLE
                        ivDeleteEmail2.visibility = View.VISIBLE
                        ivDeleteEmail2.setOnClickListener {
                            viewModel.deleteEmailReminder(emailList[1])
                        }
                    }
                    if (count == 2) {
                        clEmail4.visibility = View.GONE
                        clEmail5.visibility = View.GONE
                        tvEmail3.text = emailList[2].emailAddress
                        clEmail3.visibility = View.VISIBLE
                        tvEmail3.visibility = View.VISIBLE
                        ivDeleteEmail3.visibility = View.VISIBLE
                        ivDeleteEmail3.setOnClickListener {
                            viewModel.deleteEmailReminder(emailList[2])
                        }
                    }
                    if (count == 3) {
                        clEmail5.visibility = View.GONE
                        tvEmail4.text = emailList[3].emailAddress
                        clEmail4.visibility = View.VISIBLE
                        tvEmail4.visibility = View.VISIBLE
                        ivDeleteEmail4.visibility = View.VISIBLE
                        ivDeleteEmail4.setOnClickListener {
                            viewModel.deleteEmailReminder(emailList[3])
                        }
                    }
                    if (count == 4) {
                        tvEmail5.text = emailList[4].emailAddress
                        clEmail5.visibility = View.VISIBLE
                        tvEmail5.visibility = View.VISIBLE
                        ivDeleteEmail5.visibility = View.VISIBLE
                        ivDeleteEmail5.setOnClickListener {
                            viewModel.deleteEmailReminder(emailList[4])
                        }
                    }
                    count++
                }
            } else {
                tvEmail1.text = null
                tvEmail1.visibility = View.VISIBLE
                ivDeleteEmail1.visibility = View.GONE
            }
        }
    }

    private fun viewModelImageItem() = with(binding) {
        viewModel.getImageWithRemainderId(0).observe(viewLifecycleOwner) {
            imageList = it
            if (imageList.isNotEmpty()) {
                addImage = true
                val file = File(
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    imageList[0].imageName
                )
                val bitmap = BitmapFactory.decodeFile(file.path)
                ivAddImage.setImageBitmap(bitmap)
                tvAddImage.text = ""
                tvAddImage.visibility = View.GONE
                val newWidth = resources.getDimensionPixelSize(R.dimen.advanced_image_block_width)
                val newHeight = resources.getDimensionPixelSize(R.dimen.advanced_image_block_height)
                val layoutParams = ivAddImage.layoutParams
                layoutParams.width = newWidth
                layoutParams.height = newHeight
                ivAddImage.layoutParams = layoutParams

            } else {

                ivAddImage.setImageResource(R.drawable.addimage)
            }
        }
    }

    private fun viewModelFileItem() = with(binding) {
        viewModel.getFileWithRemainderId(0).observe(viewLifecycleOwner) {
            fileList = it
            if (fileList.isNotEmpty()) {
                addFile = true
                ivAddFile.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.file_added
                    )
                )
                tvAddFile.text = (fileList[0].name).substring(0, 11) + "..."
            } else {
//                ivAddFile.setImageResource(R.drawable.addimage)
            }
        }
    }

    private fun viewModelMelodyItem() = with(binding) {
        viewModel.getMelodyWithRemainderId(0).observe(viewLifecycleOwner) {
            melodyList = it
            if (melodyList.isNotEmpty()) {
                addMelody = true
                ivMelody.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.melody
                    )
                )
                tvMelody.text = (melodyList[0].melodyName).substring(0, 5) + "..."
            } else {
//                ivAddFile.setImageResource(R.drawable.addimage)
            }
        }
    }

    private fun updateListWidget() {
        val widgetManager = AppWidgetManager.getInstance(requireContext())
        val widgetIds = widgetManager.getAppWidgetIds(
            ComponentName(
                requireContext(), ReminderListWidget::class.java
            )
        )

        val updateIntent = Intent(requireContext(), ReminderListWidget::class.java)
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        val pendingUpdate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                requireContext(), 0, updateIntent, PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                requireContext(), 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        widgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.lvWidget)
        pendingUpdate.send()
    }

    private fun updateCalendarTwoWidget() {
        val widgetManager = AppWidgetManager.getInstance(requireContext())
        val widgetIds = widgetManager.getAppWidgetIds(
            ComponentName(
                requireContext(), ReminderCalendarTwoWidget::class.java
            )
        )

        val updateIntent = Intent(requireContext(), ReminderCalendarTwoWidget::class.java)
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        val pendingUpdate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                requireContext(), 0, updateIntent, PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                requireContext(), 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        widgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.calendar_grid_view)
        pendingUpdate.send()
    }

    private fun updateCalendarWidget() {
        val widgetManager = AppWidgetManager.getInstance(requireContext())
        val widgetIds = widgetManager.getAppWidgetIds(
            ComponentName(
                requireContext(), ReminderCalendarWidget::class.java
            )
        )

        val updateIntent = Intent(requireContext(), ReminderCalendarWidget::class.java)
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        val pendingUpdate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                requireContext(), 0, updateIntent, PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                requireContext(), 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        widgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.calendar_grid_view)
        pendingUpdate.send()
    }

    private fun saveReminderFragment() = with(binding) {
        viewModel.saveLastReminderRecords()
        viewModel.reminderItemTwo.observe(viewLifecycleOwner) { it ->
            if (it != null) {
                reminderItemTwo = it
                if (this@ReminderAddFragment::reminderItemTwo.isInitialized && reminderItemTwo.temporary) {
                    if (viewModel.validateTextBody(this.etReminder.text.toString())) {
                        editReminder(false)
                        bindToReminder()
                        updateListWidget()
                        updateCalendarWidget()
                        updateCalendarTwoWidget()
                        launchReminderListFragment()
                    } else {
                        getTextBodyFromAdds()
                    }
                } else {
                    if (viewModel.validateTextBody(this.etReminder.text.toString())) {
                        if (saveCount == 0) {
                            addReminder(false)
                            saveCount++
                            bindToReminder()
                            updateListWidget()
                            updateCalendarWidget()
                            updateCalendarTwoWidget()
                            launchReminderListFragment()
                        }
                    } else {
                        getTextBodyFromAdds()
                    }
                }
            } else {
                if (viewModel.validateTextBody(this.etReminder.text.toString())) {
                    if (saveCount == 0) {
                        addReminder(false)
                        saveCount++
                        bindToReminder()
                        updateListWidget()
                        updateCalendarWidget()
                        updateCalendarTwoWidget()
                        launchReminderListFragment()
                    }
                } else {
                    getTextBodyFromAdds()
                }
            }
        }
    }

    private fun observeReminderValidateError() = with(binding) {
        viewModel.errorInputMenuTimeout.observe(viewLifecycleOwner) {
            val menuRepeatTimeout = if (it) {
                getString(R.string.error_input_message)
            } else {
                null
            }
            tvMenu1.error = menuRepeatTimeout
        }
        viewModel.errorInputMenuRepeatFrequency.observe(viewLifecycleOwner) {
            val menuRepeatFrequency = if (it) {
                getString(R.string.error_input_phone_number)
            } else {
                null
            }
            tvMenu2.error = menuRepeatFrequency
        }
        viewModel.errorInputMenuRepeatCount.observe(viewLifecycleOwner) {
            val menuRepeatCount = if (it) {
                getString(R.string.error_input_phone_number)
            } else {
                null
            }
            tvMenu3.error = menuRepeatCount
        }
        val message = if (viewModel.validateTextBody(this.etReminder.text.toString())) {
            null
        } else {
             getString(R.string.error_input_message)
        }
        tilReminder.error = message

        viewModel.errorInputDateRemaining.observe(viewLifecycleOwner) {
            val data = if (it) {
                getString(R.string.error_input_phone_number)
            } else {
                null
            }
            tvCalendarTwo.error = data
        }
        viewModel.errorInputTimeRemaining.observe(viewLifecycleOwner) {
            val time = if (it) {
                getString(R.string.error_input_phone_number)
            } else {
                null
            }
            tvTimeTwo.error = time
        }
    }

    private fun datePictureDialogPopUp() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Создание экземпляра DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, yearOfYear, monthOfYear, dayOfMonth ->
                // Обработка выбора даты
                val date = Calendar.getInstance().apply {
                    set(Calendar.YEAR, yearOfYear)
                    set(Calendar.MONTH, monthOfYear)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }.time
                var dateFormat = "dd.MM.yyyy"
                dateFormat = if (!prefs.formatDate.isNullOrEmpty()) {
                    prefs.formatDate
                } else {
                    "dd MM yyyy"
                }
                val dFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
                val formattedDate = dFormat.format(date)
                binding.tvCalendar.text = formattedDate
                val dFormatTwo = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDateTwo = dFormatTwo.format(date)
                dateItem = formattedDateTwo
                binding.tvCalendarTwo.text = formattedDateTwo

                if (this@ReminderAddFragment::dateItem.isInitialized)
                    viewModel.saveDateItem(dateItem)

            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun currentTime(): String? {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return LocalTime.now().format(formatter)
    }


    private fun timePictureDialogPopUp() {

        // on below line we are getting
        // the instance of our calendar.
        val c = Calendar.getInstance()
        // on below line we are getting
        // our day, month and year.
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val time24true = prefs.hourFormat != 1
        // on below line we are creating a
        // variable for date picker dialog.
        val timePickerDialog =
            TimePickerDialog(requireContext(), { _, hourOfDay, minuteOfHour ->
                c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                c.set(Calendar.MINUTE, minuteOfHour)
                timeItem = SimpleDateFormat("HH:mm").format(c.time)
//                @SuppressLint("SuspiciousIndentation")
                if (this@ReminderAddFragment::timeItem.isInitialized) {
                    viewModel.saveTimeItem(
                        timeItem
                    )
                    binding.tvTime.text = dateTimeManager.getTime(LocalTime.parse(timeItem))
                    binding.tvTimeTwo.text = timeItem
                }
            }, hour, minute, time24true)
        timePickerDialog.show()
    }


    private fun parseParams() {
        val args = requireArguments()
        if (args.containsKey(SELECTED_DATE)) {
            remindDate = args.getString(SELECTED_DATE, "")
        }
    }

    companion object {

        const val NAME = "ReminderAddFragment"
        const val PERMISSIONS_REQUEST_CODE = 1234
        const val REQUEST_CODE_READ_EXTERNAL_STORAGE = 100
        const val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 101
        const val REQUEST_CODE_IMAGE_PICKER = 102
        const val REQUEST_SHOW_FULL_SCREEN = 103
        const val REQUEST_IMAGE = 104
        const val ACCESS_NOTIFICATION_POLICY_REQUEST_CODE = 105
        const val FOREGROUND_SERVICE_REQUEST_CODE = 106
        const val MIN_IMAGE_WIDTH = 500
        const val MIN_IMAGE_HEIGHT = 300
        private const val DATE_TIME = "2023-08-11T13:00"
        const val UNDEFINED_ID = 0
        private const val SELECTED_DATE = "selected_date"
        private const val PICK_FILE_REQUEST_CODE = 100
        private const val REQUEST_CODE_READ_CONTACTS = 1
        const val EMAIL_ADDRESS_PATTERN =
            "^(\\w+[\\.|-]?\\w*@[a-zA-Z]{2,32}[\\.]?[a-zA-Z]{0,32}[\\.]?[a-zA-Z]{0,32})$"
        const val PHONE_NUMBER_PATTERN = "^[\\+\\d\\#\\*]{3,20}\$"
        const val REMINDER_TIME_PATTERN = "^(\\d{2}:\\d{2}(:\\d{2})?)\$"
        const val REMINDER_DATE_PATTERN = "^(\\d{4}-\\d{2}-\\d{2})$"
        const val DEFAULT_DURATION = 500.toLong()


        fun newInstance(): ReminderAddFragment {
            return ReminderAddFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }

        fun newInstance(date: String): ReminderAddFragment {
            return ReminderAddFragment().apply {
                arguments = Bundle().apply {
                    putString(SELECTED_DATE, date)

                }
            }
        }
    }

    private fun launchAddSmsReminderFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, AddSmsReminderFragment.newInstance(reminderItemId))
            .addToBackStack(null).commit()
    }

    private fun launchAddPhoneNumberReminderFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(
                R.id.main_container,
                AddPhoneNumberReminderFragment.newInstance(reminderItemId)
            )
            .addToBackStack(null).commit()
    }

    private fun launchAddEmailReminderFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, AddEmailReminderFragment.newInstance(reminderItemId))
            .addToBackStack(null).commit()
    }

    private fun launchContactsFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ContactsFragment.newInstance())
            .addToBackStack(null).commit()
    }

    private fun launchReminderListFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderListFragment.newInstance())
            .addToBackStack(ReminderListFragment.NAME).commit()
    }


}

