package io.billie.organizations.services

import io.billie.organizations.dto.ContactDetailsDto
import io.billie.organizations.dto.CountryResponse
import io.billie.organizations.dto.OrganizationRequest
import io.billie.organizations.dto.OrganizationResponse
import io.billie.organizations.enities.ContactDetailsEntity
import io.billie.organizations.enities.CountryEntity
import io.billie.organizations.enities.OrganizationEntity
import io.billie.organizations.repositories.OrganizationRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrganizationService(private val organizationRepository: OrganizationRepository) {

    fun findOrganizations(): List<OrganizationResponse> = organizationRepository.findOrganizations().map { mapToResponse(it) }
    fun findOrganizationById(id: UUID): OrganizationResponse = mapToResponse(organizationRepository.findOrganizationById(id))

    fun createOrganization(organization: OrganizationRequest): OrganizationResponse = mapToResponse(organizationRepository.create(organization))
    private fun mapToResponse(entity: OrganizationEntity): OrganizationResponse {
        return OrganizationResponse(entity.id, entity.name, entity.dateFounded, countryEntity(entity.country),
                entity.vatNumber, entity.registrationNumber, entity.legalEntityType, contactDetailsEntity(entity.contactDetails))
    }

    private fun countryEntity(entity: CountryEntity) = CountryResponse(entity.id, entity.name, entity.countryCode)

    private fun contactDetailsEntity(entity: ContactDetailsEntity) = ContactDetailsDto(entity.id, entity.phoneNumber,
            entity.fax, entity.email)
}
