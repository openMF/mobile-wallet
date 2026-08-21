/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.data.repository

import org.mifospay.core.common.ScreenStateStream
import org.mifospay.core.model.network.CommonResponse
import org.mifospay.core.model.network.GenericResponse
import org.mifospay.core.model.network.entity.UserWithRole
import org.mifospay.core.model.user.NewUser

interface UserRepository {
    // Phase-3 cutover — Flow-shaped surfaces on ScreenState.
    suspend fun getUsers(): ScreenStateStream<List<UserWithRole>>

    suspend fun getUser(): ScreenStateStream<UserWithRole>

    suspend fun updateUser(userId: Int, updatedUser: NewUser): ScreenStateStream<GenericResponse>

    // Writes throw on failure (DataState-free).
    suspend fun createUser(newUser: NewUser): Int

    suspend fun updateUserPassword(userId: Long, password: String)

    suspend fun deleteUser(userId: Int): CommonResponse

    suspend fun assignClientToUser(userId: Int, clientId: Int)
}
