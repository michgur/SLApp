package com.klmn.slapp.common

import java.util.concurrent.Executor

class Task<T>(
    private var executor: Executor,
    private var task: () -> T
) {
    private var onSuccess: ((T) -> Unit)? = null
    private var onException: ((Exception) -> Unit)? = null

    fun doOnSuccess(action: (T) -> Unit) = apply { onSuccess = action }
    fun doOnException(action: (Exception) -> Unit) = apply { onException = action }

    fun execute() {
        executor.execute {
            try {
                val result = task()
                onSuccess?.invoke(result)
            } catch (exception: Exception) {
                onException?.invoke(exception)
            }
        }
    }
}

fun <T> Executor.task(task: () -> T) = Task(this, task)