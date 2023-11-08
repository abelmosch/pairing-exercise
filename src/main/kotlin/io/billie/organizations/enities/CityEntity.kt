package io.billie.organizations.enities

import java.util.*

data class CityEntity(
        val id: UUID,
        val name: String,
        val countryCode: String
)