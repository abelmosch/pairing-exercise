package io.billie.organizations.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*
import javax.validation.constraints.Size

data class CityResponse(
        val id: UUID,
        val name: String,
        @Schema(example = "DE")
        @Size(min = 2, max = 2)
        @JsonProperty("country_code")
        val countryCode: String
)

