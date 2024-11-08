package io.billie.organizations.controllers

import io.billie.organizations.dto.PaymentReceiptRequest
import io.billie.organizations.dto.PaymentReceiptResponse
import io.billie.organizations.services.PaymentsService
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid


@RestController
@RequestMapping("payments")
class PaymentsController(private val service: PaymentsService) {
    @PostMapping
    @ApiResponses(
            value = [
                ApiResponse(
                        responseCode = "200",
                        description = "Accepted the new payment receipt",
                        content = [
                            (Content(
                                    mediaType = "application/json",
                                    array = (ArraySchema(schema = Schema(implementation = PaymentReceiptResponse::class)))
                            ))]
                ),
                ApiResponse(responseCode = "400", description = "Bad request", content = [Content()]),
                ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content()])]
    )

    fun createPaymentReceipt(@Valid @RequestBody request: PaymentReceiptRequest) = service.createPaymentReceipt(request)
}
