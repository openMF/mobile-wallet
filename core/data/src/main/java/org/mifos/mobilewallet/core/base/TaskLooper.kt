package org.mifos.mobilewallet.core.base

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import javax.inject.Inject

/**
 * Created by ankur on 17/June/2018
 */
class TaskLooper @Inject constructor(private val mUsecaseHandler: UseCaseHandler) {
    var isFailed = false
    var tasks: List<UseCase<*, *>>
    private var tasksPending: Long = 0
    private var listener: Listener? = null

    init {
        tasks = ArrayList()
    }

    fun <T : UseCase.RequestValues, R : UseCase.ResponseValue?> addTask(
        useCase: UseCase<T, R>, values: T, taskData: TaskData
    ) {
        tasksPending++
        mUsecaseHandler.execute(useCase, values, object : UseCaseCallback<R> {
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
        })
    }

    private val isCompleted: Boolean
        private get() = tasksPending == 0L

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
