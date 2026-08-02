/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repository

import org.mifospay.core.common.DataState
import org.mifospay.core.common.ScreenStateStream
import org.mifospay.core.model.user.NewUser
import org.mifospay.core.network.model.CommonResponse
import org.mifospay.core.network.model.GenericResponse
import org.mifospay.core.network.model.entity.UserWithRole

interface UserRepository {
    // Phase-3 cutover — Flow-shaped surfaces on ScreenState.
    suspend fun getUsers(): ScreenStateStream<List<UserWithRole>>

    suspend fun getUser(): ScreenStateStream<UserWithRole>

    suspend fun updateUser(userId: Int, updatedUser: NewUser): ScreenStateStream<GenericResponse>

    // Writes stay on DataState (Phase-3 D1).
    suspend fun createUser(newUser: NewUser): DataState<Int>

    suspend fun updateUserPassword(userId: Long, password: String): DataState<String>

    suspend fun deleteUser(userId: Int): DataState<CommonResponse>

    suspend fun assignClientToUser(userId: Int, clientId: Int): DataState<Unit>
}
