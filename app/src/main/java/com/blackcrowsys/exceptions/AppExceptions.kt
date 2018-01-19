package com.blackcrowsys.exceptions

class InvalidUrlException : Exception()

class AppException(override val message: String) : Exception(message)