package io.billie.organizations.repositories

import io.billie.organizations.dto.ContactDetailsDto
import io.billie.organizations.dto.OrganizationRequest
import io.billie.organizations.enities.ContactDetailsEntity
import io.billie.organizations.enities.CountryEntity
import io.billie.organizations.enities.LegalEntityType
import io.billie.organizations.enities.OrganizationEntity
import io.billie.organizations.exceptions.UnableToValidateOrganizationByCountryException
import io.billie.organizations.exceptions.ValidationException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Date
import java.sql.ResultSet
import java.util.*


@Repository
class OrganizationRepository(private val countryRepository: CountryRepository,
                             private val jdbcTemplate: JdbcTemplate) {


    @Transactional(readOnly = true)
    fun findOrganizations(): List<OrganizationEntity> {
        return jdbcTemplate.query(organisationQuery(), organisationMapper())
    }

    @Transactional
    fun create(organisation: OrganizationRequest): OrganizationEntity {
        if (!valuesValid(organisation)) {
            throw UnableToValidateOrganizationByCountryException(organisation.countryCode)
        }
        val id: UUID = createContactDetails(organisation.contactDetails)
        return createOrganization(organisation, id)
    }

    private fun valuesValid(organisation: OrganizationRequest): Boolean {
        val reply: Int? = jdbcTemplate.query(
                "select count(country_code) from organisations_schema.countries c WHERE c.country_code = ?",
                ResultSetExtractor {
                    it.next()
                    it.getInt(1)
                },
                organisation.countryCode
        )
        return (reply != null) && (reply > 0)
    }

    private fun createOrganization(org: OrganizationRequest, contactDetailsId: UUID): OrganizationEntity {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(
                { connection ->
                    val ps = connection.prepareStatement(
                            "INSERT INTO organisations_schema.organisations (" +
                                    "name, " +
                                    "date_founded, " +
                                    "country_code, " +
                                    "vat_number, " +
                                    "registration_number, " +
                                    "legal_entity_type, " +
                                    "contact_details_id" +
                                    ") VALUES (?, ?, ?, ?, ?, ?, ?)",
                            arrayOf("id")
                    )
                    ps.setString(1, org.name)
                    ps.setDate(2, Date.valueOf(org.dateFounded))
                    ps.setString(3, org.countryCode)
                    ps.setString(4, org.vatNumber)
                    ps.setString(5, org.registrationNumber)
                    ps.setString(6, org.legalEntityType.toString())
                    ps.setObject(7, contactDetailsId)
                    ps
                }, keyHolder
        )
        return OrganizationEntity(
                id = keyHolder.getKeyAs(UUID::class.java)!!,
                name = org.name,
                dateFounded = org.dateFounded,
                country = countryRepository.findOneByCountryCode(org.countryCode),
                vatNumber = org.vatNumber,
                registrationNumber = org.registrationNumber,
                legalEntityType = org.legalEntityType,
                contactDetails = convertToEntity(org.contactDetails, contactDetailsId)
        )
    }

    private fun convertToEntity(contactDetails: ContactDetailsDto, contactDetailsId: UUID): ContactDetailsEntity {
        return ContactDetailsEntity(contactDetailsId, contactDetails.phoneNumber, contactDetails.fax, contactDetails.email)
    }

    private fun createContactDetails(contactDetails: ContactDetailsDto): UUID {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(
                { connection ->
                    val ps = connection.prepareStatement(
                            "insert into organisations_schema.contact_details " +
                                    "(" +
                                    "phone_number, " +
                                    "fax, " +
                                    "email" +
                                    ") values(?,?,?)",
                            arrayOf("id")
                    )
                    ps.setString(1, contactDetails.phoneNumber)
                    ps.setString(2, contactDetails.fax)
                    ps.setString(3, contactDetails.email)
                    ps
                },
                keyHolder
        )
        return keyHolder.getKeyAs(UUID::class.java)!!
    }


    private fun organisationQuery() = "select " +
            "o.id as id, " +
            "o.name as name, " +
            "o.date_founded as date_founded, " +
            "o.country_code as country_code, " +
            "c.id as country_id, " +
            "c.name as country_name, " +
            "o.VAT_number as VAT_number, " +
            "o.registration_number as registration_number," +
            "o.legal_entity_type as legal_entity_type," +
            "o.contact_details_id as contact_details_id, " +
            "cd.phone_number as phone_number, " +
            "cd.fax as fax, " +
            "cd.email as email " +
            "from " +
            "organisations_schema.organisations o " +
            "INNER JOIN organisations_schema.contact_details cd on o.contact_details_id::uuid = cd.id::uuid " +
            "INNER JOIN organisations_schema.countries c on o.country_code = c.country_code "

    private fun organisationMapper() = RowMapper<OrganizationEntity> { it: ResultSet, _: Int ->
        OrganizationEntity(
                it.getObject("id", UUID::class.java),
                it.getString("name"),
                it.getDate("date_founded").toLocalDate(),
                mapCountry(it),
                it.getString("vat_number"),
                it.getString("registration_number"),
                LegalEntityType.valueOf(it.getString("legal_entity_type")),
                mapContactDetails(it)
        )
    }

    private fun mapContactDetails(it: ResultSet): ContactDetailsEntity {
        return ContactDetailsEntity(
                UUID.fromString(it.getString("contact_details_id")),
                it.getString("phone_number"),
                it.getString("fax"),
                it.getString("email")
        )
    }

    private fun mapCountry(it: ResultSet): CountryEntity {
        return CountryEntity(
                it.getObject("country_id", UUID::class.java),
                it.getString("country_name"),
                it.getString("country_code")
        )
    }

    fun findOrganizationById(organizationId: UUID): OrganizationEntity {
        val sql = "${organisationQuery()} WHERE o.id = ?"
        try {
            return jdbcTemplate.queryForObject(sql, { rs, _ ->
                OrganizationEntity(
                        id = rs.getObject("id", UUID::class.java),
                        name = rs.getString("name"),
                        dateFounded = rs.getDate("date_founded").toLocalDate(),
                        country = countryRepository.findOneByCountryCode(rs.getString("country_code")),
                        vatNumber = rs.getString("vat_number"),
                        registrationNumber = rs.getString("registration_number"),
                        legalEntityType = LegalEntityType.valueOf(rs.getString("legal_entity_type")),
                        contactDetails = mapContactDetails(rs)
                )
            }, organizationId)!!
        } catch (e: Exception) {
            throw ValidationException("Cannot find organization! merchantId: $organizationId")
        }
    }

}
