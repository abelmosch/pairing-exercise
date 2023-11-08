package io.billie.organizations.enities

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import javax.validation.constraints.Positive

data class PriceValue(
        @Schema(example = "999.0")
        @field:Positive
        val amount: BigDecimal,
        @Schema(example = "USD")
        val currency: String
)