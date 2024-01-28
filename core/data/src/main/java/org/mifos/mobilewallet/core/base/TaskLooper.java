package org.mifos.mobilewallet.core.base;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by ankur on 17/June/2018
 */

public class TaskLooper {

    private final UseCaseHandler mUsecaseHandler;
    boolean isFailed;
    List<UseCase> tasks;
    private long tasksPending;
    private Listener listener;

    @Inject
    public TaskLooper(UseCaseHandler useCaseHandler) {
        isFailed = false;
        tasksPending = 0;
        mUsecaseHandler = useCaseHandler;
        tasks = new ArrayList<>();
    }

    public <T extends UseCase.RequestValues, R extends UseCase.ResponseValue> void addTask(
            final UseCase<T, R> useCase, T values, final TaskData taskData) {

        tasksPending++;

        mUsecaseHandler.execute(useCase, values, new UseCase.UseCaseCallback<R>() {
            @Override
            public void onSuccess(R response) {
                if (isFailed) return;

                listener.onTaskSuccess(taskData, response);

                tasksPending--;

                if (isCompleted()) {
                    listener.onComplete();
                }
            }

            @Override
            public void onError(String message) {
                isFailed = true;
                listener.onFailure(message);
            }
        });

    }

    private boolean isCompleted() {
        return tasksPending == 0;
    }

    public void listen(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {

        <R extends UseCase.ResponseValue> void onTaskSuccess(TaskData taskData, R response);

        void onComplete();

        void onFailure(String message);
    }

    public static class TaskData {

        private String taskName;
        private int taskId;

        public TaskData(String taskName, int taskId) {
            this.taskName = taskName;
            this.taskId = taskId;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public int getTaskId() {
            return taskId;
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }
    }
}


