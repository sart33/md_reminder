package solutions.mobiledev.reminder.data.reminder

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReminderListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addReminderItem(reminderItemDbModel: ReminderItemDbModel)

    @Query("DELETE FROM reminder_items WHERE id=:reminderItemId")
    suspend fun deleteReminderItem(reminderItemId: Int)

    @Query("SELECT * FROM reminder_items WHERE id=:reminderItemId LIMIT 1")
    suspend fun getReminderItem(reminderItemId: Int): ReminderItemDbModel

    @Query(
        "SELECT reminder_items.id, reminder_items.reminder_timestamp, reminder_items.date_time, " +
                "reminder_items.first_date_time_reminding, reminder_items.body, " +
                "reminder_items.temporary_remind, reminder_items.date_remaining, reminder_items.time_remaining, " +
                "reminder_items.menu_repeat_frequency, reminder_items.menu_repeat_count, " +
                "reminder_items.menu_timeout, reminder_items.contacts, reminder_items.mails, " +
                "reminder_items.phone_numbers, reminder_items.sms, reminder_items.image, " +
                "reminder_items.file, reminder_items.melody, reminder_items.state, " +
                "reminder_items.show_in_widget, melody_items.melody_name, melody_items.melody_path," +
                "image_items.image_name, image_items.image_path FROM reminder_items" +
                " LEFT JOIN melody_items ON reminder_items.id = melody_items.rem_id" +
                " LEFT JOIN image_items ON reminder_items.id = image_items.rem_id WHERE " +
                "reminder_items.id=:reminderItemId LIMIT 1"
    )
    suspend fun getReminderItemWithAll(reminderItemId: Int): ReminderItemWithMelodyDBModel

    @Query("SELECT * FROM reminder_items ORDER BY date_remaining ASC, time_remaining ASC")
    fun getReminderList(): LiveData<List<ReminderItemDbModel>>

    @Query(
        "SELECT * FROM reminder_items WHERE first_date_time_reminding >=:dateNow ORDER BY " +
                "date_remaining ASC, time_remaining ASC LIMIT 100"
    )
    fun getSomeNextReminders(dateNow: String): List<ReminderItemDbModel>

    @Query("SELECT COUNT(*) FROM reminder_items WHERE date_remaining =:dateNow")
    fun getRemindersCountAtDate(dateNow: String): Int

    @Query("SELECT COUNT(*) FROM reminder_items")
    suspend fun getRemindersCount(): Int

    @Query(
        "SELECT date_remaining, count(*) FROM reminder_items WHERE date_remaining >=:dateStart " +
                "AND date_remaining <=:dateEnd GROUP BY date_remaining ORDER BY date_remaining ASC"
    )
    suspend fun getRemindersCountAtDateDiapason(
        dateStart: String,
        dateEnd: String
    ): List<DateDiapason>

    @Query("SELECT * FROM reminder_items WHERE date_remaining =:date ORDER BY time_remaining ASC")
    fun getRemindersAtDate(date: String): LiveData<List<ReminderItemDbModel>>

    @Query("SELECT MAX(id) FROM reminder_items")
    suspend fun lastInsertId(): Int

    @Query("SELECT MAX(id) FROM reminder_items WHERE temporary_remind = 0")
    suspend fun lastInsertIdWithTemporary(): Int?

    @Query("UPDATE reminder_items SET state= :status WHERE id= :reminderId")
    suspend fun activeChangeStatus(reminderId: Int, status: Boolean)

    @Query("UPDATE reminder_items SET first_date_time_reminding= :firstDateTimeReminding " +
            "WHERE id= :reminderId")
    suspend fun editDateTimeReminding(reminderId: Int, firstDateTimeReminding: String)

    @Query("UPDATE reminder_items SET first_date_time_reminding= :firstDateTimeReminding, " +
            "menu_repeat_count= :count WHERE id= :reminderId")
    suspend fun editDateTimeRemindingAndCount(
        reminderId: Int,
        firstDateTimeReminding: String,
        count: Int
    )

    @Query("UPDATE reminder_items SET first_date_time_reminding= :firstDateTimeReminding, " +
            "date_remaining= :date, time_remaining= :time, menu_repeat_count= :count WHERE id= :reminderId")
    suspend fun editDateTimeDateTimeRemindingAndCount(
        reminderId: Int,
        firstDateTimeReminding: String,
        date: String,
        time: String,
        count: Int
    )

    @Query("UPDATE reminder_items SET show_in_widget= :status WHERE id= :reminderId")
    suspend fun showInWidgetChangeStatus(reminderId: Int, status: Boolean)

    @Query("SELECT * FROM reminder_items WHERE first_date_time_reminding > :thisMoment AND " +
            "first_date_time_reminding < :thisMomentPlusFifteen ORDER BY first_date_time_reminding ASC")
    fun timeActivateRemind(
        thisMoment: String,
        thisMomentPlusFifteen: String
    ): List<ReminderItemDbModel>?


}