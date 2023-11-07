package io.billie.payments.db

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.validation.constraints.Positive

data class InvoiceEntity(
        val id: UUID,
        val buyerId: String,
        val merchantId: UUID,
        val orderId: String,
        val totalInvoicePrice: PriceValue,
        val createdDate: LocalDateTime,
        val updatedDate: LocalDateTime,
)
data class PriceValue(
        @Schema(example = "999.0")
        @field:Positive
        val amount: BigDecimal,
        @Schema(example = "USD")
        val currency: String
)