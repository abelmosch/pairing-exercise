package io.billie.organisations.viewmodel

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class Entity(
        @Schema(description = "Created ID of the Entity") val id: UUID
)
