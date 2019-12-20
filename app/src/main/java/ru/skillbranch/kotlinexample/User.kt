package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom

class User private constructor(
        private val firstName: String,
        private val lastName: String?,
        email: String? = null,
        rawPhone: String? = null,
        meta: Map<String, Any>? = null
) {
    val userInfo: String

    private val fullName: String
        get() = listOfNotNull(firstName, lastName)
            .joinToString(" ")
            .capitalize()

    private val initials: String
        get() = listOfNotNull(firstName, lastName)
            .map {
                it.first()
                    .toUpperCase()
            }
            .joinToString(" ")

    private var phone: String? = null
        set(value) {
            field = value?.replace("[^+\\d]".toRegex(), " ")
        }

    private var _login: String? = null

    internal var login: String
        set(value) {
            _login = value?.toLowerCase()
        }
        get() = _login!!

    private var salt: String? = null
        get() = field ?: _salt

    private val _salt: String by lazy {
        ByteArray(16).also { SecureRandom().nextBytes(it) }
            .toString()
    }

    lateinit var passwordHash: String

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    var accessCode: String? = null

    // for email
    constructor(
            firstName: String,
            lastName: String?,
            email: String?,
            password: String
    ) : this(firstName, lastName, email = email, meta = mapOf("auth" to "password")) {
        println("Secondary mail constructor")
        passwordHash = encrypt(password)
    }

    // for phone
    constructor(
            firstName: String,
            lastName: String?,
            rawPhone: String
    ) : this(firstName, lastName, rawPhone = rawPhone, meta = mapOf("auth" to "sms")) {
        println("Secondary phone constructor")
        val code = generateAccessCode()
        passwordHash = encrypt(code)
        accessCode = code
        sendAccessCodeToUser(rawPhone, code)
    }

    // for csv
    constructor(
            firstName: String,
            lastName: String?,
            email: String?,
            rawPhone: String?,
            salt: String,
            hash: String
    ) : this(
            firstName,
            lastName,
            email = email,
            rawPhone = rawPhone,
            meta = mapOf("src" to "csv")
    ) {
        println("Third csv constructor")
        this.salt = salt
        passwordHash = hash
    }

    init {
        println("First init block, primary constructor was called")

        check(!firstName.isBlank()) { "First name must be not blank" }
        check(email.isNullOrBlank() || rawPhone.isNullOrBlank()) { "Email or phone must be not blank" }

        phone = rawPhone
        login = email ?: phone!!

        userInfo = """
            firstName: $firstName
            lastName: $lastName
            login: $login
            fullName: $fullName
            initials: $initials
            email: $email
            phone: $phone
            meta: $meta
            """.trimIndent()
    }

    fun checkPassword(pass: String) = encrypt(pass) == passwordHash

    fun changePasword(oldPass: String, newPass: String) {
        if (checkPassword(oldPass)) passwordHash = encrypt(newPass)
        else throw IllegalArgumentException("The entered password does not match the current password")
    }

    fun encrypt(password: String): String = salt.plus(password).md5()

    fun encryptWithSalt(saltOld: String, password: String): String = saltOld.plus(password).md5()

    fun generateAccessCode(): String {
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

        return StringBuilder().apply {
            repeat(6) {
                (possible.indices).random()
                    .also { index ->
                        append(possible[index])
                    }
            }
        }
            .toString()
    }

    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(toByteArray()) // 16byte
        val hexString = BigInteger(1, digest).toString(16)
        return hexString.padStart(32, '0')
    }

    private fun sendAccessCodeToUser(phone: String, code: String) {
        println("..... sending access code: $code on $phone")
    }

    companion object Factory {
        fun makeUser(
                fullName: String,
                email: String? = null,
                password: String? = null,
                phone: String? = null
        ): User {
            val (firstName, lastName) = fullName.fullNameToPair()

            return when {
                !phone.isNullOrBlank() -> User(firstName, lastName, phone)
                !email.isNullOrBlank() && !password.isNullOrBlank() -> User(
                        firstName,
                        lastName,
                        email,
                        password
                )
                else -> throw IllegalArgumentException("Email or phone must be not null or blank")
            }
        }

        fun importUser(
                fullName: String,
                email: String? = null,
                phone: String? = null,
                salt: String,
                hash: String
        ): User {
            val (firstName, lastName) = fullName.fullNameToPair()

            return User(firstName, lastName, email, phone, salt, hash)
        }

        private fun String.fullNameToPair(): Pair<String, String?> {
            return this.split(" ")
                .filter { it.isNotBlank() }
                .run {
                    when (size) {
                        1 -> first() to null
                        2 -> first() to last()
                        else -> throw IllegalArgumentException(
                                "Fullname must contain only first name and last name, " +
                                        "current split result $this"
                        )
                    }
                }
        }
    }
}