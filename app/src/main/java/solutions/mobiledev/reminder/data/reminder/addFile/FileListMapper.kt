package solutions.mobiledev.reminder.data.reminder.addFile

import solutions.mobiledev.reminder.domain.entity.FileItem
import solutions.mobiledev.reminder.domain.entity.ImageItem

class FileListMapper {

    fun mapEntityToDbModel(fileItem: FileItem): FileItemDbModel = FileItemDbModel(
        id = fileItem.id,
        path = fileItem.path,
        remId = fileItem.remId,
        name = fileItem.name,
    )

    fun mapDbModelToEntity(fileItemDbModel: FileItemDbModel) = FileItem(
        id = fileItemDbModel.id,
        path = fileItemDbModel.path,
        remId = fileItemDbModel.remId,
        name = fileItemDbModel.name,

    )

    fun mapListDbModelToListEntity(list: List<FileItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }

}