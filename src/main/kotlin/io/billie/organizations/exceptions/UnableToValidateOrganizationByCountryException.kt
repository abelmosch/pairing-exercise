package io.billie.organizations.exceptions

class UnableToValidateOrganizationByCountryException(val countryCode: String) : RuntimeException()
