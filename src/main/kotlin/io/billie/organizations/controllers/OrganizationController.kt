package io.billie.organizations.controllers

import io.billie.organizations.exceptions.UnableToValidateOrganizationByCountryException
import io.billie.organizations.services.OrganizationService
import io.billie.organizations.dto.*
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid


@RestController
@RequestMapping("organizations")
class OrganizationController(private val service: OrganizationService) {

    @GetMapping
    fun index(): List<OrganizationResponse> = service.findOrganizations()

    @PostMapping
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Accepted the new organization",
                content = [
                    (Content(
                        mediaType = "application/json",
                        array = (ArraySchema(schema = Schema(implementation = CreateOrganizationResponse::class)))
                    ))]
            ),
            ApiResponse(responseCode = "400", description = "Bad request", content = [Content()])]
    )
    fun post(@Valid @RequestBody organisation: OrganizationRequest): CreateOrganizationResponse {
        try {
            val organizationEntity = service.createOrganization(organisation)
            return CreateOrganizationResponse(organizationEntity.id)
        } catch (e: UnableToValidateOrganizationByCountryException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        }
    }

}
