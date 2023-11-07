package io.billie.payments.exceptions

import io.billie.organisations.data.UnableToValidateOrganizationByCountryException
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalResourceExceptionHandler {
    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(UnableToValidateOrganizationByCountryException::class)
    fun exceptionHandling(ex: UnableToValidateOrganizationByCountryException): ResponseEntity<Any> {
        logger.error(ex) { "Error happened:${ex.message}" }
        return ResponseEntity.notFound().build()
    }
    @ExceptionHandler(ValidationException::class)
    fun exceptionHandling(ex: ValidationException): ResponseEntity<ErrorMessage> {
        logger.error(ex) { "Error happened:${ex.message}" }
        return ResponseEntity.badRequest().body(ErrorMessage(ex.message))
    }
}