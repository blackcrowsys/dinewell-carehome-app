package com.blackcrowsys.exceptions

class InvalidUrlException : Exception()

class EmptyUsernamePasswordException : Exception()

class UnableToEncryptTokenException : Exception()

class UnableToDecryptTokenException : Exception()

class AppException(override val message: String) : Exception(message) {

    var secondaryMessage: String? = null

    constructor(message: String, secondaryMessage: String?) : this(message) {
        this.secondaryMessage = secondaryMessage
    }
}
