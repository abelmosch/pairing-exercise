package io.billie.payments.db

import io.billie.payments.exceptions.ValidationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.util.*

@Repository
class PaymentsRepository {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Transactional
    fun createInvoice(invoiceEntity: InvoiceEntity): InvoiceEntity {
        val sql = "INSERT INTO organisations_schema.invoices " +
                "(id, buyer_id, merchant_id, order_id, total_invoice_price,currency, created_date, updated_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        jdbcTemplate.update(
                sql,
                invoiceEntity.id,
                invoiceEntity.buyerId,
                invoiceEntity.merchantId,
                invoiceEntity.orderId,
                invoiceEntity.totalInvoicePrice.amount,
                invoiceEntity.totalInvoicePrice.currency,
                invoiceEntity.createdDate,
                invoiceEntity.updatedDate
        )
        return invoiceEntity
    }

    @Transactional(readOnly = true)
    fun getInvoiceById(id: UUID): InvoiceEntity {
        val sql = "SELECT * FROM organisations_schema.invoices WHERE id = ?"
        return try {
            jdbcTemplate.queryForObject(sql, { rs, _ ->
                InvoiceEntity(
                        id = rs.getObject("id", UUID::class.java),
                        buyerId = rs.getString("buyer_id"),
                        merchantId = rs.getObject("merchant_id", UUID::class.java),
                        orderId = rs.getString("order_id"),
                        totalInvoicePrice = PriceValue(rs.getBigDecimal("total_invoice_price"), rs.getString("currency")),
                        createdDate = rs.getTimestamp("created_date").toLocalDateTime(),
                        updatedDate = rs.getTimestamp("updated_date").toLocalDateTime()
                )
            }, id)!!
        } catch (e: EmptyResultDataAccessException) {
            throw ValidationException("Invoice: $id is not found!")
        }
    }

    @Transactional
    fun createPayment(paymentReceiptEntity: PaymentReceiptEntity): PaymentReceiptEntity {
        try {
            val keyHolder: KeyHolder = GeneratedKeyHolder()
            jdbcTemplate.update(
                    { connection ->
                        val ps = connection.prepareStatement(
                                "INSERT INTO organisations_schema.payment_receipts (amount, currency, invoice_id, payment_type, created_date) " +
                                        "VALUES " +
                                        "    (?,?,?,?,?)",
                                arrayOf("id")
                        )
                        ps.setBigDecimal(1, paymentReceiptEntity.amount.amount)
                        ps.setString(2, paymentReceiptEntity.amount.currency)
                        ps.setObject(3, paymentReceiptEntity.invoiceId)
                        ps.setString(4, paymentReceiptEntity.paymentType.toString())
                        ps.setTimestamp(5, Timestamp.valueOf(paymentReceiptEntity.createdDate))
                        ps
                    }, keyHolder
            )
            return paymentReceiptEntity.copy(id = keyHolder.getKeyAs(UUID::class.java)!!)
        } catch (e: Exception) {
            throw ValidationException("Persisting error: ${e.message}", e)
        }
    }

    @Transactional
    fun getPaymentReceiptsByInvoiceIdWithLock(invoiceId: UUID): List<PaymentReceiptEntity> {
        val sql = "SELECT * FROM organisations_schema.payment_receipts WHERE invoice_id = ? FOR UPDATE"
        return jdbcTemplate.query(
                sql,
                { rs, _ ->
                    PaymentReceiptEntity(
                            id = rs.getObject("id", UUID::class.java),
                            amount = PriceValue(rs.getBigDecimal("amount"), "USD"),
                            invoiceId = rs.getObject("invoice_id", UUID::class.java),
                            paymentType = PaymentType.valueOf(rs.getString("payment_type")),
                            createdDate = rs.getTimestamp("created_date").toLocalDateTime()
                    )
                },
                invoiceId
        )
    }

}