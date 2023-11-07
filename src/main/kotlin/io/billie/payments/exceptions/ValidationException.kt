package io.billie.payments.exceptions

class ValidationException(message: String, override val cause: Throwable? = null) : RuntimeException(message)
