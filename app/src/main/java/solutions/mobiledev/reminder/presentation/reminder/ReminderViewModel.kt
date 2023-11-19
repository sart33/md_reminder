package solutions.mobiledev.reminder.presentation.reminder

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Environment
import android.os.LocaleList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import solutions.mobiledev.reminder.data.AppDatabase
import solutions.mobiledev.reminder.data.ContactRepositoryImpl
import solutions.mobiledev.reminder.data.reminder.ReminderItemDbModel
import solutions.mobiledev.reminder.data.reminder.ReminderListMapper
import solutions.mobiledev.reminder.data.reminder.ReminderRepositoryImpl
import solutions.mobiledev.reminder.data.reminder.addEmail.EmailRepositoryImpl
import solutions.mobiledev.reminder.data.reminder.addFile.FileRepositoryImpl
import solutions.mobiledev.reminder.data.reminder.addImage.ImageRepositoryImpl
import solutions.mobiledev.reminder.data.reminder.addMelody.MelodyRepositoryImpl
import solutions.mobiledev.reminder.data.reminder.addPhoneNumber.PhoneNumberRepositoryImpl
import solutions.mobiledev.reminder.data.reminder.addSaveContact.SaveContactRepositoryImpl
import solutions.mobiledev.reminder.data.reminder.addSms.SmsRepositoryImpl
import solutions.mobiledev.reminder.domain.entity.*
import solutions.mobiledev.reminder.domain.reminder.*
import solutions.mobiledev.reminder.domain.reminder.contact.EditContactItemUseCase
import solutions.mobiledev.reminder.domain.reminder.contact.selectList.EditContactItemOfSelectListUseCase
import solutions.mobiledev.reminder.domain.reminder.email.*
import solutions.mobiledev.reminder.domain.reminder.email.DeleteEmailItemUseCase
import solutions.mobiledev.reminder.domain.reminder.file.*
import solutions.mobiledev.reminder.domain.reminder.image.*
import solutions.mobiledev.reminder.domain.reminder.melody.*
import solutions.mobiledev.reminder.domain.reminder.menu.*
import solutions.mobiledev.reminder.domain.reminder.phoneNumber.*
import solutions.mobiledev.reminder.domain.reminder.saveContact.*
import solutions.mobiledev.reminder.domain.reminder.sms.*
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderAddFragment.Companion.EMAIL_ADDRESS_PATTERN
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderAddFragment.Companion.PHONE_NUMBER_PATTERN
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderAddFragment.Companion.REMINDER_DATE_PATTERN
import solutions.mobiledev.reminder.presentation.reminder.fragment.ReminderAddFragment.Companion.REMINDER_TIME_PATTERN
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.Duration.*

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val smsRep = SmsRepositoryImpl(application)
    private val phoneNumberRep = PhoneNumberRepositoryImpl(application)
    private val conRep = ContactRepositoryImpl
    private val emailRep = EmailRepositoryImpl(application)
    private val saveConRep = SaveContactRepositoryImpl(application)
    private val imageRep = ImageRepositoryImpl(application)
    private val fileRep = FileRepositoryImpl(application)
    private val melodyRep = MelodyRepositoryImpl(application)

    private val editContactItemSelect = EditContactItemOfSelectListUseCase(conRep)
    private val editContactItem = EditContactItemUseCase(conRep)


    private val addSmsItemRep = AddSmsItemUseCase(smsRep)
    private val getSmsListRep = GetSmsListUseCase(smsRep)
    private val getSmsFromRemId = GetSmsWithRemIdUseCase(smsRep)
    private val deleteSmsRep = DeleteSmsItemUseCase(smsRep)
    private val deleteSmsFromRemId = DeleteSmsFromRemIdUseCase(smsRep)
    private val bindCurrentSmsToRem = BindCurrentSmsToReminderUseCase(smsRep)

    private val addPhoneNumberItemRep = AddPhoneNumberItemUseCase(phoneNumberRep)
    private val getPhoneNumberListRep = GetPhoneNumberListUseCase(phoneNumberRep)
    private val getPhoneNumberFromRemId = GetPhoneNumberWithRemIdUseCase(phoneNumberRep)
    private val deletePhoneNumberRep = DeletePhoneNumberItemUseCase(phoneNumberRep)
    private val deletePhoneNumberFromRemId = DeletePhoneNumberFromRemIdUseCase(phoneNumberRep)
    private val bindCurrentPhoneNumberToRem =
        BindCurrentPhoneNumberToReminderUseCase(phoneNumberRep)

    private val addImageItemRep = AddImageItemUseCase(imageRep)
    private val getImageItemRep = GetImageItemUseCase(imageRep)
    private val getImageFromRemId = GetImageWithRemIdUseCase(imageRep)
    private val deleteImageRep = DeleteImageFromRemIdUseCase(imageRep)
    private val deleteImageFromRemId = DeleteImageFromRemIdUseCase(imageRep)
    private val bindCurrentImageToRem = BindCurrentImageToReminderUseCase(imageRep)

    private val addFileItemRep = AddFileItemUseCase(fileRep)
    private val getFileItemRep = GetFileItemUseCase(fileRep)
    private val getFileFromRemId = GetFileWithRemIdUseCase(fileRep)
    private val deleteFileRep = DeleteFileFromRemIdUseCase(fileRep)
    private val deleteFileFromRemId = DeleteFileFromRemIdUseCase(fileRep)
    private val bindCurrentFileToRem = BindCurrentFileToReminderUseCase(fileRep)

    private val addMelodyItemRep = AddMelodyItemUseCase(melodyRep)
    private val getMelodyItemRep = GetMelodyItemUseCase(melodyRep)
    private val getMelodyFromRemId = GetMelodyWithRemIdUseCase(melodyRep)
    private val deleteMelodyRep = DeleteMelodyFromRemIdUseCase(melodyRep)
    private val deleteMelodyFromRemId = DeleteMelodyFromRemIdUseCase(melodyRep)
    private val bindCurrentMelodyToRem = BindCurrentMelodyToReminderUseCase(melodyRep)


    private val addSaveContactItemRep = AddSaveContactItemUseCase(saveConRep)
    private val getSaveContactListRep = GetSaveContactListUseCase(saveConRep)
    private val getSaveContactFromRemId = GetSaveContactWithRemIdUseCase(saveConRep)
    private val deleteSaveContactRep = DeleteSaveContactItemUseCase(saveConRep)
    private val deleteSaveContactFromRemId = DeleteSaveContactFromRemIdUseCase(saveConRep)
    private val bindCurrentSaveContactToRem =
        BindCurrentSaveContactToReminderUseCase(saveConRep)

    private val addEmailItemRep = AddEmailItemUseCase(emailRep)
    private val getEmailListRep = GetEmailListUseCase(emailRep)
    private val getEmailFromRemId = GetEmailWithRemIdUseCase(emailRep)
    private val deleteEmailRep = DeleteEmailItemUseCase(emailRep)
    private val deleteEmailFromRemId = DeleteEmailFromRemIdUseCase(emailRep)
    private val bindCurrentEmailsToRem = BindCurrentEmailsToReminderUseCase(emailRep)

    private val remRepository = ReminderRepositoryImpl(application)


    private val addReminderItem = AddReminderItemUseCase(remRepository)
    private val activeChangeStatus = ActiveChangeStatusUseCase(remRepository)
    private val showInWidgetChangeStatus = ShowInWidgetChangeStatusUseCase(remRepository)
    private val editReminderItem = EditReminderItemUseCase(remRepository)
    val deleteReminderItem = DeleteReminderItemUseCase(remRepository)
    private val getReminderItem = GetReminderItemUseCase(remRepository)
    private val getReminderList = GetReminderListUseCase(remRepository)
    private val getRemindersAtDate = GetRemindersAtDateUseCase(remRepository)
    private val getRemindersCount = GetRemindersCountUseCase(remRepository)

    private val getLastInsertId = GetLastInsertIdUseCase(remRepository)
    private val getLastInsertIdWithTemporary =
        GetLastInsertIdWithTemporaryUseCase(remRepository)
    private val checkDate = CheckDateUseCase(remRepository)

    //    private val timeActivateRemindList = TimeActivateRemindListUseCase(remRepository)
    private val timeActivateReminderTimer = TimeActivateReminderTimerUseCase(remRepository)
    private val editDateTimeReminding = EditDateTimeRemindingUseCase(remRepository)
    private val editDateTimeRemindingAndCount = EditDateTimeRemindingAndCountUseCase(remRepository)
    private val editDateTimeDateTimeRemindingAndCount = EditDateTimeDateTimeRemindingUseCase(remRepository)


    val reminderList = getReminderList.invoke()
//        val remindersAtDate = getRemindersAtDate.invoke(date)

    private val _errorInputString = MutableLiveData<Boolean>()
    private val _errorInputNumber = MutableLiveData<Boolean>()

    private val _errorInputBody = MutableLiveData<Boolean>()
    private val _errorInputAdd = MutableLiveData<Boolean>()
    private val _errorInputDateRemaining = MutableLiveData<Boolean>()
    private val _errorInputTimeRemaining = MutableLiveData<Boolean>()
    private val _errorInputTimePeriod = MutableLiveData<Boolean>()
    private val _errorInputRemainingCount = MutableLiveData<Boolean>()
    private val _errorInputContacts = MutableLiveData<Boolean>()
    private val _errorInputMails = MutableLiveData<Boolean>()
    private val _errorInputTelNumbers = MutableLiveData<Boolean>()
    private val _errorInputSms = MutableLiveData<Boolean>()
    private val _errorInputImage = MutableLiveData<Boolean>()
    private val _errorInputFile = MutableLiveData<Boolean>()
    private val _errorInputState = MutableLiveData<Boolean>()

    private val _reminderItem = MutableLiveData<ReminderItem?>()
    val reminderItem: LiveData<ReminderItem?>
        get() = _reminderItem


    private val _reminderItemTwo = MutableLiveData<ReminderItem?>()
    val reminderItemTwo: LiveData<ReminderItem?>
        get() = _reminderItemTwo

    private val _emptyReminderTable = MutableLiveData<Int>()
    val emptyReminderTable: LiveData<Int>
        get() = _emptyReminderTable

    private var _remindersCount = MutableLiveData<Int>()
    val remindersCount: LiveData<Int>
        get() = _remindersCount

    private val _shouldCloseScreen = MutableLiveData<Unit>()

    private var _remindersAtDate = MutableLiveData<List<ReminderItem>>()
    val remindersAtDate: LiveData<List<ReminderItem>>
        get() = _remindersAtDate

    /**
     * Setting block
     */

    private val _settingItem = MutableLiveData<SettingItem?>()
    val settingItem: LiveData<SettingItem?>
        get() = _settingItem

    /**
     * Melody block
     */

    private val _melodyItem = MutableLiveData<MelodyItem?>()
    val melodyItem: LiveData<MelodyItem?>
        get() = _melodyItem

    private val _melodyList = MutableLiveData<List<MelodyItem>>()
    val melodyList: LiveData<List<MelodyItem>>
        get() = _melodyList

    private val _melodyName = MutableLiveData<String>()
    val melodyName: LiveData<String>
        get() = _melodyName

    private val _melodyPath = MutableLiveData<String>()
    val melodyPath: LiveData<String>
        get() = _melodyPath

    /**
     * Error block
     */


    private val _errorInputMenuRepeatCount = MutableLiveData<Boolean>()
    val errorInputMenuRepeatCount: LiveData<Boolean>
        get() = _errorInputMenuRepeatCount

    private val _errorInputMenuRepeatFrequency = MutableLiveData<Boolean>()
    val errorInputMenuRepeatFrequency: LiveData<Boolean>
        get() = _errorInputMenuRepeatFrequency

    private val _errorInputMenuTimeout = MutableLiveData<Boolean>()
    val errorInputMenuTimeout: LiveData<Boolean>
        get() = _errorInputMenuTimeout

    private val _formattedDateFromDB = MutableLiveData<String>()
    val formattedDateFromDB: LiveData<String>
        get() = _formattedDateFromDB


    /***
     * Date and Time block
     */

    private val _reminderTime = MutableLiveData<LocalDateTime>()
    val reminderTime: LiveData<LocalDateTime>
        get() = _reminderTime

    private val _dateItem = MutableLiveData<String>()
    val dateItem: LiveData<String>
        get() = _dateItem

    private val _timeItem = MutableLiveData<String>()
    val timeItem: LiveData<String>
        get() = _timeItem

    /***
     * selected contacts block
     */

    private val _selectedContacts = MutableLiveData<List<ContactItem>>()
    val selectedContacts: LiveData<List<ContactItem>>
        get() = _selectedContacts

    /***
     * sms block
     */

    private val _contactItem = MutableLiveData<ContactItem>()
    val contactItem: LiveData<ContactItem>
        get() = _contactItem

    private val _contactList = MutableLiveData<List<ContactItem>>()
    val contactList: LiveData<List<ContactItem>>
        get() = _contactList

    private val _selectedSmsItem = MutableLiveData<List<SmsItem>>()
    val selectedSmsData: LiveData<List<SmsItem>>
        get() = _selectedSmsItem

    private val _smsItem = MutableLiveData<SmsItem>()
    val smsItem: LiveData<SmsItem>
        get() = _smsItem

    private val _smsList = MutableLiveData<List<SmsItem>>()
    val smsList: LiveData<List<SmsItem>>
        get() = _smsList

    private val _smsMessage = MutableLiveData<String>()
    val smsMessage: LiveData<String>
        get() = _smsMessage

    private val _deleteSmsReminder = MutableLiveData<SmsItem>()
    val deleteSmsReminder: LiveData<SmsItem>
        get() = _deleteSmsReminder

    private val _errorInputPhoneNumber = MutableLiveData<Boolean>()
    val errorInputPhoneNumber: LiveData<Boolean>
        get() = _errorInputPhoneNumber

    private val _errorInputMessage = MutableLiveData<Boolean>()
    val errorInputMessage: LiveData<Boolean>
        get() = _errorInputMessage


    /***
     * email block
     */
    private val _selectedEmailItem = MutableLiveData<List<EmailItem>>()
    val selectedEmailItem: LiveData<List<EmailItem>>
        get() = _selectedEmailItem

    private val _emailItem = MutableLiveData<EmailItem>()
    val emailItem: LiveData<EmailItem>
        get() = _emailItem

    private val _emailList = MutableLiveData<List<EmailItem>>()
    val emailList: LiveData<List<EmailItem>>
        get() = _emailList

    private val _emailMessage = MutableLiveData<String>()
    val emailMessage: LiveData<String>
        get() = _emailMessage

    private val _errorInputMail = MutableLiveData<Boolean>()
    val errorInputMail: LiveData<Boolean>
        get() = _errorInputMail

    /**
     * file block
     */

    private val _fileItem = MutableLiveData<FileItem>()
    val fileItem: LiveData<FileItem>
        get() = _fileItem

    private val _fileList = MutableLiveData<List<FileItem>>()
    val fileList: LiveData<List<FileItem>>
        get() = _fileList

    private val _fileName = MutableLiveData<String>()
    val fileName: LiveData<String>
        get() = _fileName

    private val _filePath = MutableLiveData<String>()
    val filePath: LiveData<String>
        get() = _filePath

    /***
     * image block
     */
//    private val _selectedEmailItem = MutableLiveData<List<EmailItem>>()
//    val selectedEmailItem: LiveData<List<EmailItem>>
//        get() = _selectedEmailItem

    private val _imageItem = MutableLiveData<ImageItem>()
    val imageItem: LiveData<ImageItem>
        get() = _imageItem

    private val _imageList = MutableLiveData<List<ImageItem>>()
    val imageList: LiveData<List<ImageItem>>
        get() = _imageList

    private val _imageName = MutableLiveData<String>()
    val imageName: LiveData<String>
        get() = _imageName

    private val _imagePath = MutableLiveData<String>()
    val imagePath: LiveData<String>
        get() = _imagePath

//    private val _errorInputImage = MutableLiveData<Boolean>()
//    val errorInputImage: LiveData<Boolean>
//        get() = _errorInputImage


    /***
     * reminder menu block
     */
    private val _selectedReminderMenuItem = MutableLiveData<List<ReminderMenuItem>>()
    val selectedReminderMenuItem: LiveData<List<ReminderMenuItem>>
        get() = _selectedReminderMenuItem

    private val _reminderMenuItem = MutableLiveData<ReminderMenuItem>()
    val reminderMenuItem: LiveData<ReminderMenuItem>
        get() = _reminderMenuItem

    private val _reminderMenuTimeOut = MutableLiveData<Int>()
    val reminderMenuTimeOut: LiveData<Int>
        get() = _reminderMenuTimeOut

    private val _reminderMenuCount = MutableLiveData<Int>()
    val reminderMenuCount: LiveData<Int>
        get() = _reminderMenuCount

    private val _reminderMenuFrequency = MutableLiveData<Int>()
    val reminderMenuFrequency: LiveData<Int>
        get() = _reminderMenuFrequency

    private val _reminderMenuList = MutableLiveData<List<ReminderMenuItem>>()
    val reminderMenuList: LiveData<List<ReminderMenuItem>>
        get() = _reminderMenuList

    private val _reminderMenuMessage = MutableLiveData<String>()
    val reminderMenuMessage: LiveData<String>
        get() = _reminderMenuMessage

    private val _errorInputMenu = MutableLiveData<Boolean>()
    val errorInputMenu: LiveData<Boolean>
        get() = _errorInputMenu

    private val _reminderBody = MutableLiveData<Boolean>()
    val reminderBody: LiveData<Boolean>
        get() = _reminderBody

    private val _reminderRemind = MutableLiveData<Boolean>()
    val reminderRemind: LiveData<Boolean>
        get() = _reminderRemind

    /***
     * Phone number block
     */


    private val _phoneNumberItem = MutableLiveData<PhoneNumberItem>()
    val phoneNumberItem: LiveData<PhoneNumberItem>
        get() = _phoneNumberItem

    /***
     * Reminder block
     */
    private val _lastValueTemporary = MutableLiveData<Boolean>()
    val lastValueTemporary: LiveData<Boolean>
        get() = _lastValueTemporary

    fun chooseSelectContacts(selectedContacts: List<ContactItem>) {
        _selectedContacts.value = selectedContacts

    }

    fun changeEnableState(contactItem: ContactItem) {
        val newItem = contactItem.copy(item = !contactItem.item)
        editContactItem.invoke(newItem)

    }


    private val _deleteEmailReminder = MutableLiveData<EmailItem>()
    val deleteEmailReminder: LiveData<EmailItem>
        get() = _deleteEmailReminder

    private val _selectedPhoneNumbers = MutableLiveData<List<String>>()
    val selectedPhoneNumbers: LiveData<List<String>>
        get() = _selectedPhoneNumbers

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String>
        get() = _phoneNumber

    /**
     * Save Contacts Block
    //     */
    private val _saveContactsList = MutableLiveData<List<SaveContactItem>>()
    val saveContactsList: LiveData<List<SaveContactItem>>
        get() = _saveContactsList

    private val _viewHolder = MutableLiveData<List<SaveContactItem>>()
    val viewHolder: LiveData<List<SaveContactItem>>
        get() = _viewHolder

    private val _saveContactItem = MutableLiveData<SaveContactItem>()
    val selectedContactItem: LiveData<SaveContactItem>
        get() = _saveContactItem
//    var selectedContactList = GetContactListUseCase(conRepository).invoke()

    fun chooseContact(selectedContactItem: SaveContactItem) {
        _saveContactItem.value = selectedContactItem

    }

    fun chooseSelectedContacts(saveContactsList: List<SaveContactItem>) {
        _saveContactsList.value = saveContactsList

    }

    /**
     * end current block
     */

    /**
     * Date and Time methods block
     */
    fun saveDateItem(date: String) {
        _dateItem.value = date
    }


    fun saveTimeItem(time: String) {
        _timeItem.value = time
    }

    /**
     * end current block
     */


//    /**
//     * Email methods block
//     */
    fun chooseContact(contactItem: ContactItem) {
        _contactItem.value = contactItem

    }


    fun chooseSelectSmsList(selectedSms: List<SmsItem>) {
        _selectedSmsItem.postValue(selectedSms.toMutableList())

    }


    fun addEmailToList(emailData: EmailItem) {
        _emailItem.value = emailData
    }

    fun addPhoneNumberToList(phNumber: String) {
        _phoneNumber.value = phNumber
    }

    private var scope = CoroutineScope(Dispatchers.IO)


    /**
     * Sms methods block
     */
    fun getSmsListReminder(): LiveData<List<SmsItem>> {
        return getSmsListRep()
    }

    fun getSmsWithRemainderId(remId: Int): LiveData<List<SmsItem>> {
        return getSmsFromRemId(remId)
    }


    fun addSmsItem(
        smsId: Int,
        smsMessage: String,
        phoneNumber: String,
        contactName: String,
        contactAvatar: String,
        reminderItemId: Int,
    ) {
        val message = parseString(smsMessage)
        val number = parseString(phoneNumber)
        val fieldsValid = validateInputSms(message, number)
        if (fieldsValid) {
            scope.launch {
                val smsItem =
                    SmsItem(smsId, message, number, contactName, contactAvatar, reminderItemId)
                _smsItem.postValue(smsItem)
                addSmsItemRep(smsItem)
                finishWork()
            }
        }
    }

    fun deleteSmsReminder(smsItem: SmsItem) {
        scope.launch {
            deleteSmsRep(smsItem)
        }
    }

    fun deleteSmsFromRemainderId(remId: Int) {
        scope.launch {
            deleteSmsFromRemId(remId)
        }
    }

    fun deleteSaveContact(saveContact: SaveContactItem) {
        scope.launch {
            deleteSaveContactRep(saveContact)
        }
    }

    fun executeMethods(remId: Int) {
        scope.launch {
            getImageWithRemainderId(remId)
            delay(1000) // задержка в 1 секунду
            deleteImageFromRemId(remId)
        }
    }

    fun saveSmsMessage(smsMessage: String) {
        _smsMessage.value = smsMessage
    }

    /**
     * File methods block
     */
    fun getFileWithRemainderId(remId: Int): LiveData<List<FileItem>> {
        return getFileFromRemId(remId)

    }


    fun addFileItem(
        fileId: Int,
        fileName: String,
        filePath: String,
        remId: Int

    ) {
        scope.launch {
            val fileItem = FileItem(fileId, fileName, filePath, remId)
            _fileItem.postValue(fileItem)
            addFileItemRep(fileItem)
            finishWork()
        }
    }

    fun deleteFileFromInternalStorage(context: Context, melodyName: String): Boolean {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val melody = File(dir, melodyName)
        return melody.delete()
    }

    /**
     * Melody methods block
     */
    fun getMelodyWithRemainderId(remId: Int): LiveData<List<MelodyItem>> {
        return getMelodyFromRemId(remId)

    }


    fun addMelodyItem(
        melodyId: Int,
        melodyName: String,
        melodyPath: String,
        remId: Int

    ) {
        scope.launch {
            val melodyItem = MelodyItem(melodyId, melodyName, melodyPath, remId)
            _melodyItem.postValue(melodyItem)
            addMelodyItemRep(melodyItem)
            finishWork()
        }
    }

    fun deleteMelodyFromInternalStorage(context: Context, melodyName: String): Boolean {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val melody = File(dir, melodyName)
        return melody.delete()
    }

    /**
     * Image methods block
     */
    suspend fun getImageItemReminder(id: Int): ImageItem {
        return getImageItemRep(id)
    }

    fun getImageWithRemainderId(remId: Int): LiveData<List<ImageItem>> {
        return getImageFromRemId(remId)

    }


    fun addImageItem(
        imageId: Int,
        imageName: String,
        imagePath: String,
        remId: Int

    ) {
        scope.launch {
            val imageItem = ImageItem(imageId, imageName, imagePath, remId)
            _imageItem.postValue(imageItem)
            addImageItemRep(imageItem)
            finishWork()
        }
    }


    fun resizeImageWithAspectRatio(originalImage: Bitmap, newWidth: Int): Bitmap {
        val width = originalImage.width
        val height = originalImage.height
        val aspectRatio = height.toFloat() / width.toFloat()
        val newHeight = (newWidth * aspectRatio).toInt()
        return Bitmap.createScaledBitmap(originalImage, newWidth, newHeight, false)
    }


    fun validateImage(bitmap: Bitmap): Boolean {
        val maxSizeBytes = 1024 * 1024 * 2// 2MB
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val imageBytes = outputStream.toByteArray()
        return imageBytes.size <= maxSizeBytes
    }

    fun deleteImageReminder(imageItem: ImageItem) {
        scope.launch {
            deleteImageRep(imageItem.id)
        }
    }

    fun deleteImageFromRemainderId(remId: Int) {
        scope.launch {
            deleteImageFromRemId(remId)
        }
    }

    fun deleteFileFromRemainderId(remId: Int) {
        scope.launch {
            deleteFileFromRemId(remId)
        }
    }

    fun deleteMelodyFromRemainderId(remId: Int) {
        scope.launch {
            deleteMelodyFromRemId(remId)
        }
    }


    fun deleteImageFromInternalStorage(context: Context, fileName: String): Boolean {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(dir, fileName)
        return file.delete()
    }

    /***
     * Phone number block
     */
    fun getPhoneNumberWithRemainderId(remId: Int): LiveData<List<PhoneNumberItem>> {
        return getPhoneNumberFromRemId(remId)
    }


    fun addPhoneNumberItem(id: Int, phoneNumber: String, remId: Int) {
        val number = parseString(phoneNumber)
        val fieldsValid = validateInputPhone(phoneNumber)
        if (fieldsValid) {
            scope.launch {
                val phoneNumberItem = PhoneNumberItem(id, number, remId)
                _phoneNumberItem.postValue(phoneNumberItem)
                addPhoneNumberItemRep(phoneNumberItem)
                finishWork()
            }
        }
    }

    fun deletePhoneNumberReminder(smsItem: PhoneNumberItem) {
        scope.launch {
            deletePhoneNumberRep(smsItem)
        }
    }

    fun deletePhoneNumberFromRemainderId(remId: Int) {
        scope.launch {
            deletePhoneNumberFromRemId(remId)
        }
    }


    /**
     * end current block
     */


    /**
     * ReminderMenu methods block
     */
//    fun getReminderMenuListReminder(): LiveData<List<ReminderMenuItem>> {
//        return getReminderMenuListRep()
//    }

//    fun getReminderMenuWithRemainderId(remId: Int): LiveData<List<ReminderMenuItem>> {
//        return getReminderMenuFromRemId(remId)
//    }


//    fun addReminderMenuItem(
//        id: Int,
//        reminderMenuId: Int,
//        reminderMenuFrequency: Int,
//        reminderMenuRepeatCount: Int,
//        reminderMenuTimeOut: Int
//
//    ) {
//
//        val fieldsValid = validateReminderMenu(
//            reminderMenuTimeOut,
//            reminderMenuFrequency,
//            reminderMenuRepeatCount
//        )
//        if (fieldsValid) {
//            scope.launch {
//                val reminderMenuItem = ReminderMenuItem(
//                    id = UNDEFINED_ID,
//                    remId = reminderMenuId,
//                    menuRepeatFrequency = reminderMenuFrequency,
//                    menuTimeout = reminderMenuTimeOut,
//                    menuRepeatCount = reminderMenuRepeatCount
//                )
//                _reminderMenuItem.postValue(reminderMenuItem)
//                addReminderMenuItemRep(reminderMenuItem)
////                finishReminderRemind()
////                finishWork()
//            }
//        }
//    }

//    fun deleteReminderMenuReminder(reminderMenuItem: ReminderMenuItem) {
//        scope.launch {
//            deleteReminderMenuRep(reminderMenuItem)
//        }
//    }
//
//    fun deleteReminderMenuFromRemainderId(remId: Int) {
//        scope.launch {
//            deleteReminderMenuFromRemId(remId)
//        }
//    }

    fun saveReminderMenuMessage(reminderMenuMessage: String) {
        _reminderMenuMessage.value = reminderMenuMessage
    }

    /**
     * end current block
     */


    /**
     * SaveContact methods block
     */

    fun getSaveContactListReminder(): LiveData<List<SaveContactItem>> {
        return getSaveContactListRep()
    }

    fun getSaveContactWithRemainderId(remId: Int): LiveData<List<SaveContactItem>> {
        return getSaveContactFromRemId(remId)
    }

    fun addSaveContactItem(saveContactItem: SaveContactItem) {
        scope.launch {
            _saveContactItem.postValue(saveContactItem)
            addSaveContactItemRep(saveContactItem)
            finishWork()
        }
    }

    fun deleteSaveContactReminder(saveContactItem: SaveContactItem) {
        scope.launch {
            deleteSaveContactRep(saveContactItem)
        }
    }

    fun deleteSaveContactFromRemainderId(remId: Int) {
        scope.launch {
            deleteSaveContactFromRemId(remId)
        }
    }

    /**
     * end current block
     */


    /**
     * Email methods block
     */

    fun getEmailListReminder(): LiveData<List<EmailItem>> {
        return getEmailListRep()
    }


    fun getEmailWithRemainderId(remId: Int): LiveData<List<EmailItem>> {
        return getEmailFromRemId(remId)

    }

    fun addEmailItem(
        emailId: Int,
        emailMessage: String,
        emailAddress: String,
        contactName: String,
        contactAvatar: String,
        remId: Int
    ) {
        val message = parseString(emailMessage)
        val address = parseString(emailAddress)
        val fieldsValid = validateInputEmail(message, address)
        if (fieldsValid) {
            scope.launch {
                val emailItem =
                    EmailItem(emailId, message, address, contactName, contactAvatar, remId)
                _emailItem.postValue(emailItem)
                addEmailItemRep(emailItem)
                finishWork()
            }
        }
    }

    fun deleteEmailReminder(emailItem: EmailItem) {
        scope.launch {
            deleteEmailRep(emailItem)
        }
    }

    fun deleteEmailFromRemainderId(remId: Int) {
        scope.launch {
            deleteEmailFromRemId(remId)
        }
    }

    fun bindCurrentSmsToReminder(remId: Int) {
        scope.launch {
            bindCurrentSmsToRem(remId)
        }
    }

    fun bindCurrentPhoneNumberToReminder(remId: Int) {
        scope.launch {
            bindCurrentPhoneNumberToRem(remId)
        }
    }

    fun bindCurrentSaveContactToReminder(remId: Int) {
        scope.launch {
            bindCurrentSaveContactToRem(remId)
        }
    }

    fun bindCurrentEmailsToReminder(remId: Int) {
        scope.launch {
            bindCurrentEmailsToRem(remId)
        }
    }

//    fun bindCurrentReminderMenuToReminder(remId: Int) {
//        scope.launch {
//            bindCurrentReminderMenuToRem(remId)
//        }
//    }

    fun saveEmailMessage(emailMessage: String) {
        _emailMessage.value = emailMessage
    }

    /**
     * end current block
     */


    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

    fun reminderMenuTimeOut(menuTimeOut: Int) {
        _reminderMenuTimeOut.value = menuTimeOut
    }

    fun reminderMenuCount(menuCount: Int) {
        _reminderMenuCount.value = menuCount
    }

    fun reminderMenuFrequency(menuFrequency: Int) {
        _reminderMenuFrequency.value = menuFrequency
    }

    fun addEmailData(emailData: EmailItem) {
        _emailItem.value = emailData
    }

    fun addPhoneNumber(phNumber: String) {
        _phoneNumber.value = phNumber
    }

    val errorInputString: LiveData<Boolean>
        get() = _errorInputString

    val errorInputNumber: LiveData<Boolean>
        get() = _errorInputNumber

    val errorInputBody: LiveData<Boolean>
        get() = _errorInputBody

    val errorInputAdd: LiveData<Boolean>
        get() = _errorInputAdd

    val errorInputDateRemaining: LiveData<Boolean>
        get() = _errorInputDateRemaining

    val errorInputTimeRemaining: LiveData<Boolean>
        get() = _errorInputTimeRemaining

    val errorInputTimePeriod: LiveData<Boolean>
        get() = _errorInputTimePeriod

    val errorInputRemainingCount: LiveData<Boolean>
        get() = _errorInputRemainingCount

    val errorInputContacts: LiveData<Boolean>
        get() = _errorInputContacts

    val errorInputMails: LiveData<Boolean>
        get() = _errorInputMails

    val errorInputTelNumbers: LiveData<Boolean>
        get() = _errorInputTelNumbers

    val errorInputSms: LiveData<Boolean>
        get() = _errorInputSms

    val errorInputImage: LiveData<Boolean>
        get() = _errorInputImage

    val errorInputFile: LiveData<Boolean>
        get() = _errorInputFile

    val errorInputState: LiveData<Boolean>
        get() = _errorInputState


    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    /**
     * ReminderItem
     */

    fun changeEnableState(reminderItem: ReminderItem) {
        viewModelScope.launch {
            val newItem = reminderItem.copy(state = !reminderItem.state)
            editReminderItem(newItem)
        }
    }

    fun deleteReminderAll(reminderItem: ReminderItem) {
        viewModelScope.launch {
            deleteReminderItem(reminderItem)
            deleteEmailFromRemId(reminderItem.id)
            deleteSmsFromRemId(reminderItem.id)
            deleteSaveContactFromRemId(reminderItem.id)
            deletePhoneNumberFromRemId(reminderItem.id)
            deleteImageFromRemId(reminderItem.id)
        }
    }

    fun activeChangeStatusRemind(reminderId: Int, status: Boolean) {
        viewModelScope.launch {
            activeChangeStatus(reminderId, status)

        }
    }

    fun showInWidgetChangeStatusRemind(reminderId: Int, status: Boolean) {
        viewModelScope.launch {
            showInWidgetChangeStatus(reminderId, status)

        }
    }


    fun dateToISOFormat(date: String, time: String): LocalDateTime {
        var localDateTime = LocalDateTime.now()
        if (time.length == 5) {
        localDateTime = LocalDateTime.parse("${date}T$time:00")}
        if (time.length == 8) {
            localDateTime = LocalDateTime.parse("${date}T$time")}
        _reminderTime.value = localDateTime
        return localDateTime
    }


    fun getReminderViewItem(reminderId: Int) {
        viewModelScope.launch {
            _reminderItem.value = (getReminderItem(reminderId))
        }
    }


    fun getReminderViewItemTwo(reminderId: Int) {
        viewModelScope.launch {
            _reminderItemTwo.value = (getReminderItem(reminderId))
        }
    }


    fun getReminderViewItem(date: String): LiveData<List<ReminderItem>> {
//            viewModelScope.launch {
        _remindersAtDate = getRemindersAtDate(date) as MutableLiveData<List<ReminderItem>>
        return _remindersAtDate
    }


    fun getRemindersCountItem() {
        viewModelScope.launch {
            if (getRemindersCount == null) {
                _remindersCount.value = 0
            } else {
                _remindersCount.value = getRemindersCount()!!
            }
        }
    }


    fun addReminderItemWithTimeDate(
        inputBody: String,
//        inputAdd: String,
        inputTimeRemaining: String,
        inputDateRemaining: String,
        reminderMenuTimeOut: Int,
        reminderMenuFrequency: Int,
        reminderMenuRepeatCount: Int,
        addContacts: Boolean,
        addSms: Boolean,
        addEmails: Boolean,
        addPhoneNumbers: Boolean,
        temporary: Boolean,
        addImage: Boolean,
        addFile: Boolean,
        addMelody: Boolean,
        inputState: Boolean,
//        showInWidget: Boolean
    ) {

        var body = parseString(inputBody)
//        val contacts = parseString(inputContacts)
//        val mails = parseString(inputMails)
//        val telNumbers = parseString(inputTelNumbers)
//        val sms = parseString(inputSms)
//        val image = parseString(inputImage)
//        val file = parseString(inputFile)
//        val remainingCount = parseNumber(inputRemainingCount)

        if (validateInputReminder(
                reminderText = body,
                date = inputDateRemaining,
                time = inputTimeRemaining,
                timeout = reminderMenuTimeOut,
                frequency = reminderMenuFrequency,
                count = reminderMenuRepeatCount
            )
        ) {
            val reminderTime = dateToISOFormat(inputDateRemaining, inputTimeRemaining)
            val firstDateTimeReminding = reminderTime.minusMinutes(reminderMenuTimeOut.toLong())




            viewModelScope.launch {
                val reminderItem = ReminderItem(
                    reminderTime = reminderTime,
//                    dateTime= mDateTime,
                    body = inputBody,
//                    add = inputAdd,
                    timeRemaining = inputTimeRemaining,
                    dateRemaining = inputDateRemaining,
                    firstDateTimeReminding = firstDateTimeReminding,
                    menuRepeatFrequency = reminderMenuFrequency,
                    menuTimeout = reminderMenuTimeOut,
                    menuRepeatCount = reminderMenuRepeatCount,
                    contacts = addContacts,
                    phoneNumbers = addPhoneNumbers,
                    sms = addSms,
                    mails = addEmails,
                    image = addImage,
                    file = addFile,
                    melody = addMelody,
                    state = true,
                    temporary = temporary,
                    dateTime = LocalDateTime.now(),
                    showInWidget = true
                )
                addReminderItem(reminderItem)
//                bindCurrentEmailsToLastReminder()
                finishWork()
            }
        }
    }

    fun editReminderItemTwo(
        inputBody: String,
//        inputAdd: String,
        inputTimeRemaining: String,
        inputDateRemaining: String,
        reminderMenuTimeOut: Int,
        reminderMenuFrequency: Int,
        reminderMenuRepeatCount: Int,
        addContacts: Boolean,
        addSms: Boolean,
        addEmails: Boolean,
        addPhoneNumbers: Boolean,
        temporary: Boolean,
        addImage: Boolean,
        addFile: Boolean,
        addMelody: Boolean,
        inputState: Boolean,
        showInWidget: Boolean
    ) {
        var body = parseString(inputBody)
//        val contacts = parseString(inputContacts)
//        val mails = parseString(inputMails)
//        val telNumbers = parseString(inputTelNumbers)
//        val sms = parseString(inputSms)
//        val image = parseString(inputImage)
//        val file = parseString(inputFile)
//        val remainingCount = parseNumber(inputRemainingCount)

        if (validateInputReminder(
                reminderText = body,
                date = inputDateRemaining,
                time = inputTimeRemaining,
                timeout = reminderMenuTimeOut,
                frequency = reminderMenuFrequency,
                count = reminderMenuRepeatCount
            )
        ) {
            val reminderTime = dateToISOFormat(inputDateRemaining, inputTimeRemaining)
            val firstDateTimeReminding = reminderTime.minusMinutes(reminderMenuTimeOut.toLong())

            _reminderItemTwo.value?.let {
                viewModelScope.launch {


//                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
//                    val mDateTime = formatter.parse(Instant.now().toString())
                    val item = it.copy(
                        reminderTime = reminderTime,
                        body = inputBody,
//                    add = inputAdd,
                        timeRemaining = inputTimeRemaining,
                        dateRemaining = inputDateRemaining,
                        firstDateTimeReminding = firstDateTimeReminding,
                        menuRepeatFrequency = reminderMenuFrequency,
                        menuTimeout = reminderMenuTimeOut,
                        menuRepeatCount = reminderMenuRepeatCount,
                        contacts = addContacts,
                        phoneNumbers = addPhoneNumbers,
                        sms = addSms,
                        mails = addEmails,
                        image = addImage,
                        file = addFile,
                        melody = addMelody,
                        state = true,
                        temporary = temporary,
                        dateTime = LocalDateTime.now(),
                        showInWidget = showInWidget

                    )
                    editReminderItem(item)
//                    finishReminderBody()
                    finishWork()
                }
            }

        }
    }

    fun editReminderItem(
        inputBody: String,
//        inputAdd: String,
        inputTimeRemaining: String,
        inputDateRemaining: String,
        reminderMenuTimeOut: Int,
        reminderMenuFrequency: Int,
        reminderMenuRepeatCount: Int,
        addContacts: Boolean,
        addSms: Boolean,
        addEmails: Boolean,
        addPhoneNumbers: Boolean,
        temporary: Boolean,
        addImage: Boolean,
        addFile: Boolean,
        addMelody: Boolean,
        inputState: Boolean,
        showInWidget: Boolean
    ) {
        var body = parseString(inputBody)
//        val contacts = parseString(inputContacts)
//        val mails = parseString(inputMails)
//        val telNumbers = parseString(inputTelNumbers)
//        val sms = parseString(inputSms)
//        val image = parseString(inputImage)
//        val file = parseString(inputFile)
//        val remainingCount = parseNumber(inputRemainingCount)

        if (validateInputReminder(
                reminderText = body,
                date = inputDateRemaining,
                time = inputTimeRemaining,
                timeout = reminderMenuTimeOut,
                frequency = reminderMenuFrequency,
                count = reminderMenuRepeatCount
            )
        ) {
            val reminderTime = dateToISOFormat(inputDateRemaining, inputTimeRemaining)
            val firstDateTimeReminding = reminderTime.minusMinutes(reminderMenuTimeOut.toLong())

            _reminderItem.value?.let {
                viewModelScope.launch {


//                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
//                    val mDateTime = formatter.parse(Instant.now().toString())
                    val item = it.copy(
                        reminderTime = reminderTime,
                        body = inputBody,
//                    add = inputAdd,
                        timeRemaining = inputTimeRemaining,
                        dateRemaining = inputDateRemaining,
                        firstDateTimeReminding = firstDateTimeReminding,
                        menuRepeatFrequency = reminderMenuFrequency,
                        menuTimeout = reminderMenuTimeOut,
                        menuRepeatCount = reminderMenuRepeatCount,
                        contacts = addContacts,
                        phoneNumbers = addPhoneNumbers,
                        sms = addSms,
                        mails = addEmails,
                        image = addImage,
                        file = addFile,
                        melody = addMelody,
                        state = true,
                        temporary = temporary,
                        dateTime = LocalDateTime.now(),
                        showInWidget = showInWidget

                    )
                    editReminderItem(item)
//                    finishReminderBody()
                    finishWork()
                }
            }

        }
    }

    fun tempLastReminderRecords() {
        viewModelScope.launch {
            if (getLastInsertId() != null) {
                _reminderItem.value = (getReminderItem(getLastInsertId()))
            } else {
                _reminderItem.value = null
            }
        }
    }

    fun tempLastReminderRecordsTwo() {
        viewModelScope.launch {
            if (getLastInsertId() != null) {
                _reminderItemTwo.value = (getReminderItem(getLastInsertId()))
            } else {
                _reminderItemTwo.value = null
            }
        }
    }

    fun saveLastReminderRecords() {
        viewModelScope.launch {
            if (getLastInsertId() != null) {
                _reminderItemTwo.value = (getReminderItem(getLastInsertId()))
            } else {
                _reminderItemTwo.value = null
            }
        }
    }

    fun lastRecordsTemporary() {
        viewModelScope.launch {
            if (getLastInsertIdWithTemporary() != null) {
                if (getLastInsertId() == getLastInsertIdWithTemporary()) {
                    _lastValueTemporary.value = true
                }
            }
        }
    }

    fun lastRecordsId() {
        viewModelScope.launch {
            if (getLastInsertId() != null) {
                _emptyReminderTable.value = getLastInsertId()!!
            } else {
                _emptyReminderTable.value = 0
            }
        }
    }

    fun bindCurrentImageToLastReminder() {
        viewModelScope.launch {
            bindCurrentImageToRem(getLastInsertId())
        }
    }

    fun bindCurrentFileToLastReminder() {
        viewModelScope.launch {
            bindCurrentFileToRem(getLastInsertId())
        }
    }

    fun bindCurrentMelodyToLastReminder() {
        viewModelScope.launch {
            bindCurrentMelodyToRem(getLastInsertId())
        }
    }

    fun bindCurrentEmailsToLastReminder() {
        viewModelScope.launch {
            bindCurrentEmailsToReminder(getLastInsertId())
        }
    }

    fun bindCurrentSmsToLastReminder() {
        viewModelScope.launch {
            bindCurrentSmsToReminder(getLastInsertId())
        }
    }

    fun bindCurrentPhoneNumberToLastReminder() {
        viewModelScope.launch {
            bindCurrentPhoneNumberToReminder(getLastInsertId())
        }
    }

    fun bindCurrentSaveContactToLastReminder() {
        viewModelScope.launch {
            bindCurrentSaveContactToReminder(getLastInsertId())
        }
    }

    fun bindCurrentEmailsToItemReminder(id: Int) {
        viewModelScope.launch {
            bindCurrentEmailsToReminder(id)
        }
    }

    fun bindCurrentSmsToItemReminder(id: Int) {
        viewModelScope.launch {
            bindCurrentSmsToReminder(id)
        }
    }

    fun bindCurrentPhoneNumberToItemReminder(id: Int) {
        viewModelScope.launch {
            bindCurrentPhoneNumberToReminder(id)
        }
    }

    fun bindCurrentSaveContactToItemReminder(id: Int) {
        viewModelScope.launch {
            bindCurrentSaveContactToReminder(id)
        }
    }

    fun editDateTimeFirstReminding(id: Int, dateTimeRem: String) {
        viewModelScope.launch {
            editDateTimeReminding(id, dateTimeRem)
        }
    }

    fun editDateAndTimeFirstReminding(
        id: Int,
        dateTimeRem: String,
        date: String,
        time: String,
        count: Int
    ) {
        viewModelScope.launch {
            editDateTimeDateTimeRemindingAndCount(id, dateTimeRem, date, time, count)
        }
    }

    fun editDateTimeFirstRemindingAndCount(id: Int, dateTimeRem: String, count: Int) {
        viewModelScope.launch {
            editDateTimeRemindingAndCount(id, dateTimeRem, count)
        }
    }


    /**
     * setting block
     */

    fun setLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        configuration.setLocales(LocaleList(locale))

        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }


    fun currentDateFormat(formatOfDate: String): String {
        val date = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern(formatOfDate)
        return date.format(formatter)
    }

    /***
     * parse block
     */


    private fun parseNumber(inputValue: String): Int {
        return try {
            inputValue.trim().toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }


    private fun parseString(inputString: String?): String {
        return inputString?.trim() ?: ""
    }

    /**
     * Validate block
     *
     */
    private fun validateString(text: String): Boolean {
        var result = true
        if (text.isBlank()) {
            _errorInputString.value = true
            result = false
        }
        return result
    }


    private fun validateInt(value: Int): Boolean {
        var result = true
        if (value < 0) {
            _errorInputNumber.value = true
            result = false
        }
        return result
    }


    private fun validateInput(
        body: String,
        remainingCount: Int,
        contacts: String,
        mails: String,
        telNumbers: String
    ): Boolean {
        var result = true
        if (!validateString(body)) _errorInputBody.value = true
        result = false
        if (!validateString(contacts)) _errorInputContacts.value = true
        result = false
        if (!validateEmail(mails)) _errorInputMail.value = true
        result = false
        if (!validateString(telNumbers)) _errorInputPhoneNumber.value = true
        result = false
        if (!validateInt(remainingCount)) _errorInputRemainingCount.value = true
        result = false
        return result
    }

    fun validateTextBody(reminderText: String): Boolean {
        var result = true
        if (!validateString(reminderText)) {
            _errorInputMessage.value = true
            result = false
        }
        return result
    }

    private fun validateInputReminder(
        reminderText: String,
        date: String,
        time: String,
        timeout: Int,
        frequency: Int,
        count: Int
    ): Boolean {
        var result = true
        resetErrorInputMessage()
        resetErrorInputDateRemaining()
        resetErrorInputTimeRemaining()
        _errorInputMenuTimeout.value = false
        _errorInputMenuRepeatFrequency.value = false
        _errorInputMenuRepeatCount.value = false


        if (!validateDate(date)) {
            _errorInputDateRemaining.value = true
            result = false
        }
        if (!validateTime(time)) {
            _errorInputTimeRemaining.value = true
            result = false
        }
        if (!validateInt(timeout)) {
            _errorInputMenuTimeout.value = true
            result = false
        }
        if (!validateInt(frequency)) {
            _errorInputMenuRepeatFrequency.value = true
            result = false
        }
        if (!validateInt(count)) {
            _errorInputMenuRepeatCount.value = true
            result = false
        }
        return result
    }


    private fun validateInputSms(
        message: String,
        number: String
    ): Boolean {
        var result = true

        if (!validateString(message)) {
            _errorInputMessage.value = true
            result = false
        }
        if (!validatePhone(number)) {
            _errorInputPhoneNumber.value = true
            result = false
        }
        return result
    }


    private fun validateInputPhone(
        number: String
    ): Boolean {
        var result = true

        if (!validatePhone(number)) {
            _errorInputPhoneNumber.value = true
            result = false
        }
        return result
    }


    private fun validateInputEmail(
        message: String,
        mails: String
    ): Boolean {
        var result = true
//rr
        if (mails.split(",").isNotEmpty()) {
            mails.split(",").forEach {
                if (!validateEmail(parseString(it))) {
                    _errorInputMail.value = true
                    result = false
                    return result
                }
            }
        }
        return result
    }


    private fun validateEmail(email: String): Boolean {
        val mailPattern = Regex(EMAIL_ADDRESS_PATTERN)
        return mailPattern.matches(email)

    }

    private fun validatePhone(phone: String): Boolean {
        val phonePattern = Regex(PHONE_NUMBER_PATTERN)
        return phonePattern.matches(phone)
    }

    private fun validateTime(time: String): Boolean {
        val timePattern = Regex(REMINDER_TIME_PATTERN)
        return timePattern.matches(time)
    }

    private fun validateDate(date: String): Boolean {
        val datePattern = Regex(REMINDER_DATE_PATTERN)
        return datePattern.matches(date)
    }

    fun resetErrorInputMessage() {
        _errorInputMessage.value = false
    }

    fun resetErrorInputPhone() {
        _errorInputPhoneNumber.value = false
    }


    fun resetErrorInputBody() {
        _errorInputBody.value = false
    }


    fun resetErrorInputContacts() {
        _errorInputContacts.value = false
    }

    fun resetErrorInputMails() {
        _errorInputMails.value = false
    }

    private fun resetErrorInputDateRemaining() {
        _errorInputDateRemaining.value = false
    }

    fun resetErrorInputTimeRemaining() {
        _errorInputTimeRemaining.value = false
    }

    private fun finishReminderBody() {
        _reminderBody.postValue(true)
    }

    private fun finishReminderRemind() {
        _reminderRemind.postValue(true)
    }

    private fun finishWork() {
//        if (_reminderRemind.value == true && _reminderBody.value == true) {
        _shouldCloseScreen.postValue(Unit)
    }
//    }

    /**
     * Workers blocks
     */

    fun getReminderDateInfo() = checkDate.invoke()

    init {
        checkDate.invoke()
    }

    private fun log(message: String) {
    }

    private val reminderListDao = AppDatabase.getInstance(application).reminderListDao()

    private val mapper = ReminderListMapper()

    fun queryToDB(): List<ReminderItemDbModel>? {
        return reminderListDao.timeActivateRemind(
            LocalDateTime.now().toString(),
            LocalDateTime.now().plusMinutes(15).toString()
        )
    }

    fun queryToDBReminder(): List<ReminderItemDbModel>? {

        var queryRes = reminderListDao.timeActivateRemind(
            LocalDateTime.now().toString(),
            LocalDateTime.now().plusHours(5).toString()
        )

        if (!queryRes.isNullOrEmpty()) {
            return queryRes

        } else {
            log("null!!!")
            return null
        }

    }

}






