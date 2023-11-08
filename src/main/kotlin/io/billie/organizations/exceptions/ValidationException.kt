package io.billie.organizations.exceptions

class ValidationException(message: String, override val cause: Throwable? = null) : RuntimeException(message)
