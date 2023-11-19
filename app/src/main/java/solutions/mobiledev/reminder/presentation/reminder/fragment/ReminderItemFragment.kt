package solutions.mobiledev.reminder.presentation.reminder.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import solutions.mobiledev.reminder.R
import solutions.mobiledev.reminder.databinding.FragmentReminderItemBinding
import solutions.mobiledev.reminder.domain.entity.*
import solutions.mobiledev.reminder.presentation.BaseFragment
import solutions.mobiledev.reminder.presentation.notification.ReminderNotificationFragment
import solutions.mobiledev.reminder.presentation.reminder.ReminderViewModel
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderAddFragment.Companion.UNDEFINED_ID
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private lateinit var viewModel: ReminderViewModel
private lateinit var saveContactList: List<SaveContactItem>
private lateinit var smsList: List<SmsItem>
private lateinit var emailList: List<EmailItem>
private lateinit var phoneNumberList: List<PhoneNumberItem>
private var addPhoneNumber: Boolean = false
private lateinit var imageList: List<ImageItem>
private lateinit var fileList: List<FileItem>
private lateinit var melodyList: List<MelodyItem>
private var reminderItemId: Int = UNDEFINED_ID
private var addContacts: Boolean = false
private var addSms: Boolean = false
private var addEmail: Boolean = false
private var addFile: Boolean = false
private var addMelody: Boolean = false
private var addImage: Boolean = false
private var remainderId: Int = UNDEFINED_ID



class ReminderItemFragment : BaseFragment<FragmentReminderItemBinding>() {


    private var _binding: FragmentReminderItemBinding? = null
    private val binding: FragmentReminderItemBinding
        get() = _binding ?: throw RuntimeException("FragmentReminderItemBinding == null")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderItemBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        viewModel = ViewModelProvider(this@ReminderItemFragment)[ReminderViewModel::class.java]
        super.onViewCreated(view, savedInstanceState)

        viewModel.getReminderViewItem(reminderItemId)
        viewModel.reminderItem.observe(viewLifecycleOwner) {
            if (it != null) {
                val menu1 = getString(R.string.dropdown_menu_timeout)
                tvMenu1.text = "$menu1 ${it.menuTimeout}"
                val menu2 = getString(R.string.dropdown_repeat_frequency)
                tvMenu2.text = "$menu2 ${it.menuRepeatFrequency}"
                val menu3 = getString(R.string.dropdown_repeat_count)
                tvMenu3.text = "$menu3 ${it.menuRepeatCount}"
                val localDate = LocalDate.parse(it.dateRemaining)
                getFormatDate(localDate)
                tvMessage.text = it.body
                val localTime = LocalTime.parse(it.timeRemaining)
                tvTime.text = dateTimeManager.getTime(localTime)
            }
        }

        viewModelSelectContactList()
        viewModelSmsList()
        viewModelEmailList()
        viewModelPhoneNumberList()
        viewModelImageItem()
        viewModelFileItem()
        viewModelMelodyItem()

        laClose.setOnClickListener {
            launchReminderListFragment()
        }
        tvReminderDate.setOnClickListener {
            launchReminderNotificationFragment(reminderItemId)
        }
        laEdit.setOnClickListener {
            launchReminderEditFragment(reminderItemId)
        }
    }


    private fun getFormatDate(date: LocalDate) = with(binding) {
        val formatterTwo = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDateTwo = date.format(formatterTwo)
        tvCalendar.text = dateTimeManager.getDate(date)
        tvCalendarTwo.text = formattedDateTwo
    }

    private fun getBitmap(avatar: String): Bitmap {
        if (avatar != "null") {
            val cr = requireContext().contentResolver
            val source = ImageDecoder.createSource(cr, Uri.parse(avatar))
            return ImageDecoder.decodeBitmap(source)
        } else {
            return BitmapFactory.decodeResource(resources, R.drawable.no_image_icon)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_SEND_SMS -> {
                // Проверяем, дано ли разрешение на отправку SMS
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Если разрешение дано, отправляем SMS
                    sendSms("+380959288472", "message")
                } else {
                    // Если разрешение не дано, выводим сообщение об ошибке
                    Toast.makeText(
                        requireContext(), "Нет разрешения на отправку SMS", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun sendSms(phoneNumber: String, message: String) {

        // Создаем Intent для запуска системного приложения для отправки SMS
        val uri = Uri.parse("smsto:$phoneNumber")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", message)

        // Запускаем системное приложение для отправки SMS
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            // Если нет подходящего приложения для отправки SMS, выводим сообщение об ошибке
            Toast.makeText(requireContext(), "Нет приложения для отправки SMS", Toast.LENGTH_SHORT)
                .show()
        }
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
                        ivContactOne.setImageBitmap(getBitmap(saveContactList[0].avatar))
                        tvContactOne.text = saveContactList[0].name
                        clContactOne.visibility = View.VISIBLE
                        ivContactOne.visibility = View.VISIBLE
                    }
                    if (count == 1) {
                        clContactThree.visibility = View.GONE
                        clContactFour.visibility = View.GONE
                        clContactFive.visibility = View.GONE
                        clContactSix.visibility = View.GONE
                        ivContactTwo.setImageBitmap(getBitmap(saveContactList[1].avatar))
                        tvContactTwo.text = saveContactList[1].name
                        clContactTwo.visibility = View.VISIBLE
                    }
                    if (count == 2) {
                        clContactFour.visibility = View.GONE
                        clContactFive.visibility = View.GONE
                        clContactSix.visibility = View.GONE
                        ivContactThree.setImageBitmap(getBitmap(saveContactList[2].avatar))
                        tvContactThree.text = saveContactList[2].name
                        clContactThree.visibility = View.VISIBLE
                    }
                    if (count == 3) {
                        clContactFive.visibility = View.GONE
                        clContactSix.visibility = View.GONE
                        ivContactFour.setImageBitmap(getBitmap(saveContactList[3].avatar))
                        tvContactFour.text = saveContactList[3].name
                        clContactFour.visibility = View.VISIBLE
                    }
                    if (count == 4) {
                        clContactSix.visibility = View.GONE
                        ivContactFive.setImageBitmap(getBitmap(saveContactList[4].avatar))
                        tvContactFive.text = saveContactList[4].name
                        clContactFive.visibility = View.VISIBLE
                    }
                    if (count == 5) {
                        ivContactSix.setImageBitmap(getBitmap(saveContactList[5].avatar))
                        tvContactSix.text = saveContactList[5].name
                        clContactSix.visibility = View.VISIBLE
                        clContactSix.setOnClickListener {
                            viewModel.deleteSaveContactReminder(saveContactList[5])
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
        viewModel.getSmsWithRemainderId(reminderItemId).observe(viewLifecycleOwner) {
            smsList = it
            if (smsList.isNotEmpty()) {
                addSms = true
                var count = 0
                repeat(it.size) {
                    if (count == 0) {
                        clSms3.visibility = View.GONE
                        clSms4.visibility = View.GONE
                        clSms5.visibility = View.GONE
                        tvSms2.text = null

                        tvSms1.text = smsList[0].smsMessage
                        tvSms1.visibility = View.VISIBLE
                        clSms1.setOnClickListener {
                            // Проверяем, есть ли у приложения разрешение на отправку SMS
                            if (ContextCompat.checkSelfPermission(
                                    requireContext(), Manifest.permission.SEND_SMS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // Если нет, запрашиваем его
                                ActivityCompat.requestPermissions(
                                    requireActivity(),
                                    arrayOf(Manifest.permission.SEND_SMS),
                                    REQUEST_CODE_SEND_SMS
                                )
                            } else {
                                // Если есть, отправляем SMS
                                sendSms(smsList[0].smsPhone, smsList[0].smsMessage)
                            }
                        }
                    }
                    if (count == 1) {
                        clSms3.visibility = View.GONE
                        clSms4.visibility = View.GONE
                        clSms5.visibility = View.GONE
                        tvSms2.text = smsList[1].smsMessage
                        tvSms2.visibility = View.VISIBLE

                    }
                    if (count == 2) {
                        clSms4.visibility = View.GONE
                        clSms5.visibility = View.GONE

                        tvSms3.text = smsList[2].smsMessage
                        clSms3.visibility = View.VISIBLE
                        tvSms3.visibility = View.VISIBLE

                    }
                    if (count == 3) {
                        clSms5.visibility = View.GONE
                        tvSms4.text = smsList[3].smsMessage
                        clSms4.visibility = View.VISIBLE
                        tvSms4.visibility = View.VISIBLE

                    }
                    if (count == 4) {
                        tvSms5.text = smsList[4].smsMessage
                        clSms5.visibility = View.VISIBLE
                        tvSms5.visibility = View.VISIBLE
                    }
                    count++
                }
            } else {
                tvSms1.text = null
                tvSms1.visibility = View.VISIBLE
            }
        }
    }

    private fun viewModelEmailList() = with(binding) {
        viewModel.getEmailWithRemainderId(reminderItemId).observe(viewLifecycleOwner) {
            emailList = it
            if (emailList.isNotEmpty()) {
                addEmail = true
                var count = 0
                repeat(it.size) {
                    if (count == 0) {

                        clEmail3.visibility = View.GONE
                        clEmail4.visibility = View.GONE
                        clEmail5.visibility = View.GONE
                        tvEmail1.text = emailList[0].emailAddress
                        tvEmail1.visibility = View.VISIBLE
                        tvEmail2.text = null
                        clEmail1.visibility = View.VISIBLE
                    }
                    if (count == 1) {
                        clEmail3.visibility = View.GONE
                        clEmail4.visibility = View.GONE
                        clEmail5.visibility = View.GONE
                        tvEmail2.text = emailList[1].emailAddress
                        tvEmail2.visibility = View.VISIBLE
                        clEmail2.visibility = View.VISIBLE
                    }
                    if (count == 2) {
                        clEmail4.visibility = View.GONE
                        clEmail5.visibility = View.GONE
                        tvEmail3.text = emailList[2].emailAddress
                        clEmail3.visibility = View.VISIBLE
                        tvEmail3.visibility = View.VISIBLE
                    }
                    if (count == 3) {
                        clEmail5.visibility = View.GONE
                        tvEmail4.text = emailList[3].emailAddress
                        clEmail4.visibility = View.VISIBLE
                        tvEmail4.visibility = View.VISIBLE
                    }
                    if (count == 4) {
                        tvEmail5.text = emailList[4].emailAddress
                        clEmail5.visibility = View.VISIBLE
                        tvEmail5.visibility = View.VISIBLE
                    }
                    count++
                }
            } else {
                tvEmail1.text = null
                tvEmail1.visibility = View.VISIBLE
            }
        }
    }

    private fun viewModelPhoneNumberList() = with(binding) {
        viewModel.getPhoneNumberWithRemainderId(reminderItemId).observe(viewLifecycleOwner) {
            phoneNumberList = it
            if (phoneNumberList.isNotEmpty()) {
                addPhoneNumber = true
                var count = 0
                repeat(it.size) {
                    if (count == 0) {
                        tvPhone2.text = null
                        tvPhone1.text = phoneNumberList[0].number
                        tvPhone1.visibility = View.VISIBLE
                    }
                    if (count == 1) {
                        tvPhone2.text = phoneNumberList[1].number
                        tvPhone2.visibility = View.VISIBLE
                    }
                    if (count == 2) {
                        tvPhone3.text = phoneNumberList[2].number
                        clPhone3.visibility = View.VISIBLE
                    }
                    if (count == 3) {
                        tvPhone4.text = phoneNumberList[3].number
                        clPhone4.visibility = View.VISIBLE

                    }
                    if (count == 4) {
                        tvPhone5.text = phoneNumberList[4].number
                        clPhone5.visibility = View.VISIBLE

                    }
                    count++
                }
            } else {
                tvPhone1.text = null
                tvPhone1.visibility = View.VISIBLE
                clPhone3.visibility = View.GONE
                clPhone4.visibility = View.GONE
                clPhone5.visibility = View.GONE
            }
        }
    }

    private fun viewModelImageItem() = with(binding) {
        viewModel.getImageWithRemainderId(reminderItemId).observe(viewLifecycleOwner) {
            imageList = it
            if (imageList.isNotEmpty()) {
                addImage = true
                val file = File(
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    imageList[0].imageName
                )
                val bitmap = BitmapFactory.decodeFile(file.path)
                ivAddImage.setImageBitmap(bitmap)
            } else {
                ivAddImage.setImageResource(R.drawable.addimage)
            }
        }
    }

    private fun viewModelFileItem() = with(binding) {
        viewModel.getFileWithRemainderId(reminderItemId).observe(viewLifecycleOwner) {
            fileList = it
            if (fileList.isNotEmpty()) {
                addFile = true
                ivAddFile.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.file_added
                    )
                )
                tvAddFile.text = fileList[0].name
                llAddFile.visibility = View.VISIBLE
            } else {
                llAddFile.visibility = View.GONE
            }
        }
    }

    private fun viewModelMelodyItem() = with(binding) {
        viewModel.getMelodyWithRemainderId(reminderItemId).observe(viewLifecycleOwner) {
            melodyList = it
            if (melodyList.isNotEmpty()) {
                addMelody = true
                ivMelody.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.melody
                    )
                )
                tvMelody.text = melodyList[0].melodyName
            } else {
            }
        }
    }

    private fun parseParams() {
        val args = this@ReminderItemFragment.arguments
        if (args != null) {
            if (!args.containsKey(REMINDER_ITEM_ID)) {
                throw RuntimeException("Param reminder item id is absent")
            } else {
                reminderItemId = args.getInt(REMINDER_ITEM_ID)
            }
        }
    }


    companion object {

        const val NAME = "ReminderItemFragment"
        const val UNDEFINED_ID = 0
        const val REMINDER_ITEM_ID = "reminder_item_id"
        const val REQUEST_CODE_SEND_SMS = 123


        fun newInstance(remainderId: Int): ReminderItemFragment {
            return ReminderItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(REMINDER_ITEM_ID, remainderId)
                }
            }
        }
    }

    private fun launchReminderListFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderListFragment.newInstance())
            .addToBackStack(ReminderListFragment.NAME).commit()
    }

    private fun launchReminderEditFragment(id: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderEditFragment.newInstance(id))
            .addToBackStack(ReminderEditFragment.NAME).commit()
    }

    private fun launchReminderNotificationFragment(id: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            .replace(R.id.main_container, ReminderNotificationFragment.newInstance(id))
            .addToBackStack(null).commit()
    }
}
