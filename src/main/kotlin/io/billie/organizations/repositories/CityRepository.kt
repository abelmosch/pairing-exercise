package io.billie.organizations.repositories

import io.billie.organizations.enities.CityEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.util.*

@Repository
class CityRepository {


    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Transactional(readOnly = true)
    fun findByCountryCode(countryCode: String): List<CityEntity> {
        return jdbcTemplate.query(
                "select id, name, country_code from organisations_schema.cities where country_code = ?",
                cityResponseMapper(),
                countryCode
        )
    }

    private fun cityResponseMapper() = RowMapper<CityEntity> { it: ResultSet, _: Int ->
        CityEntity(
                it.getObject("id", UUID::class.java),
                it.getString("name"),
                it.getString("country_code")
        )
    }
}
