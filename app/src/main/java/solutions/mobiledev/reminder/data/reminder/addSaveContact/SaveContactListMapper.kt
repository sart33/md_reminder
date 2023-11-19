package solutions.mobiledev.reminder.data.reminder.addSaveContact

import solutions.mobiledev.reminder.domain.entity.SaveContactItem

class SaveContactListMapper {
    fun mapEntityToDbModel(saveContactItem: SaveContactItem): SaveContactItemDbModel =
        SaveContactItemDbModel(
            id = UNDEFINED_ID,
            contactId = saveContactItem.id,
            name = saveContactItem.name,
            number = saveContactItem.number,
            avatar = saveContactItem.avatar,
            remId = saveContactItem.remId
        )

    fun mapDbModelToEntity(selectedContactItemDbModel: SaveContactItemDbModel) =
        SaveContactItem(
            id = selectedContactItemDbModel.contactId,
            name = selectedContactItemDbModel.name,
            number = selectedContactItemDbModel.number,
            avatar = selectedContactItemDbModel.avatar,
            remId = selectedContactItemDbModel.remId
        )

    fun mapListDbModelToListEntity(list: List<SaveContactItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
    companion object {
        const val UNDEFINED_ID = 0
    }
}