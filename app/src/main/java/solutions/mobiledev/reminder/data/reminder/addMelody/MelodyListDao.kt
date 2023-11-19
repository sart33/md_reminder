package solutions.mobiledev.reminder.data.reminder.addMelody

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MelodyListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMelodyItem(melodyItemDbModel: MelodyItemDbModel)

    @Query("DELETE FROM melody_items WHERE id=:melodyItemId")
    suspend fun deleteMelodyItem(melodyItemId: Int)

    @Query("DELETE FROM melody_items WHERE rem_id=:remId")
    suspend fun deleteMelodyFromRemId(remId: Int)

    @Query("SELECT * FROM melody_items WHERE id=:melodyItemId LIMIT 1")
    suspend fun getMelodyItem(melodyItemId: Int) : MelodyItemDbModel

    @Query("SELECT * FROM melody_items WHERE rem_id=:remId LIMIT 1")
    suspend fun getMelodyUseRemId(remId: Int) : MelodyItemDbModel

    @Query("SELECT * FROM melody_items WHERE rem_id=:remId LIMIT 100")
    fun getMelodyWithRemId(remId: Int) : LiveData<List<MelodyItemDbModel>>

    @Query("UPDATE melody_items SET rem_id =:remId WHERE melody_items.rem_id = 0")
    suspend fun bindCurrentMelodiesToReminder(remId: Int)

    @Query("SELECT * FROM melody_items")
    fun getMelodyList() : LiveData<List<MelodyItemDbModel>>
}