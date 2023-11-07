package io.billie.functional

import com.fasterxml.jackson.databind.ObjectMapper
import io.billie.functional.data.Fixtures
import io.billie.organisations.viewmodel.Entity
import io.billie.payments.db.PaymentsRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.util.*



@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CanCreatePaymentTest {

    @Autowired
    private lateinit var paymentsRepository: PaymentsRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper


    // buyer choose bilie as a payment method-> merchant
    // billie create an invoice -> buyer
    // merchant send goods and then confirm shipment of the order -> billie
    // billie create a payment receipt in status TO_BE_PAID  -> merchant
    /*
    The ability for the merchant to notify Billie of shipment of an order, so they can get paid.
    The merchant is not required to send a list of the shipped items but the sum of the shipments
     should not exceed the total order amount.
     */

    @Test
    fun `create payment receipt without organisation and fail`() {
        val fakeInvoiceId = UUID.randomUUID()
        val fakeOrgId = UUID.randomUUID()

        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(fakeOrgId, fakeInvoiceId))
        ).andExpect(status().isBadRequest())
    }

    @Test
    fun `create payment receipt without invoice and fail`() {
        val fakeInvoiceId = UUID.randomUUID()

        val response = createOrg()

        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(response.id, fakeInvoiceId))
        ).andExpect(status().isBadRequest())
    }

    @Test
    fun `create not fully paid payment receipt`() {
        val response = createOrg()

        val halfPaidAmount = BigDecimal(500)
        val createInvoice = paymentsRepository.createInvoice(Fixtures.invoiceEntity(response.id, BigDecimal(1000)))
        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(response.id, createInvoice.id, halfPaidAmount))
        ).andExpect(status().isOk())
    }
    @Test
    fun `create fully paid payment receipt in multiple shipments`() {
        val response = createOrg()

        val halfPaidAmount = BigDecimal(500)
        val createInvoice = paymentsRepository.createInvoice(Fixtures.invoiceEntity(response.id, BigDecimal(1000)))
        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(response.id, createInvoice.id, halfPaidAmount))
        ).andExpect(status().isOk())
        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(response.id, createInvoice.id, halfPaidAmount))
        ).andExpect(status().isOk())


        //try to acquire more money
        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(response.id, createInvoice.id, BigDecimal(0.01)))
        ).andExpect(status().isBadRequest())
    }


    @Test
    fun `create fully paid payment receipt`() {
        val response = createOrg()
        val fullyPaidAmount = BigDecimal(1000)

        val createInvoice = paymentsRepository.createInvoice(Fixtures.invoiceEntity(response.id, fullyPaidAmount))
        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(response.id, createInvoice.id, fullyPaidAmount))
        ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.invoiceId").value(createInvoice.id.toString()))

    }

    @Test
    fun `create payment receipt when invoice has already been paid`() {
        val response = createOrg()
        val fullyPaidAmount = BigDecimal(1000)

        val createInvoice = paymentsRepository.createInvoice(Fixtures.invoiceEntity(response.id, fullyPaidAmount))

        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(response.id, createInvoice.id, fullyPaidAmount))
        ).andExpect(status().isOk())

        //try to acquire more money
        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(response.id, createInvoice.id, fullyPaidAmount))
        ).andExpect(status().isBadRequest())
    }

    @Test
    fun `create payment receipt with different merchant id`() {
        val wrongMerchantId = UUID.randomUUID()
        val organization = createOrg()
        val fullyPaidAmount = BigDecimal(1000)

        val createInvoice = paymentsRepository.createInvoice(Fixtures.invoiceEntity(organization.id, fullyPaidAmount))

        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(wrongMerchantId, createInvoice.id, fullyPaidAmount))
        ).andExpect(status().isBadRequest())
    }

    private fun createOrg(): Entity {
        val result = mockMvc.perform(
                post("/organisations")
                        .contentType(APPLICATION_JSON).content(Fixtures.orgRequestJson())
        )
                .andExpect(status().isOk)
                .andReturn()

        return mapper.readValue(result.response.contentAsString, Entity::class.java)
    }

}
