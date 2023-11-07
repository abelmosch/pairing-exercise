package io.billie.payments.models

import io.billie.payments.db.PaymentType
import io.billie.payments.db.PriceValue
import java.time.LocalDateTime
import java.util.*

data class PaymentReceiptRequest(
        val merchantId: UUID,
        val invoiceId: UUID,
        val amount: PriceValue,
        val paymentType: PaymentType,
        val createdDate: LocalDateTime
)