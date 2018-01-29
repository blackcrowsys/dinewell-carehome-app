package com.blackcrowsys.exceptions

class InvalidUrlException : Exception()

class EmptyUsernamePasswordException : Exception()

class AppException(override val message: String) : Exception(message)