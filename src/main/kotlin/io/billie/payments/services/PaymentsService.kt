package io.billie.payments.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import io.billie.organisations.service.OrganisationService
import io.billie.payments.exceptions.ValidationException
import io.billie.payments.db.PaymentsRepository
import io.billie.payments.db.PaymentReceiptEntity
import io.billie.payments.models.PaymentReceiptRequest
import io.billie.payments.models.PaymentReceiptResponse
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentsService(
        private val objectMapper: ObjectMapper,
        private val paymentsRepository: PaymentsRepository,
        private val organisationService: OrganisationService) {

    private val logger = KotlinLogging.logger {}

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun createPayment(request: PaymentReceiptRequest): PaymentReceiptResponse {
        logger.debug { "start createPayment($request)" }
        if (organisationService.findOrganisations().none { it.id == request.merchantId }) {
            throw ValidationException("Cannot find organization! invoiceId: ${request.invoiceId} merchantId: ${request.merchantId}")
        }
        val paymentReceiptEntity = PaymentReceiptEntity(
                invoiceId = request.invoiceId,
                amount = request.amount,
                paymentType = request.paymentType,
                createdDate = request.createdDate
        )
        val invoice = paymentsRepository.getInvoiceById(paymentReceiptEntity.invoiceId)
        if (invoice.merchantId != request.merchantId) {
            throw ValidationException("MerchantId: ${request.merchantId} doesn't match for invoiceId: ${request.invoiceId}. Invoice has wrong merchantId: ${invoice.merchantId}")
        }
        val alreadyPaidPayments = paymentsRepository.getPaymentReceiptsByInvoiceIdWithLock(invoice.id)
        val totalPaidAmount = alreadyPaidPayments.sumOf { it.amount.amount }
        if (invoice.totalInvoicePrice.amount <= totalPaidAmount) {
            throw ValidationException("Invoice has already been paid. InvoiceId: ${request.invoiceId}")
        }
        if (invoice.totalInvoicePrice.amount < request.amount.amount) {
            throw ValidationException("Invoice has a lower amount to be paid! InvoiceId: ${request.invoiceId}")
        }
        val updated = paymentsRepository.createPayment(paymentReceiptEntity)
        val receiptResponse = objectMapper.convertValue<PaymentReceiptResponse>(updated)
        logger.debug { "finish createPayment(), receiptResponse:$receiptResponse" }
        return receiptResponse
    }

}
