package solutions.mobiledev.reminder.data.reminder.addFile

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FileListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFileItem(fileItemDbModel: FileItemDbModel)

    @Query("DELETE FROM file_items WHERE id=:fileItemId")
    suspend fun deleteFileItem(fileItemId: Int)

    @Query("DELETE FROM file_items WHERE remId=:remId")
    suspend fun deleteFileFromRemId(remId: Int)

    @Query("SELECT * FROM file_items WHERE id=:fileItemId LIMIT 1")
    suspend fun getFileItem(fileItemId: Int) : FileItemDbModel

    @Query("SELECT * FROM file_items WHERE remId=:remId LIMIT 100")
    fun getFileWithRemId(remId: Int) : LiveData<List<FileItemDbModel>>

    @Query("UPDATE file_items SET remId = :remId WHERE file_items.remId = 0")
    suspend fun bindCurrentFilesToReminder(remId: Int)

    @Query("SELECT * FROM file_items")
    fun getFileList() : LiveData<List<FileItemDbModel>>
}