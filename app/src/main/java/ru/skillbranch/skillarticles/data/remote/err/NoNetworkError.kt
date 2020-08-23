package ru.skillbranch.skillarticles.data.remote.err

import java.io.IOException

/**
 * @author mmikhailov on 20.08.2020.
 */
class NoNetworkError(override val message: String = "Network not available") : IOException(message)