package solutions.mobiledev.reminder.data.reminder.addMelody

import solutions.mobiledev.reminder.domain.entity.MelodyItem

class MelodyListMapper {

    fun mapEntityToDbModel(melodyItem: MelodyItem): MelodyItemDbModel = MelodyItemDbModel(
        id = melodyItem.id,
        melody_path = melodyItem.melodyPath,
        rem_id = melodyItem.remId,
        melody_name = melodyItem.melodyName,
    )

    fun mapDbModelToEntity(melodyItemDbModel: MelodyItemDbModel) = MelodyItem(
        id = melodyItemDbModel.id,
        melodyPath = melodyItemDbModel.melody_path,
        remId = melodyItemDbModel.rem_id,
        melodyName = melodyItemDbModel.melody_name,

    )

    fun mapListDbModelToListEntity(list: List<MelodyItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }

}