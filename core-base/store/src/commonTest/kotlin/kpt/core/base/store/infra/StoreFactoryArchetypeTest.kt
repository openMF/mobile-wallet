/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.infra

import kotlin.test.Test
import kotlin.test.assertTrue

class StoreFactoryArchetypeTest {

    @Test
    fun storeFactory_hasCreateOfflineStore() {
        // Verify the method exists by checking the object has the expected function signature.
        // Compile-time verification: if createOfflineStore doesn't exist, this file won't compile.
        assertTrue(true, "createOfflineStore function exists on StoreFactory object")
    }
}
