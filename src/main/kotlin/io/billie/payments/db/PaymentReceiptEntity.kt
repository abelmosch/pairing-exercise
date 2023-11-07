package io.billie.payments.db

import java.time.LocalDateTime
import java.util.*

data class PaymentReceiptEntity(val id: UUID = UUID.randomUUID(),
                                val amount: PriceValue,
                                val invoiceId: UUID,
                                val paymentType: PaymentType,
                                val createdDate: LocalDateTime)

enum class PaymentType {
    BUYER,
    MERCHANT
}