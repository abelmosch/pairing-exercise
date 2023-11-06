package io.billie.organisations.viewmodel

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class ContactDetailsResponse(
        @Schema(description = "A generated ID of the ContactDetails Entity") val id: UUID?,
        @Schema(example = "+14848884771") @JsonProperty("phone_number") val phoneNumber: String?,
        @Schema(example = "75774661") val fax: String?,
        @Schema(example = "boris@britva.com") val email: String?
)
