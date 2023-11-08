package io.billie.organizations.repositories

import io.billie.organizations.enities.CountryEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.util.*

@Repository
class CountryRepository {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Transactional(readOnly = true)
    fun findCountries(): List<CountryEntity> {
        return jdbcTemplate.query(
                "select id, name, country_code from organisations_schema.countries",
                countryEntityMapper()
        )
    }

    @Transactional(readOnly = true)
    fun findOneByCountryCode(countryCode: String): CountryEntity {
        return jdbcTemplate.queryForObject(
                "select id, name, country_code from organisations_schema.countries where country_code = ?",
                countryEntityMapper(),
                countryCode
        )!!
    }

    private fun countryEntityMapper() = RowMapper<CountryEntity> { it: ResultSet, _: Int ->
        CountryEntity(
                it.getObject("id", UUID::class.java),
                it.getString("name"),
                it.getString("country_code")
        )
    }
}
