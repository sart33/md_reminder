package solutions.mobiledev.reminder.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import solutions.mobiledev.reminder.data.reminder.FullReminderItemDbModel
import solutions.mobiledev.reminder.data.reminder.ReminderItemDbModel
import solutions.mobiledev.reminder.data.reminder.ReminderItemWithMelodyDBModel
import solutions.mobiledev.reminder.data.reminder.ReminderListDao
import solutions.mobiledev.reminder.data.reminder.addEmail.EmailItemDbModel
import solutions.mobiledev.reminder.data.reminder.addEmail.EmailListDao
import solutions.mobiledev.reminder.data.reminder.addFile.FileItemDbModel
import solutions.mobiledev.reminder.data.reminder.addFile.FileListDao
import solutions.mobiledev.reminder.data.reminder.addImage.ImageItemDbModel
import solutions.mobiledev.reminder.data.reminder.addImage.ImageListDao
import solutions.mobiledev.reminder.data.reminder.addMelody.MelodyItemDbModel
import solutions.mobiledev.reminder.data.reminder.addMelody.MelodyListDao
import solutions.mobiledev.reminder.data.reminder.addPhoneNumber.PhoneNumberItemDbModel
import solutions.mobiledev.reminder.data.reminder.addPhoneNumber.PhoneNumberListDao
import solutions.mobiledev.reminder.data.reminder.addSaveContact.SaveContactItemDbModel
import solutions.mobiledev.reminder.data.reminder.addSaveContact.SaveContactListDao
import solutions.mobiledev.reminder.data.reminder.addSms.SmsItemDbModel
import solutions.mobiledev.reminder.data.reminder.addSms.SmsListDao


@Database(
    entities = [SmsItemDbModel::class, PhoneNumberItemDbModel::class, ReminderItemDbModel::class, ReminderItemWithMelodyDBModel::class,
        EmailItemDbModel::class, SaveContactItemDbModel::class, ImageItemDbModel::class,
        FileItemDbModel::class, MelodyItemDbModel::class],
    version = 13,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderListDao(): ReminderListDao
    abstract fun smsListDao(): SmsListDao
    abstract fun phoneNumberListDao(): PhoneNumberListDao
    abstract fun saveContactListDao(): SaveContactListDao
    abstract fun emailListDao(): EmailListDao
    abstract fun imageListDao(): ImageListDao
    abstract fun fileListDao(): FileListDao
    abstract fun melodyListDao(): MelodyListDao

    companion object {

        private var INSTANCE: AppDatabase? = null
        private val LOCK = Any()
        private const val DB_NAME = "reminder_v2.db"

        //        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(application: Application): AppDatabase {
            INSTANCE?.let {
                return it
            }
            synchronized(LOCK) {
                INSTANCE?.let {
                    return it
                }
                val db = Room.databaseBuilder(
                    application,
                    AppDatabase::class.java,
                    DB_NAME
                )
//            .allowMainThreadQueries()
//                    .addMigration(migration4to5)
//                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = db
                return db
            }
        }
    }
}
//
//val migration4to5 = object : Migration(4, 5) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        // Здесь вы должны определить необходимые действия миграции для перехода с версии 4 на версию 5
//        // Например, можно выполнить SQL-запросы для обновления схемы базы данных или конвертирования данных
//        // database.execSQL("ALTER TABLE ...")
//    }
//}

