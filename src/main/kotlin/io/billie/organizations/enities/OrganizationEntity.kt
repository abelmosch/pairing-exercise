package io.billie.organizations.enities

import java.time.LocalDate
import java.util.UUID

data class OrganizationEntity(
        val id: UUID,
        val name: String,
        val dateFounded: LocalDate,
        val country: CountryEntity,
        val vatNumber: String?,
        val registrationNumber: String?,
        val legalEntityType: LegalEntityType,
        val contactDetails: ContactDetailsEntity,
)