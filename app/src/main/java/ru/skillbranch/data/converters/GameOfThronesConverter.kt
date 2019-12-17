package ru.skillbranch.data.converters

import ru.skillbranch.data.local.entities.Character
import ru.skillbranch.data.local.entities.House
import ru.skillbranch.data.remote.res.CharacterRes
import ru.skillbranch.data.remote.res.HouseRes


/**
 * @author neestell on 2019-12-08.
 */
class GameOfThronesConverter {
    //
    fun rawToHouse(raw: HouseRes): House {
        return House(
                id = raw.url,
                name = raw.name,
                region = raw.region,
                coatOfArms = raw.coatOfArms,
                ancestralWeapons = raw.ancestralWeapons,
                currentLord = raw.currentLord,
                diedOut = raw.diedOut,
                founded = raw.founded,
                founder = raw.founder,
                heir = raw.heir,
                overlord = raw.overlord,
                seats = raw.seats,
                titles = raw.titles,
                words = raw.words, swornMembers = raw.swornMembers)
    }

    fun rawToCharacter(raw: CharacterRes): Character {
        return Character(id = raw.url,
                name = raw.name,
                born = raw.born,
                culture = raw.culture,
                died = raw.died,
                father = raw.father,
                gender = raw.gender,
                houseId = raw.houseId,
                mother = raw.mother,
                titles = raw.titles,
                aliases = raw.aliases,
                spouse = raw.spouse)
    }
//
//    fun rawToBook(raw: BooksRes): Book {
//        return Book()
//    }
}