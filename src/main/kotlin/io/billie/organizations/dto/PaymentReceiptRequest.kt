package io.billie.organizations.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.billie.organizations.enities.PaymentType
import io.billie.organizations.enities.PriceValue
import java.time.LocalDateTime
import java.util.*

data class PaymentReceiptRequest(
        @JsonProperty("merchant_id") val merchantId: UUID,
        @JsonProperty("amount") val amount: PriceValue,
        @JsonProperty("invoice_id") val invoiceId: UUID,
        @JsonProperty("payment_type") val paymentType: PaymentType,
        @JsonProperty("created_date") val createdDate: LocalDateTime
)