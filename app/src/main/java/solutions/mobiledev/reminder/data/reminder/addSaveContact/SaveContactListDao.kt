package solutions.mobiledev.reminder.data.reminder.addSaveContact

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SaveContactListDao {
    /**
     * If add new Id with current id, it will be replaced. If this Id is absent, it will be added.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSaveContactItem(saveContactItemDbModel: SaveContactItemDbModel)

    @Query("DELETE FROM save_contact_items WHERE contactId=:saveContactItemId")
    suspend fun deleteSaveContactItem(saveContactItemId: Int)

    @Query("DELETE FROM save_contact_items WHERE remId=:remId")
    suspend fun deleteSaveContactFromRemId(remId: Int)

    @Query("SELECT * FROM save_contact_items WHERE id=:saveContactItemId LIMIT 1")
    suspend fun getSaveContactItem(saveContactItemId: Int): SaveContactItemDbModel

    @Query("SELECT * FROM save_contact_items")
    fun getSaveContactList(): LiveData<List<SaveContactItemDbModel>>

    @Query("UPDATE save_contact_items SET remId = :remId WHERE save_contact_items.remId = 0")
    suspend fun bindCurrentSaveContactToReminder(remId: Int)

    @Query("SELECT * FROM save_contact_items WHERE remId=:remId LIMIT 10")
    fun getSaveContactWithRemId(remId: Int) : LiveData<List<SaveContactItemDbModel>>
}