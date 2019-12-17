package ru.skillbranch.presentation

import ru.skillbranch.data.local.entities.Character
import ru.skillbranch.data.local.entities.CharacterFull
import ru.skillbranch.data.local.entities.CharacterItem
import ru.skillbranch.data.local.entities.House
import ru.skillbranch.data.local.entities.RelativeCharacter


/**
 * @author neestell on 2019-12-13.
 */
class EntityConverter {

    fun characterToItem(entity: Character): CharacterItem {
        return CharacterItem(id = entity.id,
                name = entity.name,
                aliases = entity.aliases,
                titles = entity.titles,
                house = entity.houseId)
    }

    fun characterToFullItem(character: Character, house: House, mother: Character, father: Character): CharacterFull {
        return CharacterFull(id = character.id,
                name = character.name,
                aliases = character.aliases,
                titles = character.titles,
                house = character.houseId,
                died = character.died,
                born = character.born,
                words = house.words,
                mother = RelativeCharacter(name = mother.name, id = mother.id, house = mother.houseId),
                father = RelativeCharacter(name = father.name, id = father.id, house = father.houseId))
    }
}