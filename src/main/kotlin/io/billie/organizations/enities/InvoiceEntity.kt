package io.billie.organizations.enities

import java.time.LocalDateTime
import java.util.*

data class InvoiceEntity(
        val id: UUID,
        val buyerId: String,
        val merchantId: UUID,
        val orderId: String,
        val totalInvoicePrice: PriceValue,
        val createdDate: LocalDateTime,
        val updatedDate: LocalDateTime,
)
