package ru.skillbranch.repositories

import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.skillbranch.data.api.IceAndFireApi
import ru.skillbranch.data.converters.GameOfThronesConverter
import ru.skillbranch.data.db.AppDatabase
import ru.skillbranch.data.local.entities.CharacterFull
import ru.skillbranch.data.local.entities.CharacterItem
import ru.skillbranch.data.local.entities.House
import ru.skillbranch.data.remote.res.CharacterRes
import ru.skillbranch.data.remote.res.HouseRes
import ru.skillbranch.di.root.NetworkModule
import ru.skillbranch.presentation.EntityConverter


/**
 * @author neestell on 2019-12-07.
 */
object RootRepository {
    val api: IceAndFireApi = NetworkModule.api

    /**
     * Получение данных о всех домах из сети
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun getAllHouses(result: (houses: List<HouseRes>) -> Unit) {
        val maxPages = 10
        val houses: MutableList<HouseRes> = mutableListOf()
        for (page in 1..maxPages) {
            withContext(Dispatchers.IO) {
                val pageHouses = api.getHouses(page = page)
                houses.addAll(pageHouses)
            }
        }
        result.invoke(houses)
    }

    /**
     * Получение данных о требуемых домах по их полным именам из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun getNeedHouses(vararg houseNames: String, result: (houses: List<HouseRes>) -> Unit) {
        val houses: MutableList<HouseRes> = loadHouses(houseNames)

        result.invoke(houses)
    }

    private suspend fun loadHouses(houseNames: Array<out String>): MutableList<HouseRes> {
        val houses: MutableList<HouseRes> = mutableListOf()

        for (houseName in houseNames) {
            withContext(Dispatchers.IO) {
                val house = api.getHouse(houseName)
                    .first()
                houses.add(house)
            }
        }
        return houses
    }

    /**
     * Получение данных о требуемых домах по их полным именам и персонажах в каждом из домов из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о доме и персонажей в нем (Дом - Список Персонажей в нем)
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun getNeedHouseWithCharacters(
            vararg houseNames: String,
            result: (houses: List<Pair<HouseRes, List<CharacterRes>>>) -> Unit
    ) {
        val housesCharacters: MutableList<Pair<HouseRes, List<CharacterRes>>> = mutableListOf()
        val houses = loadHouses(houseNames)
        withContext(Dispatchers.IO) {
            houses.forEach { house ->
                val characters: MutableList<CharacterRes> = mutableListOf()

                house.swornMembers.forEach { url ->
                    val character = api.getCharacter(url)
                    characters.add(character)
                }
                housesCharacters.add(Pair(house, characters))
            }
        }
        result.invoke(housesCharacters)
    }

    /**
     * Запись данных о домах в DB
     * @param houses - Список персонажей (модель HouseRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun insertHouses(context: Context, houses: List<HouseRes>, complete: () -> Unit) {
        val converter = GameOfThronesConverter()
        val db = AppDatabase.getInstance(context)
        val listOfHouses = mutableListOf<House>()
        withContext(Dispatchers.IO) {
            houses.forEach { rawHouse ->
                listOfHouses.add(converter.rawToHouse(rawHouse))
            }
            val count = db.houseDao()
                .insertAll(listOfHouses)
            Log.d("qwqw", "Count inserts $count")
        }
        complete.invoke()
    }

    /**
     * Запись данных о пересонажах в DB
     * @param Characters - Список персонажей (модель CharacterRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun insertCharacters(context: Context, characters: List<CharacterRes>, complete: () -> Unit) {
        val converter = GameOfThronesConverter()

        withContext(Dispatchers.IO) {
            val db = AppDatabase.getInstance(context)
            val persons = characters.map { raw -> converter.rawToCharacter(raw) }
                .toCollection(ArrayList())
            db.characterDao()
                .insertAll(persons)
        }
        complete.invoke()
    }

    /**
     * При вызове данного метода необходимо выполнить удаление всех записей в db
     * @param complete - колбек о завершении очистки db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun dropDb(context: Context, complete: () -> Unit) {
        val db = AppDatabase.getInstance(context)
        withContext(Dispatchers.IO) {
            db.clearAllTables()
        }
        complete.invoke()
    }

    /**
     * Поиск всех персонажей по имени дома, должен вернуть список краткой информации о персонажах
     * дома - смотри модель CharacterItem
     * @param name - краткое имя дома (его первычный ключ)
     * @param result - колбек содержащий в себе список краткой информации о персонажах дома
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun findCharactersByHouseName(context: Context, name: String, result: (characters: List<CharacterItem>) -> Unit) {
        val characters = mutableListOf<CharacterItem>()
        val converter = EntityConverter()
        val db = AppDatabase.getInstance(context)
        withContext(Dispatchers.IO) {
            val items = db.characterDao()
                .getCharactersByHouseId(name)
                .map { entity -> converter.characterToItem(entity) }
                .toCollection(ArrayList())
            characters.addAll(items)
        }

        result.invoke(characters)
    }

    /**
     * Поиск персонажа по его идентификатору, должен вернуть полную информацию о персонаже
     * и его родственных отношения - смотри модель CharacterFull
     * @param id - идентификатор персонажа
     * @param result - колбек содержащий в себе полную информацию о персонаже
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun findCharacterFullById(context: Context, id: String, result: (character: CharacterFull) -> Unit) {
        val converter = EntityConverter()
        val db = AppDatabase.getInstance(context)
        var fullCharacter: CharacterFull? = null
        val characterId = "https://www.anapioficeandfire.com/api/characters/${id}"
        withContext(Dispatchers.IO) {
            val house = db.houseDao()
                .getHouseForCharacterId(characterId)
            val character = db.characterDao()
                .getCharactersByCharacterId(characterId)
            val mother = db.characterDao()
                .getCharactersByCharacterId(character.mother)
            val father = db.characterDao()
                .getCharactersByCharacterId(character.father)

            fullCharacter = converter.characterToFullItem(character, house, mother, father)
        }

        result.invoke(fullCharacter!!)
    }

    /**
     * Метод возвращет true если в базе нет ни одной записи, иначе false
     * @param result - колбек о завершении очистки db
     */
    suspend fun isNeedUpdate(context: Context, result: (isNeed: Boolean) -> Unit) {
        val db = AppDatabase.getInstance(context)
        var isNeed = false
        withContext(Dispatchers.IO) {
            isNeed = db.houseDao()
                .getAll()
                .isEmpty()
        }
        result.invoke(isNeed)
    }

}