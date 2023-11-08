package io.billie.organizations.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*
import javax.validation.constraints.Size

data class CountryResponse(
        val id: UUID,
        val name: String,
        @Schema(example = "DE") @JsonProperty("country_code") @Size(min = 2, max = 2) val countryCode: String? =null,
)

