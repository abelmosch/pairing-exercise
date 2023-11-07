package io.billie.payments.resources

import io.billie.payments.models.PaymentReceiptRequest
import io.billie.payments.services.PaymentsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid


@RestController
@RequestMapping("payments")
class PaymentsResource(val service: PaymentsService) {
    @PostMapping
    fun createPayment(@Valid @RequestBody request: PaymentReceiptRequest) = service.createPayment(request)
}
