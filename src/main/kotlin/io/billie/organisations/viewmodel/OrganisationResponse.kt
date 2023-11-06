package io.billie.organisations.viewmodel

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import io.billie.countries.model.CountryResponse
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.util.*

data class OrganisationResponse(
        val id: UUID,
        val name: String,
        @Schema(example = "30/08/1999") @JsonFormat(pattern = "dd/MM/yyyy") @JsonProperty("date_founded") val dateFounded: LocalDate,
        val country: CountryResponse,
        @JsonProperty("vat_number") val vatNumber: String?,
        @JsonProperty("registration_number") val registrationNumber: String?,
        @JsonProperty("legal_entity_type") val legalEntityType: LegalEntityType,
        @JsonProperty("contact_details") val contactDetails: ContactDetailsResponse,
)
