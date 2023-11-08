package io.billie.organizations.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class CreateOrganizationResponse(
        @Schema(description = "Created ID of the Entity") val id: UUID
)
