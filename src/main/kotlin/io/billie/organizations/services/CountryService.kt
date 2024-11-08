package io.billie.organizations.services

import io.billie.organizations.dto.CityResponse
import io.billie.organizations.dto.CountryResponse
import io.billie.organizations.repositories.CityRepository
import io.billie.organizations.repositories.CountryRepository
import org.springframework.stereotype.Service

@Service
class CountryService(
    private val countryRepository: CountryRepository,
    private val cityRepository: CityRepository
) {

    fun findCountries(): List<CountryResponse> = countryRepository.findCountries()
        .map { mapToCountryEntity(it) }

    fun findCities(countryCode: String): List<CityResponse> = cityRepository.findByCountryCode(countryCode)
        .map { mapToCityResponse(it) }

}
