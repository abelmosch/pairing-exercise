package io.billie.organizations.enities

import java.util.*

data class ContactDetailsEntity(
        val id: UUID?,
        val phoneNumber: String?,
        val fax: String?,
        val email: String?
)

