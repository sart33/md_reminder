package solutions.mobiledev.reminder.presentation.notification

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import solutions.mobiledev.reminder.BuildConfig
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentReminderItemNotificationBinding
import solutions.mobiledev.reminder.domain.entity.EmailItem
import solutions.mobiledev.reminder.domain.entity.FileItem
import solutions.mobiledev.reminder.domain.entity.ImageItem
import solutions.mobiledev.reminder.domain.entity.PhoneNumberItem
import solutions.mobiledev.reminder.domain.entity.ReminderItem
import solutions.mobiledev.reminder.domain.entity.SaveContactItem
import solutions.mobiledev.reminder.domain.entity.SmsItem
import solutions.mobiledev.reminder.presentation.BaseFragment
import solutions.mobiledev.reminder.presentation.notification.ReminderItemNotificationFragment.Companion.UNDEFINED_ID
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderEditFragment
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderListFragment
import java.io.File
import java.time.LocalDate
import java.time.LocalTime


private lateinit var viewModel: ReminderViewModel
private lateinit var saveContactList: List<SaveContactItem>
private lateinit var reminderItem: ReminderItem
private lateinit var smsList: List<SmsItem>
private lateinit var emailList: List<EmailItem>
private lateinit var phoneNumberList: List<PhoneNumberItem>
private lateinit var imageList: List<ImageItem>
private lateinit var fileList: List<FileItem>
private var addPhoneNumber: Boolean = false
private var reminderItemId: Int = UNDEFINED_ID
private var addContacts: Boolean = false
private var addSms: Boolean = false
private var addEmail: Boolean = false

private lateinit var animatorSetContactOne: ObjectAnimator
private lateinit var animatorSetContactTwo: ObjectAnimator
private lateinit var animatorSetContactThree: ObjectAnimator
private lateinit var animatorSetContactFour: ObjectAnimator
private lateinit var animatorSetContactFive: ObjectAnimator
private lateinit var animatorSetContactSix: ObjectAnimator

private lateinit var animatorSetMessageOne: ObjectAnimator
private lateinit var animatorSetMessageTwo: ObjectAnimator
private lateinit var animatorSetMessageThree: ObjectAnimator
private lateinit var animatorSetMessageFour: ObjectAnimator
private lateinit var animatorSetMessageFive: ObjectAnimator
private lateinit var animatorSetMessageSix: ObjectAnimator

private lateinit var animatorSetMailOne: ObjectAnimator
private lateinit var animatorSetMailTwo: ObjectAnimator
private lateinit var animatorSetMailThree: ObjectAnimator
private lateinit var animatorSetMailFour: ObjectAnimator
private lateinit var animatorSetMailFive: ObjectAnimator
private lateinit var animatorSetMailSix: ObjectAnimator

private lateinit var animatorSetPhoneOne: ObjectAnimator
private lateinit var animatorSetPhoneTwo: ObjectAnimator
private lateinit var animatorSetPhoneThree: ObjectAnimator
private lateinit var animatorSetPhoneFour: ObjectAnimator
private lateinit var animatorSetPhoneFive: ObjectAnimator
private lateinit var animatorSetPhoneSix: ObjectAnimator

private lateinit var animatorSetStop: ObjectAnimator
private lateinit var animatorSetEdit: ObjectAnimator
private lateinit var animatorSetSnooze: ObjectAnimator
private lateinit var animatorSetTrash: ObjectAnimator
private lateinit var animatorSetCheck: ObjectAnimator

private lateinit var animatorSetFile: ObjectAnimator


class ReminderItemNotificationFragment : BaseFragment<FragmentReminderItemNotificationBinding>() {

    private var _binding: FragmentReminderItemNotificationBinding? = null
    private val binding: FragmentReminderItemNotificationBinding
        get() = _binding ?: throw RuntimeException("FragmentReminderItemNotificationBinding == null")

//    val scope = CoroutineScope(context= CoroutineContext())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentReminderItemNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        viewModel =
            ViewModelProvider(this@ReminderItemNotificationFragment)[ReminderViewModel::class.java]
        super.onViewCreated(view, savedInstanceState)
        screenTouchAnimation()
        if (reminderItemId != UNDEFINED_ID) {
            viewModel.getReminderViewItem(reminderItemId)
            viewModel.reminderItem.observe(viewLifecycleOwner) {
                if (it != null) {
                    reminderItem = it
                    val localTime = LocalTime.parse(it.timeRemaining)
                    tvReminderTime.text = dateTimeManager.getTime(localTime)
                    val localDate = LocalDate.parse(it.dateRemaining)
                    tvReminderDate.text = dateTimeManager.getDate(localDate)
                    tvReminderMessage.text = it.body
                    if (it.mails || it.contacts || it.phoneNumbers || it.sms) {
                        clActionBar.visibility = View.VISIBLE
                    } else {
                        clActionBar.visibility = View.INVISIBLE
                    }
                }
            }
            viewModelSelectContactList()
            viewModelSmsList()
            viewModelEmailList()
            viewModelPhoneNumberList()
            viewModelFileItem()
            viewModelImageItem()
        }
        tvActionTitle.text = requireContext().getString(R.string.actions)
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ivTrash.setOnClickListener {
            animatorSetTrash.start()
            viewModel.deleteReminderAll(reminderItem)
            notificationManager.deleteNotificationChannel(CHANNEL_ID + "_" + reminderItem.id)
            launchReminderListFragment()
        }
        ivStop.setOnClickListener {
            animatorSetStop.start()
            viewModel.activeChangeStatusRemind(reminderItem.id, STATUS_INACTIVE)
            notificationManager.cancel(reminderItem.id)
            notificationManager.deleteNotificationChannel(CHANNEL_ID + "_" + reminderItem.id)
            launchReminderListFragment()

        }
        ivCheck.setOnClickListener {
            animatorSetCheck.start()
            launchReminderListFragment()
        }
        ivEdit.setOnClickListener {
            animatorSetEdit.start()
            launchReminderEditFragment(reminderItem.id)
        }
        ivSnooze.setOnClickListener {
            animatorSetSnooze.start()
            val postponeNotificationFragment = PostponeNotificationFragment()
            val args = Bundle().apply {
                putInt(REMINDER_ITEM_ID, reminderItem.id)
            }
            postponeNotificationFragment.arguments = args
            val manager = requireActivity().supportFragmentManager
            postponeNotificationFragment.show(manager, "PostponeDialog")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun screenTouchAnimation() = with(binding) {

        val startColor = ContextCompat.getColor(requireContext(), R.color.notification_background_click)
        val endColor = ContextCompat.getColor(requireContext(), R.color.notification_background_start_click)

        animatorSetContactOne = ObjectAnimator.ofInt(clContactOne, "backgroundColor", startColor, endColor)
        animatorSetContactOne.setEvaluator(ArgbEvaluator())
        animatorSetContactOne.duration = 500 // 500мс

        animatorSetContactTwo = ObjectAnimator.ofInt(clContactTwo, "backgroundColor", startColor, endColor)
        animatorSetContactTwo.setEvaluator(ArgbEvaluator())
        animatorSetContactTwo.duration = 500 // 500мс

        animatorSetContactThree = ObjectAnimator.ofInt(clContactThree, "backgroundColor", startColor, endColor)
        animatorSetContactThree.setEvaluator(ArgbEvaluator())
        animatorSetContactThree.duration = 500 // 500мс

        animatorSetContactFour = ObjectAnimator.ofInt(clContactFour, "backgroundColor", startColor, endColor)
        animatorSetContactFour.setEvaluator(ArgbEvaluator())
        animatorSetContactFour.duration = 500 // 500мс

        animatorSetContactFive = ObjectAnimator.ofInt(clContactFive, "backgroundColor", startColor, endColor)
        animatorSetContactFive.setEvaluator(ArgbEvaluator())
        animatorSetContactFive.duration = 500 // 500мс

        animatorSetContactSix = ObjectAnimator.ofInt(clContactSix, "backgroundColor", startColor, endColor)
        animatorSetContactSix.setEvaluator(ArgbEvaluator())
        animatorSetContactSix.duration = 500 // 500мс


        animatorSetMessageOne = ObjectAnimator.ofInt(clMessageOne, "backgroundColor", startColor, endColor)
        animatorSetMessageOne.setEvaluator(ArgbEvaluator())
        animatorSetMessageOne.duration = 500 // 500мс

        animatorSetMessageTwo = ObjectAnimator.ofInt(clMessageTwo, "backgroundColor", startColor, endColor)
        animatorSetMessageTwo.setEvaluator(ArgbEvaluator())
        animatorSetMessageTwo.duration = 500 // 500мс

        animatorSetMessageThree = ObjectAnimator.ofInt(clMessageThree, "backgroundColor", startColor, endColor)
        animatorSetMessageThree.setEvaluator(ArgbEvaluator())
        animatorSetMessageThree.duration = 500 // 500мс

        animatorSetMessageFour = ObjectAnimator.ofInt(clMessageFour, "backgroundColor", startColor, endColor)
        animatorSetMessageFour.setEvaluator(ArgbEvaluator())
        animatorSetMessageFour.duration = 500 // 500мс

        animatorSetMessageFive = ObjectAnimator.ofInt(clMessageFive, "backgroundColor", startColor, endColor)
        animatorSetMessageFive.setEvaluator(ArgbEvaluator())
        animatorSetMessageFive.duration = 500 // 500мс

        animatorSetMessageSix = ObjectAnimator.ofInt(clMessageSix, "backgroundColor", startColor, endColor)
        animatorSetMessageSix.setEvaluator(ArgbEvaluator())
        animatorSetMessageSix.duration = 500 // 500мс


        animatorSetMailOne = ObjectAnimator.ofInt(clMailOne, "backgroundColor", startColor, endColor)
        animatorSetMailOne.setEvaluator(ArgbEvaluator())
        animatorSetMailOne.duration = 500 // 500мс

        animatorSetMailTwo = ObjectAnimator.ofInt(clMailTwo, "backgroundColor", startColor, endColor)
        animatorSetMailTwo.setEvaluator(ArgbEvaluator())
        animatorSetMailTwo.duration = 500 // 500мс

        animatorSetMailThree = ObjectAnimator.ofInt(clMailThree, "backgroundColor", startColor, endColor)
        animatorSetMailThree.setEvaluator(ArgbEvaluator())
        animatorSetMailThree.duration = 500 // 500мс

        animatorSetMailFour = ObjectAnimator.ofInt(clMailFour, "backgroundColor", startColor, endColor)
        animatorSetMailFour.setEvaluator(ArgbEvaluator())
        animatorSetMailFour.duration = 500 // 500мс

        animatorSetMailFive = ObjectAnimator.ofInt(clMailFive, "backgroundColor", startColor, endColor)
        animatorSetMailFive.setEvaluator(ArgbEvaluator())
        animatorSetMailFive.duration = 500 // 500мс

        animatorSetMailSix = ObjectAnimator.ofInt(clMailSix, "backgroundColor", startColor, endColor)
        animatorSetMailSix.setEvaluator(ArgbEvaluator())
        animatorSetMailSix.duration = 500 // 500мс


        animatorSetPhoneOne = ObjectAnimator.ofInt(clPhoneOne, "backgroundColor", startColor, endColor)
        animatorSetPhoneOne.setEvaluator(ArgbEvaluator())
        animatorSetPhoneOne.duration = 500 // 500мс

        animatorSetPhoneTwo = ObjectAnimator.ofInt(clPhoneTwo, "backgroundColor", startColor, endColor)
        animatorSetPhoneTwo.setEvaluator(ArgbEvaluator())
        animatorSetPhoneTwo.duration = 500 // 500мс

        animatorSetPhoneThree = ObjectAnimator.ofInt(clPhoneThree, "backgroundColor", startColor, endColor)
        animatorSetPhoneThree.setEvaluator(ArgbEvaluator())
        animatorSetPhoneThree.duration = 500 // 500мс

        animatorSetPhoneFour = ObjectAnimator.ofInt(clPhoneFour, "backgroundColor", startColor, endColor)
        animatorSetPhoneFour.setEvaluator(ArgbEvaluator())
        animatorSetPhoneFour.duration = 500 // 500мс

        animatorSetPhoneFive = ObjectAnimator.ofInt(clPhoneFive, "backgroundColor", startColor, endColor)
        animatorSetPhoneFive.setEvaluator(ArgbEvaluator())
        animatorSetPhoneFive.duration = 500 // 500мс

        animatorSetPhoneSix = ObjectAnimator.ofInt(clPhoneSix, "backgroundColor", startColor, endColor)
        animatorSetPhoneSix.setEvaluator(ArgbEvaluator())
        animatorSetPhoneSix.duration = 500 // 500мс

        
        animatorSetStop = ObjectAnimator.ofInt(ivStop, "backgroundColor", startColor, endColor)
        animatorSetStop.setEvaluator(ArgbEvaluator())
        animatorSetStop.duration = 500 // 500мс

        animatorSetEdit = ObjectAnimator.ofInt(ivEdit, "backgroundColor", startColor, endColor)
        animatorSetEdit.setEvaluator(ArgbEvaluator())
        animatorSetEdit.duration = 500 // 500мс

        animatorSetSnooze = ObjectAnimator.ofInt(ivSnooze, "backgroundColor", startColor, endColor)
        animatorSetSnooze.setEvaluator(ArgbEvaluator())
        animatorSetSnooze.duration = 500 // 500мс

        animatorSetTrash = ObjectAnimator.ofInt(ivTrash, "backgroundColor", startColor, endColor)
        animatorSetTrash.setEvaluator(ArgbEvaluator())
        animatorSetTrash.duration = 500 // 500мс

        animatorSetCheck = ObjectAnimator.ofInt(ivCheck, "backgroundColor", startColor, endColor)
        animatorSetCheck.setEvaluator(ArgbEvaluator())
        animatorSetCheck.duration = 500 // 500мс

        animatorSetFile = ObjectAnimator.ofInt(llAddFile, "backgroundColor", startColor, endColor)
        animatorSetFile.setEvaluator(ArgbEvaluator())
        animatorSetFile.duration = 500 // 500мс
    }

    private fun openFile(name: String) {
        val docFile = File(
            requireContext().getExternalFilesDir("Documents"), name
        )
        val docUri = FileProvider.getUriForFile(
            requireContext(),
            "${BuildConfig.APPLICATION_ID}.provider",
            docFile
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(docUri, "application/*")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
    }

    private fun getBitmap(avatar: String): Bitmap {
        if (!avatar.isNullOrEmpty()) {
            if (avatar == "null") {
                return BitmapFactory.decodeResource(resources, R.drawable.no_image_icon)
            }
            return if (avatar == "phone") {
                BitmapFactory.decodeResource(resources, R.drawable.transparent_ava)
            } else {
                val cr = requireContext().contentResolver
                val source = ImageDecoder.createSource(cr, Uri.parse(avatar))
                ImageDecoder.decodeBitmap(source)
            }

        } else {
            return BitmapFactory.decodeResource(resources, R.drawable.transparent_ava)

        }
    }

    private fun callTo(phoneNumber: String) {
        // Создаем Intent для запуска системного приложения для звонков
        val uri = Uri.parse("tel:$phoneNumber")
        val intent = Intent(Intent.ACTION_DIAL, uri)

        //Запускаем системное приложение для звонков
//        if (intent.resolveActivity(requireActivity().packageManager) != null) {
        startActivity(intent)
//        }
    }


    private fun sendSmsTo(phoneNumber: String, message: String) {
        // Создаем Intent для отправки SMS
        val uri = Uri.parse("smsto:$phoneNumber")
        val intent = Intent(Intent.ACTION_SENDTO, uri)

        // Добавляем текст сообщения
        intent.putExtra("sms_body", message)

        // Запускаем системное приложение для отправки SMS
        startActivity(intent)
    }

    private fun sendEmail(email: String, subject: String, message: String) {
        // Проверяем, есть ли у приложения разрешение на отправку электронной почты
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Если нет, запрашиваем его
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.INTERNET),
                REQUEST_CODE_SEND_EMAIL
            )
            return
        }


        // Создаем Intent для запуска системного приложения для отправки email
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        // Запускаем системное приложение для отправки email
//        if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
        startActivity(emailIntent)
//        }
    }

    // Обработка результата запроса разрешения
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_SEND_EMAIL -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение получено, отправляем email
//                    sendEmail("lydia.yatsuhnenko@gmail.com", "Reminder email", "Hello, World!")
                    Toast.makeText(requireContext(), "Mail sent successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    // Разрешение не получено, обрабатываем этот случай
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun viewModelEmailList() = with(binding) {
        viewModel.getEmailWithRemainderId(reminderItemId).observe(viewLifecycleOwner) {
            emailList = it
            if (emailList.isNotEmpty()) {
                addEmail = true
                var count = 0
                var localCount = 0
                var temp = 0
                repeat(it.size) {
                    if (count == 0) {
                        val listEmails = emailList[0].emailAddress.split(",")
                        val localSize = listEmails.size
                        val listNames = emailList[0].name.split(",")
                        val listMessages = emailList[0].emailMessage.split(",")
                        val listAvatars = emailList[0].avatar.split(",")
                        repeat(localSize) {
                            if (localCount == 0) {
                                clMailTwo.visibility = View.GONE
                                clMailThree.visibility = View.GONE
                                clMailFour.visibility = View.GONE
                                clMailFive.visibility = View.GONE
                                clMailSix.visibility = View.GONE
                                tvEmailTextOne.text = listNames[0]
                                tvEmailAddressOne.text = listEmails[0]
                                ivMailAvaOne.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                clMailOne.visibility = View.VISIBLE
                                clMailOne.setOnClickListener {
                                    animatorSetMailOne.start()
                                    sendEmail(listEmails[0], "Reminder email", listMessages[0])
                                }
                            }
                            if (localCount == 1) {
                                clMailThree.visibility = View.GONE
                                clMailFour.visibility = View.GONE
                                clMailFive.visibility = View.GONE
                                clMailSix.visibility = View.GONE
                                tvEmailTextTwo.text = listNames[1]
                                tvEmailAddressTwo.text = listEmails[1]
                                ivMailAvaTwo.setImageBitmap(getBitmap(listAvatars[1].trimStart()))
                                clMailTwo.visibility = View.VISIBLE
                                clMailTwo.setOnClickListener {
                                    animatorSetMailTwo.start()
                                    sendEmail(listEmails[1], "Reminder email", listMessages[0])
                                }
                            }
                            if (localCount == 2) {
                                clMailFour.visibility = View.GONE
                                clMailFive.visibility = View.GONE
                                clMailSix.visibility = View.GONE
                                tvEmailTextThree.text = listNames[2]
                                tvEmailAddressThree.text = listEmails[2]
                                ivMailAvaThree.setImageBitmap(getBitmap(listAvatars[2].trimStart()))
                                clMailThree.visibility = View.VISIBLE
                                clMailThree.setOnClickListener {
                                    animatorSetMailThree.start()
                                    sendEmail(listEmails[2], "Reminder email", listMessages[0])
                                }
                            }
                            if (localCount == 3) {
                                clMailFive.visibility = View.GONE
                                clMailSix.visibility = View.GONE
                                tvEmailTextFour.text = listNames[3]
                                tvEmailAddressFour.text = listEmails[3]
                                ivMailAvaFour.setImageBitmap(getBitmap(listAvatars[3].trimStart()))
                                clMailFour.visibility = View.VISIBLE
                                clMailFour.setOnClickListener {
                                    animatorSetMailFour.start()
                                    sendEmail(listEmails[3], "Reminder email", listMessages[0])
                                }

                            }
                            if (localCount == 4) {
                                clMailSix.visibility = View.GONE
                                tvEmailTextFive.text = listNames[4]
                                tvEmailAddressFive.text = listEmails[3]
                                ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[4].trimStart()))
                                clMailFive.visibility = View.VISIBLE
                                clMailFive.setOnClickListener {
                                    animatorSetMailFive.start()
                                    sendEmail(listEmails[4], "Reminder email", listMessages[0])
                                }
                            }
                            if (localCount == 5) {
                                tvEmailTextSix.text = listNames[4]
                                tvEmailAddressSix.text = listEmails[5]
                                ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[5].trimStart()))
                                clMailSix.visibility = View.VISIBLE
                                clMailSix.setOnClickListener {
                                    animatorSetMailSix.start()
                                    sendEmail(listEmails[5], "Reminder email", listMessages[0])
                                }
                            }
                            localCount++
                        }
                        temp = localCount
                    }
                    if (count == 1) {
                        val listEmails = emailList[1].emailAddress.split(",")
                        val listNames = emailList[1].name.split(",")
                        val localSize = listEmails.size
                        val listAvatars = emailList[1].avatar.split(",")
                        val listMessages = emailList[1].emailMessage.split(",")
                        repeat(localSize) {
                            if (localCount == 1) {
                                if (localCount == temp) {
                                    clMailThree.visibility = View.GONE
                                    clMailFour.visibility = View.GONE
                                    clMailFive.visibility = View.GONE
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextTwo.text = listNames[0]
                                    tvEmailAddressTwo.text = listEmails[0]
                                    ivMailAvaTwo.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                    clMailTwo.visibility = View.VISIBLE
                                    clMailTwo.setOnClickListener {
                                        animatorSetMailTwo.start()
                                        sendEmail(listEmails[0], "Reminder email", listMessages[0])
                                    }
                                }
                            }
                            if (localCount == 2) {
                                if (localCount == temp) {
                                    clMailFour.visibility = View.GONE
                                    clMailFive.visibility = View.GONE
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextThree.text = listNames[0]
                                    tvEmailAddressThree.text = listEmails[0]
                                    ivMailAvaThree.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                    clMailThree.visibility = View.VISIBLE
                                    clMailThree.setOnClickListener {
                                        animatorSetMailThree.start()
                                        sendEmail(listEmails[0], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == temp + 1 && localSize >= 2) {
                                    clMailFour.visibility = View.GONE
                                    clMailFive.visibility = View.GONE
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextThree.text = listNames[1]
                                    tvEmailAddressThree.text = listEmails[1]
                                    ivMailAvaThree.setImageBitmap(getBitmap(listAvatars[1].trimStart()))
                                    clMailThree.visibility = View.VISIBLE
                                    clMailThree.setOnClickListener {
                                        animatorSetMailThree.start()
                                        sendEmail(listEmails[1], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 2) && localSize >= 3) {
                                    clMailFour.visibility = View.GONE
                                    clMailFive.visibility = View.GONE
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextThree.text = listNames[2]
                                    tvEmailAddressThree.text = listEmails[2]
                                    ivMailAvaThree.setImageBitmap(getBitmap(listAvatars[2].trimStart()))
                                    clMailThree.visibility = View.VISIBLE
                                    clMailThree.setOnClickListener {
                                        animatorSetMailThree.start()
                                        sendEmail(listEmails[2], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 3) && localSize >= 4) {

                                    clMailFour.visibility = View.GONE
                                    clMailFive.visibility = View.GONE
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextThree.text = listNames[3]
                                    tvEmailAddressThree.text = listEmails[3]
                                    ivMailAvaThree.setImageBitmap(getBitmap(listAvatars[3].trimStart()))
                                    clMailThree.visibility = View.VISIBLE
                                    clMailThree.setOnClickListener {
                                        animatorSetMailThree.start()
                                        sendEmail(listEmails[3], "Reminder email", listMessages[0])
                                    }
                                }
                            }
                            if (localCount == 3) {
                                if (localCount == temp) {
                                    clMailFive.visibility = View.GONE
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFour.text = listNames[0]
                                    tvEmailAddressFour.text = listEmails[0]
                                    ivMailAvaFour.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                    clMailFour.visibility = View.VISIBLE
                                    clMailFour.setOnClickListener {
                                        animatorSetMailFour.start()
                                        sendEmail(listEmails[0], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 1) && localSize >= 2) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFour.text = listNames[1]
                                    tvEmailAddressFour.text = listEmails[1]
                                    ivMailAvaFour.setImageBitmap(getBitmap(listAvatars[1].trimStart()))
                                    clMailFour.visibility = View.VISIBLE
                                    clMailFour.setOnClickListener {
                                        animatorSetMailFour.start()
                                        sendEmail(listEmails[1], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 2) && localSize >= 3) {
                                    tvEmailTextFour.text = listNames[2]
                                    tvEmailAddressFour.text = listEmails[2]
                                    ivMailAvaFour.setImageBitmap(getBitmap(listAvatars[2].trimStart()))
                                    clMailFour.visibility = View.VISIBLE
                                    clMailFour.setOnClickListener {
                                        animatorSetMailFour.start()
                                        sendEmail(listEmails[2], "Reminder email", listMessages[0])
                                    }
                                }
                            }
                            if (localCount == 4) {
                                if (localCount == temp) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[0]
                                    tvEmailAddressFive.text = listEmails[0]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[0], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 1) && localSize >= 2) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[1]
                                    tvEmailAddressFive.text = listEmails[1]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[1].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[1], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 2) && localSize >= 3) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[2]
                                    tvEmailAddressFive.text = listEmails[2]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[2].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[2], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 3) && localSize >= 4) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[3]
                                    tvEmailAddressFive.text = listEmails[3]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[3].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[3], "Reminder email", listMessages[0])
                                    }
                                }
                            }
                            if (localCount == 5) {
                                if (localCount == temp) {
                                    tvEmailTextSix.text = listNames[0]
                                    tvEmailAddressSix.text = listEmails[0]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[0], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 1) && localSize >= 2) {
                                    tvEmailTextSix.text = listNames[1]
                                    tvEmailAddressSix.text = listEmails[1]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[1].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[1], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 2) && localSize >= 3) {
                                    tvEmailTextSix.text = listNames[2]
                                    tvEmailAddressSix.text = listEmails[2]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[2].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[2], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 3) && localSize >= 4) {
                                    tvEmailTextSix.text = listNames[3]
                                    tvEmailAddressSix.text = listEmails[3]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[3].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[3], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 4) && localSize >= 5) {
                                    tvEmailTextSix.text = listNames[4]
                                    tvEmailAddressSix.text = listEmails[4]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[4].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[4], "Reminder email", listMessages[0])
                                    }
                                }
                            }
                            localCount++

                        }
                        temp = localCount
                    }
                    if (count == 2) {
                        val listNames = emailList[2].name.split(",")
                        val listEmails = emailList[2].emailAddress.split(",")
                        val localSize = listEmails.size
                        val listAvatars = emailList[2].avatar.split(",")
                        val listMessages = emailList[2].emailMessage.split(",")
                        repeat(localSize) {
                            if (localCount == 1) {
                                if (localCount == temp) {
                                    clMailFour.visibility = View.GONE
                                    clMailFive.visibility = View.GONE
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextThree.text = listNames[0]
                                    tvEmailAddressThree.text = listEmails[0]
                                    ivMailAvaThree.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                    clMailThree.visibility = View.VISIBLE
                                    clMailThree.setOnClickListener {
                                        animatorSetMailThree.start()
                                        sendEmail(listEmails[0], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == temp + 1 && localSize >= 2) {
                                    clMailFour.visibility = View.GONE
                                    clMailFive.visibility = View.GONE
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextThree.text = listNames[1]
                                    tvEmailAddressThree.text = listEmails[1]
                                    ivMailAvaThree.setImageBitmap(getBitmap(listAvatars[1].trimStart()))
                                    clMailThree.visibility = View.VISIBLE
                                    clMailThree.setOnClickListener {
                                        animatorSetMailThree.start()
                                        sendEmail(listEmails[1], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 2) && localSize >= 3) {
                                    clMailFour.visibility = View.GONE
                                    clMailFive.visibility = View.GONE
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextThree.text = listNames[2]
                                    tvEmailAddressThree.text = listEmails[2]
                                    ivMailAvaThree.setImageBitmap(getBitmap(listAvatars[2].trimStart()))
                                    clMailThree.visibility = View.VISIBLE
                                    clMailThree.setOnClickListener {
                                        animatorSetMailThree.start()
                                        sendEmail(listEmails[2], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 3) && localSize >= 4) {

                                    clMailFour.visibility = View.GONE
                                    clMailFive.visibility = View.GONE
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextThree.text = listNames[3]
                                    tvEmailAddressThree.text = listEmails[3]
                                    ivMailAvaThree.setImageBitmap(getBitmap(listAvatars[3].trimStart()))
                                    clMailThree.visibility = View.VISIBLE
                                    clMailThree.setOnClickListener {
                                        animatorSetMailThree.start()
                                        sendEmail(listEmails[3], "Reminder email", listMessages[0])
                                    }
                                }
                            }
                            if (localCount == 2) {
                                if (localCount == temp) {
                                    clMailFive.visibility = View.GONE
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFour.text = listNames[0]
                                    tvEmailAddressFour.text = listEmails[0]
                                    ivMailAvaFour.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                    clMailFour.visibility = View.VISIBLE
                                    clMailFour.setOnClickListener {
                                        animatorSetMailFour.start()
                                        sendEmail(listEmails[0], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 1) && localSize >= 2) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFour.text = listNames[1]
                                    tvEmailAddressFour.text = listEmails[1]
                                    ivMailAvaFour.setImageBitmap(getBitmap(listAvatars[1].trimStart()))
                                    clMailFour.visibility = View.VISIBLE
                                    clMailFour.setOnClickListener {
                                        animatorSetMailFour.start()
                                        sendEmail(listEmails[1], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 2) && localSize >= 3) {
                                    tvEmailTextFour.text = listNames[2]
                                    tvEmailAddressFour.text = listEmails[2]
                                    ivMailAvaFour.setImageBitmap(getBitmap(listAvatars[2].trimStart()))
                                    clMailFour.visibility = View.VISIBLE
                                    clMailFour.setOnClickListener {
                                        animatorSetMailFour.start()
                                        sendEmail(listEmails[2], "Reminder email", listMessages[0])
                                    }
                                }
                            }
                            if (localCount == 3) {
                                if (localCount == temp) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[0]
                                    tvEmailAddressFive.text = listEmails[0]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[0], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 1) && localSize >= 2) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[1]
                                    tvEmailAddressFive.text = listEmails[1]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[1].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[1], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 2) && localSize >= 3) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[2]
                                    tvEmailAddressFive.text = listEmails[2]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[2].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[2], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 3) && localSize >= 4) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[3]
                                    tvEmailAddressFive.text = listEmails[3]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[3].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[3], "Reminder email", listMessages[0])
                                    }
                                }
                            }
                            if (localCount == 4) {
                                if (localCount == temp) {
                                    tvEmailTextSix.text = listNames[0]
                                    tvEmailAddressSix.text = listEmails[0]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[0], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 1) && localSize >= 2) {
                                    tvEmailTextSix.text = listNames[1]
                                    tvEmailAddressSix.text = listEmails[1]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[1].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[1], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 2) && localSize >= 3) {
                                    tvEmailTextSix.text = listNames[2]
                                    tvEmailAddressSix.text = listEmails[2]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[2].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[2], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 3) && localSize >= 4) {
                                    tvEmailTextSix.text = listNames[3]
                                    tvEmailAddressSix.text = listEmails[3]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[3].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[3], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 4) && localSize >= 5) {
                                    tvEmailTextSix.text = listNames[4]
                                    tvEmailAddressSix.text = listEmails[4]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[4].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[4], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 5) && localSize >= 6) {
                                    tvEmailTextSix.text = listNames[5]
                                    tvEmailAddressSix.text = listEmails[5]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[5].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[5], "Reminder email", listMessages[0])
                                    }
                                }
                            }
                            localCount++
                        }
                        temp = localCount
                    }
                    if (count == 3) {
                        val listNames = emailList[3].name.split(",")
                        val listEmails = emailList[3].emailAddress.split(",")
                        val localSize = listEmails.size
                        val listAvatars = emailList[3].avatar.split(",")
                        val listMessages = emailList[3].emailMessage.split(",")
                        repeat(localSize) {
                            if (localCount == 2) {
                                if (localCount == temp) {
                                    clMailFive.visibility = View.GONE
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFour.text = listNames[0]
                                    tvEmailAddressFour.text = listEmails[0]
                                    ivMailAvaFour.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                    clMailFour.visibility = View.VISIBLE
                                    clMailFour.setOnClickListener {
                                        animatorSetMailFour.start()
                                        sendEmail(listEmails[0], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 1) && localSize >= 2) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFour.text = listNames[1]
                                    tvEmailAddressFour.text = listEmails[1]
                                    ivMailAvaFour.setImageBitmap(getBitmap(listAvatars[1].trimStart()))
                                    clMailFour.visibility = View.VISIBLE
                                    clMailFour.setOnClickListener {
                                        animatorSetMailFour.start()
                                        sendEmail(listEmails[1], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 2) && localSize >= 3) {
                                    tvEmailTextFour.text = listNames[2]
                                    tvEmailAddressFour.text = listEmails[2]
                                    ivMailAvaFour.setImageBitmap(getBitmap(listAvatars[2].trimStart()))
                                    clMailFour.visibility = View.VISIBLE
                                    clMailFour.setOnClickListener {
                                        animatorSetMailFour.start()
                                        sendEmail(listEmails[2], "Reminder email", listMessages[0])
                                    }
                                }
                            }
                            if (localCount == 3) {
                                if (localCount == temp) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[0]
                                    tvEmailAddressFive.text = listEmails[0]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[0], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 1) && localSize >= 2) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[1]
                                    tvEmailAddressFive.text = listEmails[1]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[1].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[1], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 2) && localSize >= 3) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[2]
                                    tvEmailAddressFive.text = listEmails[2]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[2].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[2], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 3) && localSize >= 4) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[3]
                                    tvEmailAddressFive.text = listEmails[3]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[3].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[3], "Reminder email", listMessages[0])
                                    }
                                }
                            }
                            if (localCount == 4) {
                                if (localCount == temp) {
                                    tvEmailTextSix.text = listNames[0]
                                    tvEmailAddressSix.text = listEmails[0]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[0], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 1) && localSize >= 2) {
                                    tvEmailTextSix.text = listNames[1]
                                    tvEmailAddressSix.text = listEmails[1]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[1].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[1], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 2) && localSize >= 3) {
                                    tvEmailTextSix.text = listNames[2]
                                    tvEmailAddressSix.text = listEmails[2]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[2].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[2], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 3) && localSize >= 4) {
                                    tvEmailTextSix.text = listNames[3]
                                    tvEmailAddressSix.text = listEmails[3]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[3].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[3], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 4) && localSize >= 5) {
                                    tvEmailTextSix.text = listNames[4]
                                    tvEmailAddressSix.text = listEmails[4]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[4].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[4], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 5) && localSize >= 6) {
                                    tvEmailTextSix.text = listNames[5]
                                    tvEmailAddressSix.text = listEmails[5]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[5].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[5], "Reminder email", listMessages[0])
                                    }
                                }
                            }
                            localCount++

                        }
                        temp = localCount
                    }
                    if (count == 4) {
                        val listNames = emailList[4].name.split(",")
                        val listEmails = emailList[4].emailAddress.split(",")
                        val localSize = listEmails.size
                        val listAvatars = emailList[4].avatar.split(",")
                        val listMessages = emailList[0].emailMessage.split(",")
                        repeat(localSize) {
                            if (localCount == 3) {
                                if (localCount == temp) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[0]
                                    tvEmailAddressFive.text = listEmails[0]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[0], "Reminder email", listMessages[0])

                                    }
                                }
                                if (localCount == (temp + 1) && localSize >= 2) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[1]
                                    tvEmailAddressFive.text = listEmails[1]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[1].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[1], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 2) && localSize >= 3) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[2]
                                    tvEmailAddressFive.text = listEmails[2]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[2].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[2], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 3) && localSize >= 4) {
                                    clMailSix.visibility = View.GONE
                                    tvEmailTextFive.text = listNames[3]
                                    tvEmailAddressFive.text = listEmails[3]
                                    ivMailAvaFive.setImageBitmap(getBitmap(listAvatars[3].trimStart()))
                                    clMailFive.visibility = View.VISIBLE
                                    clMailFive.setOnClickListener {
                                        animatorSetMailFive.start()
                                        sendEmail(listEmails[3], "Reminder email", listMessages[0])
                                    }
                                }
                            }
                            if (localCount == 4) {
                                if (localCount == temp) {
                                    tvEmailTextSix.text = listNames[0]
                                    tvEmailAddressSix.text = listEmails[0]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[0], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 1) && localSize >= 2) {
                                    tvEmailTextSix.text = listNames[1]
                                    tvEmailAddressSix.text = listEmails[1]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[1].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[1], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 2) && localSize >= 3) {
                                    tvEmailTextSix.text = listNames[2]
                                    tvEmailAddressSix.text = listEmails[2]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[2].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[2], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 3) && localSize >= 4) {
                                    tvEmailTextSix.text = listNames[3]
                                    tvEmailAddressSix.text = listEmails[3]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[3].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[3], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 4) && localSize >= 5) {
                                    tvEmailTextSix.text = listNames[4]
                                    tvEmailAddressSix.text = listEmails[4]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[4].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[4], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 5) && localSize >= 6) {
                                    tvEmailTextSix.text = listNames[5]
                                    tvEmailAddressSix.text = listEmails[5]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[5].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[5], "Reminder email", listMessages[0])
                                    }
                                }
                            }
                            localCount++

                        }
                        temp = localCount
                    }
                    if (count == 5) {
                        val listNames = emailList[5].name.split(",")
                        val listEmails = emailList[5].emailAddress.split(",")
                        val localSize = listEmails.size
                        val listAvatars = emailList[5].avatar.split(",")
                        val listMessages = emailList[0].emailMessage.split(",")
                        repeat(localSize) {
                            if (localCount == 4) {
                                if (localCount == temp) {
                                    tvEmailTextSix.text = listNames[0]
                                    tvEmailAddressSix.text = listEmails[0]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[0].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[0], "Reminder email", listMessages[0])
                                    }
                                }
                                if (localCount == (temp + 1) && localSize >= 2) {
                                    tvEmailTextSix.text = listNames[1]
                                    tvEmailAddressSix.text = listEmails[1]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[1].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[1], "Reminder email", listMessages[1])
                                    }
                                }
                                if (localCount == (temp + 2) && localSize >= 3) {
                                    tvEmailTextSix.text = listNames[2]
                                    tvEmailAddressSix.text = listEmails[2]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[2].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[2], "Reminder email", listMessages[2])
                                    }
                                }
                                if (localCount == (temp + 3) && localSize >= 4) {
                                    tvEmailTextSix.text = listNames[3]
                                    tvEmailAddressSix.text = listEmails[3]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[3].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[3], "Reminder email", listMessages[3])
                                    }
                                }
                                if (localCount == (temp + 4) && localSize >= 5) {
                                    tvEmailTextSix.text = listNames[4]
                                    tvEmailAddressSix.text = listEmails[4]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[4].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[4], "Reminder email", listMessages[4])
                                    }
                                }
                                if (localCount == (temp + 5) && localSize >= 6) {
                                    tvEmailTextSix.text = listNames[5]
                                    tvEmailAddressSix.text = listEmails[5]
                                    ivMailAvaSix.setImageBitmap(getBitmap(listAvatars[5].trimStart()))
                                    clMailSix.visibility = View.VISIBLE
                                    clMailSix.setOnClickListener {
                                        animatorSetMailSix.start()
                                        sendEmail(listEmails[5], "Reminder email", listMessages[5])
                                    }
                                }
                            }
                            localCount++

                        }
                        temp = localCount
                    }
                    count++
                }
            } else {
                clMailOne.visibility = View.GONE
                clMailTwo.visibility = View.GONE
                clMailThree.visibility = View.GONE
                clMailFour.visibility = View.GONE
                clMailFive.visibility = View.GONE
                clMailSix.visibility = View.GONE
            }
        }
    }

    private fun viewModelPhoneNumberList() = with(binding) {
        viewModel.getPhoneNumberWithRemainderId(
            reminderItemId
        ).observe(viewLifecycleOwner) {
            phoneNumberList = it
            if (phoneNumberList.isNotEmpty()) {
                addPhoneNumber = true
                var count = 0
                repeat(phoneNumberList.size) {
                    if (count == 0) {
                        clPhoneTwo.visibility = View.GONE
                        clPhoneThree.visibility = View.GONE
                        clPhoneFour.visibility = View.GONE
                        clPhoneFive.visibility = View.GONE
                        clPhoneSix.visibility = View.GONE
                        tvPhoneOne.text = phoneNumberList[0].number
                        clPhoneOne.setOnClickListener {
                            animatorSetPhoneOne.start()
                            callTo(phoneNumberList[0].number)
                        }
                        clPhoneOne.visibility = View.VISIBLE

                    }
                    if (count == 1) {
                        clPhoneThree.visibility = View.GONE
                        clPhoneFour.visibility = View.GONE
                        clPhoneFive.visibility = View.GONE
                        clPhoneSix.visibility = View.GONE
                        tvPhoneTwo.text = phoneNumberList[1].number
                        clPhoneTwo.visibility = View.VISIBLE
                        clPhoneTwo.setOnClickListener {
                            animatorSetPhoneTwo.start()
                            callTo(phoneNumberList[1].number)
                        }
                    }
                    if (count == 2) {
                        clPhoneFour.visibility = View.GONE
                        clPhoneFive.visibility = View.GONE
                        clPhoneSix.visibility = View.GONE
                        tvPhoneThree.text = phoneNumberList[2].number
                        clPhoneThree.visibility = View.VISIBLE
                        clPhoneThree.setOnClickListener {
                            animatorSetPhoneThree.start()
                            callTo(phoneNumberList[2].number)
                        }
                    }
                    if (count == 3) {
                        clPhoneFive.visibility = View.GONE
                        clPhoneSix.visibility = View.GONE
                        tvPhoneFour.text = phoneNumberList[3].number
                        clPhoneFour.visibility = View.VISIBLE
                        clPhoneFour.setOnClickListener {
                            animatorSetPhoneFour.start()
                            callTo(phoneNumberList[3].number)
                        }
                    }
                    if (count == 4) {
                        clPhoneSix.visibility = View.GONE
                        tvPhoneFive.text = phoneNumberList[4].number
                        clPhoneFive.visibility = View.VISIBLE
                        clPhoneFive.setOnClickListener {
                            animatorSetPhoneFive.start()
                            callTo(phoneNumberList[4].number)
                        }
                    }
                    if (count == 5) {
                        tvPhoneSix.text = phoneNumberList[5].number
                        clPhoneSix.visibility = View.VISIBLE
                        clPhoneSix.setOnClickListener {
                            animatorSetPhoneSix.start()
                            callTo(phoneNumberList[5].number)
                        }
                    }
                    count++
                }
            } else {
                clPhoneOne.visibility = View.GONE
                clPhoneTwo.visibility = View.GONE
                clPhoneThree.visibility = View.GONE
                clPhoneFour.visibility = View.GONE
                clPhoneFive.visibility = View.GONE
                clPhoneSix.visibility = View.GONE
                vContacts.visibility = View.GONE
            }
        }
    }

    private fun sendSmsOnClick(phoneNumber: String, message: String) {

        // Permission granted, send SMS
        sendSmsTo(phoneNumber, message)
//            Toast.makeText(requireContext(), "Sms sent successfully", Toast.LENGTH_SHORT).show()

    }

    private fun viewModelSelectContactList() = with(binding) {
        viewModel.getSaveContactWithRemainderId(reminderItemId).observe(viewLifecycleOwner) {
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
                        ivAvatarOne.setImageBitmap(getBitmap(saveContactList[0].avatar))
                        tvNameOne.text = saveContactList[0].name
                        tvPhoneNumberOne.text = saveContactList[0].number
                        clContactOne.visibility = View.VISIBLE
                        vContacts.visibility = View.VISIBLE
                        clContactOne.setOnClickListener {
                            animatorSetContactOne.start()
                            callTo(saveContactList[0].number)
                        }
                    }
                    if (count == 1) {
                        clContactThree.visibility = View.GONE
                        clContactFour.visibility = View.GONE
                        clContactFive.visibility = View.GONE
                        clContactSix.visibility = View.GONE
                        ivAvatarTwo.setImageBitmap(getBitmap(saveContactList[1].avatar))
                        tvNameTwo.text = saveContactList[1].name
                        tvPhoneNumberTwo.text = saveContactList[1].number
                        clContactTwo.visibility = View.VISIBLE
                        clContactTwo.setOnClickListener {
                            animatorSetContactTwo.start()
                            callTo(saveContactList[1].number)
                        }
                    }
                    if (count == 2) {
                        clContactFour.visibility = View.GONE
                        clContactFive.visibility = View.GONE
                        clContactSix.visibility = View.GONE
                        ivAvatarThree.setImageBitmap(getBitmap(saveContactList[2].avatar))
                        tvNameThree.text = saveContactList[2].name
                        tvPhoneNumberThree.text = saveContactList[2].number
                        clContactThree.visibility = View.VISIBLE
                        clContactThree.setOnClickListener {
                            animatorSetContactThree.start()
                            callTo(saveContactList[2].number)
                        }
                    }
                    if (count == 3) {
                        clContactFive.visibility = View.GONE
                        clContactSix.visibility = View.GONE
                        ivAvatarFour.setImageBitmap(getBitmap(saveContactList[3].avatar))
                        tvNameFour.text = saveContactList[3].name
                        tvPhoneNumberFour.text = saveContactList[3].number
                        clContactFour.visibility = View.VISIBLE
                        clContactFour.setOnClickListener {
                            animatorSetContactFour.start()
                            callTo(saveContactList[3].number)
                        }
                    }
                    if (count == 4) {
                        clContactSix.visibility = View.GONE
                        ivAvatarFive.setImageBitmap(getBitmap(saveContactList[4].avatar))
                        tvNameFive.text = saveContactList[4].name
                        tvPhoneNumberFive.text = saveContactList[4].number
                        clContactFive.visibility = View.VISIBLE
                        clContactFive.setOnClickListener {
                            animatorSetContactFive.start()
                            callTo(saveContactList[4].number)
                        }
                    }
                    if (count == 5) {
                        tvNameSix.text = saveContactList[5].name
                        tvPhoneNumberSix.text = saveContactList[5].number
                        clContactSix.visibility = View.VISIBLE
                        clContactSix.setOnClickListener {
                            animatorSetContactSix.start()
                            callTo(saveContactList[5].number)
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
                vContacts.visibility = View.GONE

            }
        }
    }

    private fun viewModelSmsList() = with(binding) {
        viewModel.getSmsWithRemainderId(reminderItemId).observe(viewLifecycleOwner) {
            smsList = it
            if (smsList.isNotEmpty()) {
                addSms = true
                var count = 0
                repeat(it.size) {
                    if (count == 0) {
                        clMessageTwo.visibility = View.GONE
                        clMessageThree.visibility = View.GONE
                        clMessageFour.visibility = View.GONE
                        clMessageFive.visibility = View.GONE
                        clMessageSix.visibility = View.GONE
                        if(smsList[0].name == "") {
                            tvMessageContactOne.text = smsList[0].smsPhone
                        } else {
                            tvMessageContactOne.text = smsList[0].name
                        }

                        tvMessageTextOne.text = smsList[0].smsMessage
                        ivAvatarMessageOne.setImageBitmap(getBitmap(smsList[0].avatar))
                        clMessageOne.visibility = View.VISIBLE
                        vLineMessage.visibility = View.VISIBLE
                        clMessageOne.setOnClickListener {
                            animatorSetMessageOne.start()
                            sendSmsOnClick(smsList[0].smsPhone, smsList[0].smsMessage)
                        }
                    }
                    if (count == 1) {
                        clMessageThree.visibility = View.GONE
                        clMessageFour.visibility = View.GONE
                        clMessageFive.visibility = View.GONE
                        clMessageSix.visibility = View.GONE
                        if(smsList[1].name == "") {
                            tvMessageContactTwo.text = smsList[1].smsPhone
                        } else {
                            tvMessageContactTwo.text = smsList[1].name
                        }
                        tvMessageTextTwo.text = smsList[1].smsMessage
                        ivAvatarMessageTwo.setImageBitmap(getBitmap(smsList[1].avatar))
                        clMessageTwo.visibility = View.VISIBLE
                        clMessageTwo.setOnClickListener {
                            animatorSetMessageTwo.start()
                            sendSmsOnClick(smsList[1].smsPhone, smsList[1].smsMessage)
                        }
                    }
                    if (count == 2) {
                        clMessageFour.visibility = View.GONE
                        clMessageFive.visibility = View.GONE
                        clMessageSix.visibility = View.GONE
                        if(smsList[2].name == "") {
                            tvMessageContactThree.text = smsList[2].smsPhone
                        } else {
                            tvMessageContactThree.text = smsList[2].name
                        }
                        tvMessageTextThree.text = smsList[2].smsMessage
                        ivAvatarMessageThree.setImageBitmap(getBitmap(smsList[2].avatar))
                        clMessageThree.visibility = View.VISIBLE
                        clMessageThree.setOnClickListener {
                            animatorSetMessageThree.start()
                            sendSmsOnClick(smsList[2].smsPhone, smsList[2].smsMessage)
                        }
                    }
                    if (count == 3) {
                        clMessageFive.visibility = View.GONE
                        clMessageSix.visibility = View.GONE
                        if(smsList[3].name == "") {
                            tvMessageContactFour.text = smsList[3].smsPhone
                        } else {
                            tvMessageContactFour.text = smsList[3].name
                        }
                        tvMessageTextFour.text = smsList[3].smsMessage
                        ivAvatarMessageFour.setImageBitmap(getBitmap(smsList[3].avatar))
                        clMessageFour.visibility = View.VISIBLE
                        clMessageFour.setOnClickListener {
                            animatorSetMessageFour.start()
                            sendSmsOnClick(smsList[3].smsPhone, smsList[3].smsMessage)
                        }
                    }
                    if (count == 4) {
                        clMessageSix.visibility = View.GONE
                        if(smsList[4].name == "") {
                            tvMessageContactFive.text = smsList[4].smsPhone
                        } else {
                            tvMessageContactFive.text = smsList[4].name
                        }
                        tvMessageTextFive.text = smsList[4].smsMessage
                        ivAvatarMessageFive.setImageBitmap(getBitmap(smsList[4].avatar))
                        clMessageFive.visibility = View.VISIBLE
                        clMessageFive.setOnClickListener {
                            animatorSetMessageFive.start()
                            sendSmsOnClick(smsList[4].smsPhone, smsList[4].smsMessage)
                        }
                    }
                    count++
                }
            } else {
                clMessageOne.visibility = View.GONE
                clMessageTwo.visibility = View.GONE
                clMessageThree.visibility = View.GONE
                clMessageFour.visibility = View.GONE
                clMessageFive.visibility = View.GONE
                clMessageSix.visibility = View.GONE
                vLineMessage.visibility = View.GONE
            }
        }
    }
    private fun viewModelImageItem() {
        viewModel.getImageWithRemainderId(reminderItemId).observe(viewLifecycleOwner) { imageList ->
            val imageView = binding.imageView

            // Очищаем кэш Glide
            Glide.get(requireContext()).clearMemory()
            CoroutineScope(Dispatchers.IO).launch {
                Glide.get(requireContext()).clearDiskCache()
            }

            imageList.firstOrNull()?.let { imageItem ->
                val imagePath = imageItem.imageName
                val file = File(requireContext().getExternalFilesDir("Pictures"), imagePath)

                Glide.with(requireContext())
                    .load(file)
                    .into(imageView)
            } ?: run {
                if (prefs.notificationBackgroundImage == 1) {
                    val file = File(
                        requireContext().getExternalFilesDir("Pictures"),
                        DEFAULT_IMAGE
                    )
                    Glide.with(requireContext())
                        .load(file)
                        .into(imageView)
                } else {
                    imageView.setBackgroundColor(Color.GRAY)
                }
            }
        }
    }


    private fun viewModelFileItem() = with(binding) {
        viewModel.getFileWithRemainderId(reminderItemId).observe(viewLifecycleOwner) {
            fileList = it
            if (fileList.isNotEmpty()) {
                clFile.visibility = View.VISIBLE
//                ivAddedFile.visibility = View.VISIBLE
//                tvAddedFilename.visibility = View.VISIBLE
                tvAddedFilename.text = fileList[0].name
                llAddFile.setOnClickListener {
                    animatorSetFile.start()
                    openFile(fileList[0].name)
                }

            } else {

                cvMessage.setBackgroundColor(Color.GRAY)

            }
        }
    }

    private fun parseParams() {
        val args = this@ReminderItemNotificationFragment.arguments
        if (args != null) {
            if (!args.containsKey(REMINDER_ITEM_ID)) {
                throw RuntimeException("Param reminder item id is absent")
            } else {
                reminderItemId = args.getInt(REMINDER_ITEM_ID)
            }
        }
    }


    companion object {
        const val NAME = "ReminderItemNotificationFragment"
        const val UNDEFINED_ID = 0
        const val REMINDER_ITEM_ID = "reminder_item_id"
        const val STATUS_ACTIVE = true
        const val STATUS_INACTIVE = false
        const val REQUEST_CODE_SEND_EMAIL = 100
        const val PERMISSION_REQUEST_SEND_SMS = 123
        const val TIME_TO_EVENT = "timeToEvent"
        const val REMIND_ID = "remindId"
        const val DEFAULT_IMAGE = "default_image.jpg"

        private const val CHANNEL_ID = "channel_id"


        fun newInstance(remainderId: Int): ReminderItemNotificationFragment {
            return ReminderItemNotificationFragment().apply {
                arguments = Bundle().apply {
                    putInt(REMINDER_ITEM_ID, remainderId)
                }
            }
        }
    }

    private fun launchReminderEditFragment(id: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderEditFragment.newInstance(id))
            .addToBackStack(ReminderEditFragment.NAME)
            .commitAllowingStateLoss()
    }

    private fun launchReminderListFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderListFragment.newInstance())
            .addToBackStack(ReminderListFragment.NAME).commit()
    }
}