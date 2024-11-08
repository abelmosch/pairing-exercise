package io.billie.organizations.services

import io.billie.organizations.dto.*
import io.billie.organizations.enities.*


fun mapToCityResponse(it: CityEntity) = CityResponse(it.id, it.name, it.countryCode)
fun mapToCountryEntity(entity: CountryEntity) = CountryResponse(entity.id, entity.name, entity.countryCode)

fun mapToPaymentReceiptEntity(request: PaymentReceiptRequest) = PaymentReceiptEntity(
    invoiceId = request.invoiceId,
    amount = request.amount,
    paymentType = request.paymentType,
    createdDate = request.createdDate
)

fun mapToPaymentReceiptResponse(updated: PaymentReceiptEntity) = PaymentReceiptResponse(
    id = updated.id,
    amount = updated.amount,
    invoiceId = updated.invoiceId,
    paymentType = updated.paymentType,
    createdDate = updated.createdDate
)

fun mapToResponse(entity: OrganizationEntity) = OrganizationResponse(
    entity.id, entity.name, entity.dateFounded, countryEntity(entity.country),
    entity.vatNumber, entity.registrationNumber, entity.legalEntityType, contactDetailsEntity(entity.contactDetails)
)

fun countryEntity(entity: CountryEntity) = CountryResponse(entity.id, entity.name, entity.countryCode)

fun contactDetailsEntity(entity: ContactDetailsEntity) = ContactDetailsDto(
    entity.id, entity.phoneNumber,
    entity.fax, entity.email
)