package ru.skillbranch

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.skillbranch.data.local.entities.CharacterFull
import ru.skillbranch.data.local.entities.CharacterItem
import ru.skillbranch.data.remote.res.CharacterRes
import ru.skillbranch.data.remote.res.HouseRes
import ru.skillbranch.repositories.RootRepository


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private val stubHouseStark = HouseRes(
        url = "https://www.anapioficeandfire.com/api/houses/362",
        name = "House Stark of Winterfell",
        region = "The North",
        coatOfArms = "A running grey direwolf, on an ice-white field",
        words = "Winter is Coming",
        titles = listOf(
            "King in the North",
            "Lord of Winterfell",
            "Warden of the North",
            "King of the Trident"
        ),
        seats = listOf("Scattered (formerly Winterfell)"),
        currentLord = "",
        heir = "",
        overlord = "https://www.anapioficeandfire.com/api/houses/16",
        founded = "Age of Heroes",
        founder = "https://www.anapioficeandfire.com/api/characters/209",
        diedOut = "",
        ancestralWeapons = listOf("Ice"),
        cadetBranches = emptyList(),
        swornMembers = listOf(
            "https://www.anapioficeandfire.com/api/characters/583",
            "https://www.anapioficeandfire.com/api/characters/1650"
        )
    )

    private val stubHouseTargaryen = HouseRes(
        url = "https://www.anapioficeandfire.com/api/houses/378",
        name = "House Targaryen of King's Landing",
        region = "The Crownlands",
        coatOfArms = "Sable, a dragon thrice-headed gules",
        words = "Fire and Blood",
        titles = listOf(
            "King of the Andals, the Rhoynar and the First Men",
            "Lord of the Seven Kingdoms",
            "Prince of Summerhall"
        ),
        seats = listOf(
            "Red Keep (formerly)",
            "Summerhall (formerly)"
        ),
        currentLord = "https://www.anapioficeandfire.com/api/characters/1303",
        heir = "",
        overlord = "",
        founded = "House Targaryen: >114 BCHouse Targaryen of King's Landing:1 AC",
        founder = "",
        diedOut = "",
        ancestralWeapons = listOf(
            "Blackfyre",
            "Dark Sister"
        ),
        cadetBranches = emptyList(),
        swornMembers = listOf("https://www.anapioficeandfire.com/api/characters/867")
    )

    private val stubCharacterJonSnow = CharacterRes(
        url = "https://www.anapioficeandfire.com/api/characters/583",
        name = "Jon Snow",
        gender = "Male",
        culture = "Northmen",
        born = "In 283 AC",
        died = "",
        titles = listOf("Lord Commander of the Night's Watch"),
        aliases = listOf(
            "Lord Snow",
            "Ned Stark's Bastard",
            "The Snow of Winterfell",
            "The Crow-Come-Over",
            "The 998th Lord Commander of the Night's Watch",
            "The Bastard of Winterfell",
            "The Black Bastard of the Wall",
            "Lord Crow"
        ),
        father = "https://www.anapioficeandfire.com/api/characters/867",
        mother = "https://www.anapioficeandfire.com/api/characters/1650",
        allegiances = emptyList(),
        spouse = "",
        books = emptyList(),
        povBooks = emptyList(),
        tvSeries = emptyList(),
        playedBy = listOf("Kit Harington")
    )

    private val stubCharacterRhaegar = CharacterRes(
        url = "https://www.anapioficeandfire.com/api/characters/867",
        name = "Rhaegar Targaryen",
        gender = "Male",
        culture = "Valyrian",
        born = "In 259 AC, at Summerhall",
        died = "In 283 AC, at the Trident",
        titles = listOf(
            "Prince of Dragonstone",
            "Ser"
        ),
        aliases = listOf(
            "Silver Prince",
            "The Dragon Prince",
            "The Last Dragon"
        ),
        father = "",
        mother = "",
        spouse = "https://www.anapioficeandfire.com/api/characters/361",
        allegiances = emptyList(),
        books = emptyList(),
        povBooks = emptyList(),
        tvSeries = emptyList(),
        playedBy = emptyList()
    )

    private val stubCharacterLyanna = CharacterRes(
        url = "https://www.anapioficeandfire.com/api/characters/1650",
        name = "Lyanna Stark",
        gender = "Female",
        culture = "Northmen",
        born = "In 266 AC or 267 AC",
        died = "In 283 AC, at Tower of Joy",
        titles = emptyList(),
        aliases = listOf(
            "The She-Wolf",
            "The Wolf Maid",
            "Lya"
        ),
        father = "",
        mother = "",
        spouse = "",
        allegiances = emptyList(),
        books = emptyList(),
        povBooks = emptyList(),
        tvSeries = emptyList(),
        playedBy = emptyList()
    )

    @Test
    fun insert_house_and_drop_db() {
        //Запись в базу
        val lock0 = Object()
        RootRepository.insertHouses(listOf(stubHouseStark)) {
            synchronized(lock0) { lock0.notify() }
        }
        synchronized(lock0) { lock0.wait() }

        //Обновление не нужно
        val lock1 = Object()
        var needResult: Boolean? = null
        RootRepository.isNeedUpdate {
            needResult = it
            synchronized(lock1) { lock1.notify() }
        }
        synchronized(lock1) { lock1.wait() }
        assertEquals(false, needResult)

        //Дроп базы
        val lock = Object()
        RootRepository.dropDb {
            synchronized(lock) { lock.notify() }
        }
        synchronized(lock) { lock.wait() }

        //Обновление нужно
        val lock3 = Object()
        var needResult2: Boolean? = null
        RootRepository.isNeedUpdate {
            needResult2 = it
            synchronized(lock3) { lock3.notify() }
        }
        synchronized(lock3) { lock3.wait() }
        assertEquals(true, needResult2)
    }

    @Test
    fun insert_characters_and_find() {
        ///Дроп базы
        val lock = Object()
        RootRepository.dropDb {
            synchronized(lock) { lock.notify() }
        }
        synchronized(lock) { lock.wait() }

        //Запись домов
        val lock0 = Object()
        RootRepository.insertHouses(listOf(stubHouseStark, stubHouseTargaryen)) {
            synchronized(lock0) { lock0.notify() }
        }
        synchronized(lock0) { lock0.wait() }

        //Запись персонажей
        val lock1 = Object()
        val characters = listOf(
            stubCharacterJonSnow.apply { houseId = "Stark" },
            stubCharacterLyanna.apply { houseId = "Stark" },
            stubCharacterRhaegar.apply { houseId = "Targaryen" }
        )
        RootRepository.insertCharacters(characters) {
            synchronized(lock1) { lock1.notify() }
        }
        synchronized(lock1) { lock1.wait() }

        val lock2 = Object()
        var actualCharacters: List<CharacterItem>? = null
        RootRepository.findCharactersByHouseName("Stark") {
            actualCharacters = it
            synchronized(lock2) { lock2.notify() }
        }
        synchronized(lock2) { lock2.wait() }

        assertEquals(stubCharacterJonSnow.name, actualCharacters?.first()?.name)
        assertEquals(stubCharacterJonSnow.aliases, actualCharacters?.first()?.aliases)

        assertEquals(stubCharacterLyanna.name, actualCharacters?.last()?.name)
        assertEquals(stubCharacterLyanna.aliases, actualCharacters?.last()?.aliases)
    }

    @Test
    fun insert_characters_and_full() {
        ///Дроп базы
        val lock = Object()
        RootRepository.dropDb {
            synchronized(lock) { lock.notify() }
        }
        synchronized(lock) { lock.wait() }

        //Запись домов
        val lock0 = Object()
        RootRepository.insertHouses(listOf(stubHouseStark, stubHouseTargaryen)) {
            synchronized(lock0) { lock0.notify() }
        }
        synchronized(lock0) { lock0.wait() }

        //Запись персонажей
        val lock1 = Object()
        val characters = listOf(
            stubCharacterJonSnow.apply { houseId = "Stark" },
            stubCharacterLyanna.apply { houseId = "Stark" },
            stubCharacterRhaegar.apply { houseId = "Targaryen" }
        )
        RootRepository.insertCharacters(characters) {
            synchronized(lock1) { lock1.notify() }
        }
        synchronized(lock1) { lock1.wait() }

        val lock2 = Object()
        var actualCharacter: CharacterFull? = null
        RootRepository.findCharacterFullById("583") {
            actualCharacter = it
            synchronized(lock2) { lock2.notify() }
        }
        synchronized(lock2) { lock2.wait() }

        assertEquals(stubCharacterJonSnow.name, actualCharacter?.name)
        assertEquals(stubCharacterJonSnow.aliases, actualCharacter?.aliases)

        assertEquals(stubCharacterRhaegar.name, actualCharacter?.father?.name)
        assertEquals("Targaryen", actualCharacter?.father?.house)

        assertEquals(stubCharacterLyanna.name, actualCharacter?.mother?.name)
        assertEquals("Stark", actualCharacter?.mother?.house)
    }

    @Test
    fun get_all_houses() {
        //Запись персонажей
        val lock1 = Object()
        var actualHouses: List<HouseRes>? = null
        RootRepository.getAllHouses {
            actualHouses = it
            synchronized(lock1) { lock1.notify() }
        }
        synchronized(lock1) { lock1.wait() }

        val actualCharacters = actualHouses?.fold(mutableListOf<String>()) { acc, houses ->
            acc.also { it.addAll(houses.swornMembers) }
        }

        assertEquals(1567, actualCharacters?.size)
    }

    @Test
    fun get_need_houses() {
        //Запись персонажей
        val lock1 = Object()
        var actualHouses: List<HouseRes>? = null
        RootRepository.getNeedHouses(
            "House Greyjoy of Pyke",
            "House Tyrell of Highgarden"
        ) {
            actualHouses = it
            synchronized(lock1) { lock1.notify() }
        }
        synchronized(lock1) { lock1.wait() }

        val actualCharacters = actualHouses?.fold(mutableListOf<String>()) { acc, houses ->
            acc.also { it.addAll(houses.swornMembers) }
        }

        assertEquals(86, actualCharacters?.size)
    }

    @Test
    fun get_need_houses_with_characters() {
        val lock1 = Object()
        var actualHouses: List<Pair<HouseRes, List<CharacterRes>>>? = null
        RootRepository.getNeedHouseWithCharacters(
            "House Greyjoy of Pyke"
        ) {
            actualHouses = it
            synchronized(lock1) { lock1.notify() }
        }
        synchronized(lock1) { lock1.wait() }

        assertEquals("We Do Not Sow", actualHouses?.first()?.first?.words)
        assertEquals(42, actualHouses?.first()?.second?.size)
    }
}
