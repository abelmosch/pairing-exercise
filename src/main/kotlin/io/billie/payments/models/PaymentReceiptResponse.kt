package io.billie.payments.models

import io.billie.payments.db.PaymentType
import io.billie.payments.db.PriceValue
import java.time.LocalDateTime
import java.util.*

data class PaymentReceiptResponse(val id: UUID,
                                  val amount: PriceValue,
                                  val invoiceId: UUID,
                                  val paymentType: PaymentType,
                                  val createdDate: LocalDateTime)