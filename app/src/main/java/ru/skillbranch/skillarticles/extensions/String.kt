package ru.skillbranch.skillarticles.extensions

/**
 * @author mmikhailov on 2020-02-15.
 */
fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
    if (this == null) return emptyList()

    val result = mutableListOf<Int>()

    var position = this.indexOf(substr, 0, ignoreCase)
    while (position != -1) {
        result.add(position)
        position = this.indexOf(substr, 0, ignoreCase)
    }

    return result
}