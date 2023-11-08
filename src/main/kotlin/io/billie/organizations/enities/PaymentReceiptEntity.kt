package io.billie.organizations.enities

import java.time.LocalDateTime
import java.util.*

data class PaymentReceiptEntity(val id: UUID = UUID.randomUUID(),
                                val amount: PriceValue,
                                val invoiceId: UUID,
                                val paymentType: PaymentType,
                                val createdDate: LocalDateTime)

