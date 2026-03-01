/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.fastmpay

import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.BeneficiaryRepository
import org.mifospay.core.data.repository.OfficeRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.model.utils.QrCodeData
import org.mifospay.core.model.utils.QrCodeType
import org.mifospay.feature.fastmpay.model.QrProcessResult

/**
 * Central processor for QR code data.
 *
 * Receives [QrCodeData] and determines the appropriate navigation target
 * based on [QrCodeData.type].
 *
 * @param beneficiaryRepository Repository for checking existing beneficiaries
 * @param userPreferencesRepository Repository for user preferences including current FSP
 * @param officeRepository Repository for resolving office names from IDs
 */
class FastMpayProcessor(
    private val beneficiaryRepository: BeneficiaryRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val officeRepository: OfficeRepository,
) {

    companion object {
        const val DEFAULT_OFFICE_NAME = "Head Office"
    }

    /**
     * Process QR code data and determine navigation target.
     *
     * For INTRA_BANK type, this checks if the beneficiary already exists
     * by matching accountNumber. If found, navigates to MakeTransfer,
     * otherwise navigates to AddBeneficiary.
     *
     * @param qrData The decoded QR code data
     * @return [QrProcessResult] indicating where to navigate
     */
    suspend fun processQrCode(qrData: QrCodeData): QrProcessResult {
        return when (qrData.type) {
            QrCodeType.INTRA_BANK -> {
                processIntraBankQr(qrData)
            }

            QrCodeType.INTER_BANK -> {
                // Inter-bank: Has accountExternalId -> Go to inter-bank transfer
                val accountExternalId = qrData.accountExternalId
                if (accountExternalId.isNullOrBlank()) {
                    return QrProcessResult.Error("Inter-bank QR missing account external ID")
                }
                QrProcessResult.NavigateToInterbankTransfer(
                    accountExternalId = accountExternalId,
                    recipientName = qrData.clientName.takeIf { it.isNotBlank() },
                    amount = qrData.amount.takeIf { it.isNotBlank() },
                )
            }

            QrCodeType.BENEFICIARY -> {
                // Beneficiary: Pre-fill beneficiary form
                val officeName = resolveOfficeName(qrData)
                val beneficiaryJson = convertToBeneficiaryJson(qrData, officeName)
                QrProcessResult.NavigateToAddBeneficiary(beneficiaryJson)
            }

            QrCodeType.MERCHANT -> {
                // Merchant: Navigate to merchant payment
                QrProcessResult.NavigateToMerchantPayment(qrData)
            }
        }
    }

    /**
     * Process Intra-bank QR code.
     *
     * First checks if the QR code belongs to the same bank (FSP).
     * If different bank, returns BankMismatch result.
     * If same bank, checks if beneficiary already exists by matching accountNumber.
     * If found, navigates to MakeTransfer with the existing beneficiary.
     * If not found, navigates to AddBeneficiary with pre-filled data.
     */
    private suspend fun processIntraBankQr(qrData: QrCodeData): QrProcessResult {
        // Check for bank mismatch first
        val currentFspId = userPreferencesRepository.selectedInstance.value?.platformTenantId
        val qrFspId = qrData.fspId

        if (qrFspId != null && currentFspId != null &&
            !qrFspId.equals(currentFspId, ignoreCase = true)
        ) {
            return QrProcessResult.BankMismatch(
                qrData = qrData,
                currentBankId = currentFspId,
                qrBankId = qrFspId,
            )
        }

        // Resolve office name early for beneficiary creation
        val officeName = resolveOfficeName(qrData)

        return try {
            val beneficiaryResult = beneficiaryRepository.getBeneficiaryList().first()

            when (beneficiaryResult) {
                is DataState.Success -> {
                    val existingBeneficiary = beneficiaryResult.data.find {
                        it.accountNumber == qrData.accountNo
                    }

                    if (existingBeneficiary != null) {
                        // Beneficiary exists -> Navigate to MakeTransfer directly
                        QrProcessResult.NavigateToMakeTransfer(
                            qrData = qrData,
                            beneficiaryName = existingBeneficiary.clientName,
                        )
                    } else {
                        // Beneficiary doesn't exist -> Navigate to AddBeneficiary
                        val beneficiaryJson = convertToBeneficiaryJson(qrData, officeName)
                        QrProcessResult.NavigateToAddBeneficiary(beneficiaryJson)
                    }
                }

                is DataState.Error -> {
                    // On error, fallback to add beneficiary flow
                    val beneficiaryJson = convertToBeneficiaryJson(qrData, officeName)
                    QrProcessResult.NavigateToAddBeneficiary(beneficiaryJson)
                }

                is DataState.Loading -> {
                    // Should not happen since we use .first()
                    val beneficiaryJson = convertToBeneficiaryJson(qrData, officeName)
                    QrProcessResult.NavigateToAddBeneficiary(beneficiaryJson)
                }
            }
        } catch (e: Exception) {
            // On exception, fallback to add beneficiary flow
            val beneficiaryJson = convertToBeneficiaryJson(qrData, officeName)
            QrProcessResult.NavigateToAddBeneficiary(beneficiaryJson)
        }
    }

    /**
     * Resolves office name from QR data.
     *
     * Uses officeName directly from QR if available (new QR format).
     * Falls back to looking up by officeId for backward compatibility (old QR format).
     *
     * @param qrData The QR code data containing office info
     * @return The office name from QR, or resolved from officeId, or [DEFAULT_OFFICE_NAME]
     */
    private suspend fun resolveOfficeName(qrData: QrCodeData): String {
        // Use officeName directly if available (new QR format)
        if (qrData.officeName.isNotBlank()) {
            return qrData.officeName
        }

        // Fallback: resolve from officeId (backward compatibility with old QR codes)
        return try {
            val result = officeRepository.getOffices().first()
            when (result) {
                is DataState.Success -> {
                    result.data.find { it.id == qrData.officeId }?.name
                        ?: DEFAULT_OFFICE_NAME
                }
                else -> DEFAULT_OFFICE_NAME
            }
        } catch (e: Exception) {
            DEFAULT_OFFICE_NAME
        }
    }

    /**
     * Converts QrCodeData to Beneficiary JSON string.
     *
     * @param qrData The QR code data containing beneficiary info
     * @param officeName The resolved office name (from API lookup)
     */
    private fun convertToBeneficiaryJson(qrData: QrCodeData, officeName: String): String {
        val beneficiary = Beneficiary(
            name = qrData.clientName,
            clientName = qrData.clientName,
            accountNumber = qrData.accountNo,
            accountType = Beneficiary.AccountType(
                id = qrData.accountTypeId.toInt(),
                code = "savings",
                value = "Savings",
            ),
            officeName = officeName,
            officeId = qrData.officeId,
            transferLimit = 0,
        )
        return Json.encodeToString(Beneficiary.serializer(), beneficiary)
    }
}
