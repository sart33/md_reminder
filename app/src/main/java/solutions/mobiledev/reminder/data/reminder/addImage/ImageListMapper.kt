package solutions.mobiledev.reminder.data.reminder.addImage

import solutions.mobiledev.reminder.domain.entity.ImageItem

class ImageListMapper {

    fun mapEntityToDbModel(imageItem: ImageItem): ImageItemDbModel = ImageItemDbModel(
        id = imageItem.id,
        image_path = imageItem.imagePath,
        rem_id = imageItem.remId,
        image_name = imageItem.imageName,
    )

    fun mapDbModelToEntity(imageItemDbModel: ImageItemDbModel) = ImageItem(
        id = imageItemDbModel.id,
        imagePath = imageItemDbModel.image_path,
        remId = imageItemDbModel.rem_id,
        imageName = imageItemDbModel.image_name,

    )

    fun mapListDbModelToListEntity(list: List<ImageItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }

}