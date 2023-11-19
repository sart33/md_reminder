package solutions.mobiledev.reminder.data.reminder.addEmail

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import solutions.mobiledev.reminder.data.reminder.ReminderItemDbModel

@Dao
interface EmailListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEmailItem(emailItemDbModel: EmailItemDbModel)

    @Query("DELETE FROM email_items WHERE id=:emailItemId")
    suspend fun deleteEmailItem(emailItemId: Int)

    @Query("DELETE FROM email_items WHERE remId=:remId")
    suspend fun deleteEmailFromRemId(remId: Int)

    @Query("SELECT * FROM email_items WHERE id=:emailItemId LIMIT 1")
    suspend fun getEmailItem(emailItemId: Int) : EmailItemDbModel

    @Query("SELECT * FROM email_items WHERE remId=:remId LIMIT 10")
    fun getEmailWithRemId(remId: Int) : LiveData<List<EmailItemDbModel>>

    @Query("UPDATE email_items SET remId = :remId WHERE email_items.remId = 0")
    suspend fun bindCurrentEmailsToReminder(remId: Int)

    @Query("SELECT * FROM email_items")
    fun getEmailList() : LiveData<List<EmailItemDbModel>>
}