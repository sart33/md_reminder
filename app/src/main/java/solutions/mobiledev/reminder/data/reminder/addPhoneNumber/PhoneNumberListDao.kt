package solutions.mobiledev.reminder.data.reminder.addPhoneNumber

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhoneNumberListDao {
    /**
     * If add new Id with current id, it will be replaced. If this Id is absent, it will be added.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPhoneNumberItem(phoneNumberItemDbModel: PhoneNumberItemDbModel)


    @Query("UPDATE phone_number_items SET remId = :remId WHERE phone_number_items.remId = 0")
    suspend fun bindCurrentPhoneNumberToReminder(remId: Int)

    @Query("DELETE FROM phone_number_items WHERE id=:phoneNumberItemId")
    suspend fun deletePhoneNumberItem(phoneNumberItemId: Int)

    @Query("DELETE FROM phone_number_items WHERE remId=:remId")
    suspend fun deletePhoneNumberFromRemId(remId: Int)

    @Query("SELECT * FROM phone_number_items WHERE id=:phoneNumberItemId LIMIT 1")
    suspend fun getPhoneNumberItem(phoneNumberItemId: Int) : PhoneNumberItemDbModel

    @Query("SELECT * FROM phone_number_items WHERE remId=:remId LIMIT 10")
    fun getPhoneNumberWithRemId(remId: Int) : LiveData<List<PhoneNumberItemDbModel>>

    @Query("SELECT * FROM phone_number_items")
    fun getPhoneNumberList() : LiveData<List<PhoneNumberItemDbModel>>
}