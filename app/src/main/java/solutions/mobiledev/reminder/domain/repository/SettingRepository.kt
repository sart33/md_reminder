package solutions.mobiledev.reminder.domain.repository

import androidx.lifecycle.LiveData
import solutions.mobiledev.reminder.domain.entity.SettingItem

interface SettingRepository {

    fun getSettingList(): LiveData<List<SettingItem>>

    suspend fun addSettingItem(settingItem: SettingItem)

    suspend fun editSettingItem(settingItem: SettingItem)

    suspend fun deleteSettingItem(name: String)

    suspend fun getSettingItem(settingName: String): SettingItem

    suspend fun getDateFormatItem(): SettingItem
    suspend fun getLocaleItem(): SettingItem
}