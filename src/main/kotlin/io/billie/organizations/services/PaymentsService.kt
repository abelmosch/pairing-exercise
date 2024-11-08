package io.billie.organizations.services

import io.billie.organizations.dto.PaymentReceiptRequest
import io.billie.organizations.dto.PaymentReceiptResponse
import io.billie.organizations.exceptions.ValidationException
import io.billie.organizations.repositories.PaymentsRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentsService(
    private val paymentsRepository: PaymentsRepository,
    private val organizationService: OrganizationService
) {

    private val logger = KotlinLogging.logger {}

    @Transactional
    fun createPaymentReceipt(request: PaymentReceiptRequest): PaymentReceiptResponse {
        logger.debug { "start createPayment($request)" }
        validatePaymentReceipt(request)
        val paymentReceiptEntity = paymentsRepository.createPaymentReceipt(mapToPaymentReceiptEntity(request))
        val receiptResponse = mapToPaymentReceiptResponse(paymentReceiptEntity)
        logger.debug { "finish createPayment(), receiptResponse:$receiptResponse" }
        return receiptResponse
    }

    private fun validatePaymentReceipt(request: PaymentReceiptRequest) {
        val organizationResponse = organizationService.findOrganizationById(request.merchantId)
        logger.info { "validatePaymentReceipt for id${organizationResponse.id} ${organizationResponse.name}" }
        val invoice = paymentsRepository.getInvoiceById(request.invoiceId)
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
        logger.info { "Validated! invoiceId: ${request.invoiceId} for id${organizationResponse.id} ${organizationResponse.name}" }
    }
}
