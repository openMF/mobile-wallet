/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.base

import org.mifospay.core.data.base.UseCase.UseCaseCallback

class TaskLooper(
    private val mUseCaseHandler: UseCaseHandler,
) {
    var isFailed = false
    private var tasksPending: Long = 0
    private var listener: Listener? = null

    fun <T : UseCase.RequestValues, R : UseCase.ResponseValue?> addTask(
        useCase: UseCase<T, R>,
        values: T,
        taskData: TaskData,
    ) {
        tasksPending++
        mUseCaseHandler.execute(
            useCase,
            values,
            object : UseCaseCallback<R> {
                override fun onSuccess(response: R) {
                    if (isFailed) return
                    listener!!.onTaskSuccess(taskData, response)
                    tasksPending--
                    if (isCompleted) {
                        listener!!.onComplete()
                    }
                }

                override fun onError(message: String) {
                    isFailed = true
                    listener!!.onFailure(message)
                }
            },
        )
    }

    private val isCompleted: Boolean
        get() = tasksPending == 0L

    fun listen(listener: Listener?) {
        this.listener = listener
    }

    interface Listener {
        fun <R : UseCase.ResponseValue?> onTaskSuccess(taskData: TaskData, response: R)
        fun onComplete()
        fun onFailure(message: String?)
    }

    class TaskData(var taskName: String, var taskId: Int)
}
