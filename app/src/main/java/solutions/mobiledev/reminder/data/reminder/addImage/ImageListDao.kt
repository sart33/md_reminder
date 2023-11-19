package solutions.mobiledev.reminder.data.reminder.addImage

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addImageItem(imageItemDbModel: ImageItemDbModel)

    @Query("DELETE FROM image_items WHERE id=:imageItemId")
    suspend fun deleteImageItem(imageItemId: Int)

    @Query("DELETE FROM image_items WHERE rem_id=:remId")
    suspend fun deleteImageFromRemId(remId: Int)

    @Query("SELECT * FROM image_items WHERE id=:imageItemId LIMIT 1")
    suspend fun getImageItem(imageItemId: Int) : ImageItemDbModel

    @Query("SELECT * FROM image_items WHERE rem_id=:remId LIMIT 100")
    fun getImageWithRemId(remId: Int) : LiveData<List<ImageItemDbModel>>

    @Query("UPDATE image_items SET rem_id = :remId WHERE image_items.rem_id = 0")
    suspend fun bindCurrentImagesToReminder(remId: Int)

    @Query("SELECT * FROM image_items")
    fun getImageList() : LiveData<List<ImageItemDbModel>>
}