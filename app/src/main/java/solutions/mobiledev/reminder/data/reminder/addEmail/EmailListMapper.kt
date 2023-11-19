package solutions.mobiledev.reminder.data.reminder.addEmail

import solutions.mobiledev.reminder.domain.entity.EmailItem

class EmailListMapper {

    fun mapEntityToDbModel(emailItem: EmailItem): EmailItemDbModel = EmailItemDbModel(
        id = emailItem.id,
        message = emailItem.emailMessage,
        emails = emailItem.emailAddress,
        remId = emailItem.remId,
        name = emailItem.name,
        avatar = emailItem.avatar
    )

    fun mapDbModelToEntity(emailItemDbModel: EmailItemDbModel) = EmailItem(
        id = emailItemDbModel.id,
        emailMessage = emailItemDbModel.message,
        emailAddress = emailItemDbModel.emails,
        remId = emailItemDbModel.remId,
        name = emailItemDbModel.name,
        avatar = emailItemDbModel.avatar
    )

    fun mapListDbModelToListEntity(list: List<EmailItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }

}