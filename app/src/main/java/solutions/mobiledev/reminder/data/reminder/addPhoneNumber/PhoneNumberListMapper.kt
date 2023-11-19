package solutions.mobiledev.reminder.data.reminder.addPhoneNumber

import solutions.mobiledev.reminder.domain.entity.PhoneNumberItem

class PhoneNumberListMapper {

    fun mapEntityToDbModel(phoneNumberItem: PhoneNumberItem): PhoneNumberItemDbModel = PhoneNumberItemDbModel(
        id = phoneNumberItem.id,
        number = phoneNumberItem.number,
        remId = phoneNumberItem.remId
    )

    fun mapDbModelToEntity(phoneNumberItemDbModel: PhoneNumberItemDbModel) = PhoneNumberItem(
        id = phoneNumberItemDbModel.id,
        number = phoneNumberItemDbModel.number,
        remId = phoneNumberItemDbModel.remId
    )

    fun mapListDbModelToListEntity(list: List<PhoneNumberItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}