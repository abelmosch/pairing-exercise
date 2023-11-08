package io.billie.organizations.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.billie.organizations.controllers.data.Fixtures
import io.billie.organizations.dto.CreateOrganizationResponse
import io.billie.organizations.repositories.PaymentsRepository
import org.junit.jupiter.api.BeforeEach
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
class PaymentsControllerTest {

    @Autowired
    private lateinit var paymentsRepository: PaymentsRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    private lateinit var ORGANIZATION_ID: UUID

    @BeforeEach
    fun setUp() {
        ORGANIZATION_ID = createTestOrganization().id
    }

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


        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(ORGANIZATION_ID, fakeInvoiceId))
        ).andExpect(status().isBadRequest())
    }

    @Test
    fun `create not fully paid payment receipt`() {

        val halfPaidAmount = BigDecimal(500)
        val createInvoice = paymentsRepository.createInvoice(Fixtures.invoiceEntity(ORGANIZATION_ID, BigDecimal(1000)))
        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(ORGANIZATION_ID, createInvoice.id, halfPaidAmount))
        ).andExpect(status().isOk())
    }

    @Test
    fun `create fully paid payment receipt in multiple shipments`() {

        val halfPaidAmount = BigDecimal(500)
        val createInvoice = paymentsRepository.createInvoice(Fixtures.invoiceEntity(ORGANIZATION_ID, BigDecimal(1000)))
        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(ORGANIZATION_ID, createInvoice.id, halfPaidAmount))
        ).andExpect(status().isOk())
        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(ORGANIZATION_ID, createInvoice.id, halfPaidAmount))
        ).andExpect(status().isOk())


        //try to acquire more money
        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(ORGANIZATION_ID, createInvoice.id, BigDecimal(0.01)))
        ).andExpect(status().isBadRequest())
    }


    @Test
    fun `create fully paid payment receipt`() {
        val fullyPaidAmount = BigDecimal(1000)

        val createInvoice = paymentsRepository.createInvoice(Fixtures.invoiceEntity(ORGANIZATION_ID, fullyPaidAmount))
        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(ORGANIZATION_ID, createInvoice.id, fullyPaidAmount))
        ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.invoice_id").value(createInvoice.id.toString()))

    }

    @Test
    fun `create payment receipt when invoice has already been paid`() {
        val fullyPaidAmount = BigDecimal(1000)

        val createInvoice = paymentsRepository.createInvoice(Fixtures.invoiceEntity(ORGANIZATION_ID, fullyPaidAmount))

        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(ORGANIZATION_ID, createInvoice.id, fullyPaidAmount))
        ).andExpect(status().isOk())

        //try to acquire more money
        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(ORGANIZATION_ID, createInvoice.id, fullyPaidAmount))
        ).andExpect(status().isBadRequest())
    }

    @Test
    fun `create payment receipt with different merchant id`() {
        val wrongMerchantId = UUID.randomUUID()
        val fullyPaidAmount = BigDecimal(1000)

        val createInvoice = paymentsRepository.createInvoice(Fixtures.invoiceEntity(ORGANIZATION_ID, fullyPaidAmount))

        mockMvc.perform(
                post("/payments")
                        .contentType(APPLICATION_JSON)
                        .content(Fixtures.paymentReceipt(wrongMerchantId, createInvoice.id, fullyPaidAmount))
        ).andExpect(status().isBadRequest())
    }

    private fun createTestOrganization(): CreateOrganizationResponse {
        val result = mockMvc.perform(
                post("/organizations")
                        .contentType(APPLICATION_JSON).content(Fixtures.orgRequestJson())
        )
                .andExpect(status().isOk)
                .andReturn()

        return mapper.readValue(result.response.contentAsString, CreateOrganizationResponse::class.java)
    }

}
