package io.billie.organizations.services

import io.billie.organizations.dto.OrganizationRequest
import io.billie.organizations.dto.OrganizationResponse
import io.billie.organizations.repositories.OrganizationRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrganizationService(private val organizationRepository: OrganizationRepository) {

    fun findOrganizations(): List<OrganizationResponse> =
        organizationRepository.findOrganizations().map { mapToResponse(it) }

    fun findOrganizationById(id: UUID): OrganizationResponse =
        mapToResponse(organizationRepository.findOrganizationById(id))

    fun createOrganization(organization: OrganizationRequest): OrganizationResponse =
        mapToResponse(organizationRepository.create(organization))

}
