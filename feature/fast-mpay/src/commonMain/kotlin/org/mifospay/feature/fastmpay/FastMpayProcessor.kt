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
 */
class FastMpayProcessor(
    private val beneficiaryRepository: BeneficiaryRepository,
) {

    /**
     * Process QR code data and determine navigation target.
     *
     * For INTRA_BANK type, this checks if the beneficiary already exists
     * by matching accountNumber. If found, navigates to SendMoneyV2,
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
                val beneficiaryJson = convertToBeneficiaryJson(qrData)
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
     * Checks if beneficiary already exists by matching accountNumber.
     * If found, navigates to SendMoneyV2 with the existing beneficiary.
     * If not found, navigates to AddBeneficiary with pre-filled data.
     */
    private suspend fun processIntraBankQr(qrData: QrCodeData): QrProcessResult {
        return try {
            val beneficiaryResult = beneficiaryRepository.getBeneficiaryList().first()

            when (beneficiaryResult) {
                is DataState.Success -> {
                    val existingBeneficiary = beneficiaryResult.data.find {
                        it.accountNumber == qrData.accountNo
                    }

                    if (existingBeneficiary != null) {
                        // Beneficiary exists -> Navigate to MakeTransferV2 directly
                        QrProcessResult.NavigateToSendMoneyV2(
                            qrData = qrData,
                            beneficiaryName = existingBeneficiary.clientName,
                        )
                    } else {
                        // Beneficiary doesn't exist -> Navigate to AddBeneficiary
                        val beneficiaryJson = convertToBeneficiaryJson(qrData)
                        QrProcessResult.NavigateToAddBeneficiary(beneficiaryJson)
                    }
                }

                is DataState.Error -> {
                    // On error, fallback to add beneficiary flow
                    val beneficiaryJson = convertToBeneficiaryJson(qrData)
                    QrProcessResult.NavigateToAddBeneficiary(beneficiaryJson)
                }

                is DataState.Loading -> {
                    // Should not happen since we use .first()
                    val beneficiaryJson = convertToBeneficiaryJson(qrData)
                    QrProcessResult.NavigateToAddBeneficiary(beneficiaryJson)
                }
            }
        } catch (e: Exception) {
            // On exception, fallback to add beneficiary flow
            val beneficiaryJson = convertToBeneficiaryJson(qrData)
            QrProcessResult.NavigateToAddBeneficiary(beneficiaryJson)
        }
    }

    /**
     * Converts QrCodeData to Beneficiary JSON string.
     */
    private fun convertToBeneficiaryJson(qrData: QrCodeData): String {
        val beneficiary = Beneficiary(
            name = qrData.clientName,
            clientName = qrData.clientName,
            accountNumber = qrData.accountNo,
            accountType = Beneficiary.AccountType(
                id = qrData.accountTypeId.toInt(),
                code = "savings",
                value = "Savings",
            ),
            officeName = "Head Office",
            transferLimit = 0,
        )
        return Json.encodeToString(Beneficiary.serializer(), beneficiary)
    }
}
