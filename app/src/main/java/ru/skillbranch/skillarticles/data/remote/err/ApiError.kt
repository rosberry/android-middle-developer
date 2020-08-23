package ru.skillbranch.skillarticles.data.remote.err

import java.io.IOException

/**
 * @author mmikhailov on 23.08.2020.
 */
sealed class ApiError(override val message: String) : IOException(message) {
    class BadRequest(message: String?) : ApiError(message ?: "Bad request")
    class Unauthorized(message: String?) : ApiError(message ?: "Unauthorized")
    class Forbidden(message: String?) : ApiError(message ?: "Forbidden")
    class NotFound(message: String?) : ApiError(message ?: "NotFound")
    class InternalServerError(message: String?) : ApiError(message ?: "InternalServerError")
    class UnknownError(message: String?) : ApiError(message ?: "UnknownError")
}

class ErrorBody(val message: String)