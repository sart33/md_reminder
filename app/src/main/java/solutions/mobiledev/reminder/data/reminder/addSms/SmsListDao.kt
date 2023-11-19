package solutions.mobiledev.reminder.data.reminder.addSms

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import solutions.mobiledev.reminder.data.reminder.addEmail.EmailItemDbModel

@Dao
interface SmsListDao {
    /**
     * If add new Id with current id, it will be replaced. If this Id is absent, it will be added.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSmsItem(smsItemDbModel: SmsItemDbModel)

    @Query("UPDATE sms_items SET remId = :remId WHERE sms_items.remId = 0")
    suspend fun bindCurrentSmsToReminder(remId: Int)

    @Query("DELETE FROM sms_items WHERE id=:smsItemId")
    suspend fun deleteSmsItem(smsItemId: Int)

    @Query("DELETE FROM sms_items WHERE remId=:remId")
    suspend fun deleteSmsFromRemId(remId: Int)

    @Query("SELECT * FROM sms_items WHERE id=:smsItemId LIMIT 1")
    suspend fun getSmsItem(smsItemId: Int) : SmsItemDbModel

    @Query("SELECT * FROM sms_items WHERE remId=:remId LIMIT 10")
    fun getSmsWithRemId(remId: Int) : LiveData<List<SmsItemDbModel>>

    @Query("SELECT * FROM sms_items")
    fun getSmsList() : LiveData<List<SmsItemDbModel>>
}