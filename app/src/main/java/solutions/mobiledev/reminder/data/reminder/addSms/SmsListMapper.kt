package solutions.mobiledev.reminder.data.reminder.addSms

import solutions.mobiledev.reminder.domain.entity.SmsItem

class SmsListMapper {

    fun mapEntityToDbModel(smsItem: SmsItem): SmsItemDbModel = SmsItemDbModel(
        id = smsItem.id,
        message = smsItem.smsMessage,
        number = smsItem.smsPhone,
        remId = smsItem.remId,
        name = smsItem.name,
        avatar = smsItem.avatar
    )

    fun mapDbModelToEntity(smsItemDbModel: SmsItemDbModel) = SmsItem(
        id = smsItemDbModel.id,
        smsMessage = smsItemDbModel.message,
        smsPhone = smsItemDbModel.number,
        remId = smsItemDbModel.remId,
        name = smsItemDbModel.name,
        avatar = smsItemDbModel.avatar
    )

    fun mapListDbModelToListEntity(list: List<SmsItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}