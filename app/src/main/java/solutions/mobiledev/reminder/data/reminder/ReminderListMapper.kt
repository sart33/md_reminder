package solutions.mobiledev.reminder.data.reminder

import solutions.mobiledev.reminder.domain.entity.FullReminderItem
import solutions.mobiledev.reminder.domain.entity.ReminderItem
import java.time.LocalDateTime

class ReminderListMapper {

    fun mapEntityToDbModel(reminderItem: ReminderItem) =

        ReminderItemDbModel(
            id = reminderItem.id,
            reminder_timestamp = reminderItem.reminderTime.toString(),
            date_time = reminderItem.dateTime.toString(),
            body = reminderItem.body,
            //            add = reminderItem.add,
            first_date_time_reminding = reminderItem.firstDateTimeReminding.toString(),
            date_remaining = reminderItem.dateRemaining,
            time_remaining = reminderItem.timeRemaining,
            menu_timeout = reminderItem.menuTimeout,
            menu_repeat_frequency = reminderItem.menuRepeatFrequency,
            menu_repeat_count = reminderItem.menuRepeatCount,
            contacts = reminderItem.contacts,
            mails = reminderItem.mails,
            sms = reminderItem.sms,
            image = reminderItem.image,
            file = reminderItem.file,
            melody = reminderItem.melody,
            state = reminderItem.state,
            phone_numbers = reminderItem.phoneNumbers,
            temporary_remind = reminderItem.temporary,
            show_in_widget = reminderItem.showInWidget
        )


    fun mapDbModelToEntity(reminderItemDbModel: ReminderItemDbModel): ReminderItem {
//        val dateTime = mapDateTimeModelToEntity(reminderItemDbModel.dateTime)
//        val dateRemaining = mapDateModelToEntity(reminderItemDbModel.dateRemaining)
//        val timeRemaining = mapTimeModelToEntity(reminderItemDbModel.timeRemaining)
        return ReminderItem(
            id = reminderItemDbModel.id,
            dateTime = LocalDateTime.parse(reminderItemDbModel.date_time),
            firstDateTimeReminding = LocalDateTime.parse(reminderItemDbModel.first_date_time_reminding),
            reminderTime = LocalDateTime.parse(reminderItemDbModel.reminder_timestamp),
            body = reminderItemDbModel.body,
//            add = reminderItemDbModel.add,
            dateRemaining = reminderItemDbModel.date_remaining,
            timeRemaining = reminderItemDbModel.time_remaining,
            menuTimeout = reminderItemDbModel.menu_timeout,
            menuRepeatFrequency = reminderItemDbModel.menu_repeat_frequency,
            menuRepeatCount = reminderItemDbModel.menu_repeat_count,
            contacts = reminderItemDbModel.contacts,
            mails = reminderItemDbModel.mails,
            phoneNumbers = reminderItemDbModel.phone_numbers,
            sms = reminderItemDbModel.sms,
            image = reminderItemDbModel.image,
            file = reminderItemDbModel.file,
            melody = reminderItemDbModel.melody,
            temporary = reminderItemDbModel.temporary_remind,
            state = reminderItemDbModel.state,
            showInWidget = reminderItemDbModel.show_in_widget
        )
    }

    //    private fun mapEntityToDomainModel(reminderItemWithMelodyDBModel: ReminderItemWithMelodyDBModel): ReminderItemWithMelody {
//        return ReminderItemWithMelody(
//            reminderItem(id = reminderItemWithMelodyDBModel.id,
//            melodyItem = reminderItemWithMelodyDBModel.melodyItemDbModel
//
//            // Продолжайте преобразование остальных полей, как требуется
//        )
//    }
    fun mapDbModelWithMelodyToEntity(reminderItemWithMelodyDBModel: ReminderItemWithMelodyDBModel): FullReminderItem {
        return FullReminderItem(
            id = reminderItemWithMelodyDBModel.id,
            dateTime = LocalDateTime.parse(reminderItemWithMelodyDBModel.date_time),
            firstDateTimeReminding = LocalDateTime.parse(reminderItemWithMelodyDBModel.first_date_time_reminding),
            reminderTime = LocalDateTime.parse(reminderItemWithMelodyDBModel.reminder_timestamp),
            body = reminderItemWithMelodyDBModel.body,
//            add = reminderItemWithMelodyDBModel.add,
            dateRemaining = reminderItemWithMelodyDBModel.date_remaining,
            timeRemaining = reminderItemWithMelodyDBModel.time_remaining,
            menuTimeout = reminderItemWithMelodyDBModel.menu_timeout,
            menuRepeatFrequency = reminderItemWithMelodyDBModel.menu_repeat_frequency,
            menuRepeatCount = reminderItemWithMelodyDBModel.menu_repeat_count,
            contacts = reminderItemWithMelodyDBModel.contacts,
            mails = reminderItemWithMelodyDBModel.mails,
            phoneNumbers = reminderItemWithMelodyDBModel.phone_numbers,
            sms = reminderItemWithMelodyDBModel.sms,
            image = reminderItemWithMelodyDBModel.image,
            file = reminderItemWithMelodyDBModel.file,
            melody = reminderItemWithMelodyDBModel.melody,
            temporary = reminderItemWithMelodyDBModel.temporary_remind,
            state = reminderItemWithMelodyDBModel.state,
            showInWidget = reminderItemWithMelodyDBModel.show_in_widget,
            melodyName = reminderItemWithMelodyDBModel.melody_name,
            melodyPath = reminderItemWithMelodyDBModel.melody_path,
            imageName = reminderItemWithMelodyDBModel.image_name,
            imagePath = reminderItemWithMelodyDBModel.image_path,

            )
    }


    fun mapFullDbModelToEntity(fullReminderItemDbModel: FullReminderItemDbModel): FullReminderItem {
        return FullReminderItem(
            id = fullReminderItemDbModel.id,
            dateTime = LocalDateTime.parse(fullReminderItemDbModel.date_time),
            firstDateTimeReminding = LocalDateTime.parse(fullReminderItemDbModel.first_date_time_reminding),
            reminderTime = LocalDateTime.parse(fullReminderItemDbModel.reminder_timestamp),
            body = fullReminderItemDbModel.body,
//            add = fullReminderItemDbModel.add,
            dateRemaining = fullReminderItemDbModel.date_remaining,
            timeRemaining = fullReminderItemDbModel.time_remaining,
            menuTimeout = fullReminderItemDbModel.menu_timeout,
            menuRepeatFrequency = fullReminderItemDbModel.menu_repeat_frequency,
            menuRepeatCount = fullReminderItemDbModel.menu_repeat_count,
            contacts = fullReminderItemDbModel.contacts,
            mails = fullReminderItemDbModel.mails,
            phoneNumbers = fullReminderItemDbModel.phone_numbers,
            sms = fullReminderItemDbModel.sms,
            image = fullReminderItemDbModel.image,
            file = fullReminderItemDbModel.file,
            melody = fullReminderItemDbModel.melody,
            temporary = fullReminderItemDbModel.temporary_remind,
            melodyName = fullReminderItemDbModel.melody_name,
            melodyPath = fullReminderItemDbModel.melody_path,
            imageName = fullReminderItemDbModel.melody_name,
            imagePath = fullReminderItemDbModel.melody_path,
            state = fullReminderItemDbModel.state,
            showInWidget = fullReminderItemDbModel.show_in_widget
        )
    }

    fun mapListDbModelToListEntity(list: List<ReminderItemDbModel>): List<ReminderItem> = list.map {
        mapDbModelToEntity(it)
    }

}
//    fun mapDateTimeModelToEntity(string: String): Date {
//
////        try {
//        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
//        val mDateTime = formatter.parse(string)
//        Log.e("mDateTime", mDateTime.toString())
//        return mDateTime
////        } catch (e: Exception) {
////            Log.e("mDate", e.toString())
////            null
////        }
//    }
//
//
//    fun mapDateModelToEntity(string: String): Date {
////        return try {
//        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//        val mDate = formatter.parse(string)
//        Log.e("mDate", mDate.toString())
//        return mDate
////        } catch (e: Exception) {
////            Log.e("mDate", e.toString())
////            null
////        }
//    }


//    fun mapTimeModelToEntity(string: String): Date {
////        return try {
//        val formatter = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
//        val mTime = formatter.parse(string)
//        Log.e("mTime", mTime.toString())
//        return mTime
////        } catch (e: Exception) {
////            Log.e("mTime", e.toString())
////            null
////        }
//    }
//}