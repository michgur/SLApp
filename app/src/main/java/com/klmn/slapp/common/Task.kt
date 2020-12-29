package com.klmn.slapp.common

import java.util.concurrent.Executor

class Task<T>(
    private var executor: Executor,
    private var task: () -> T
) {
    private var onSuccess: ((T) -> Unit)? = null
    private var onException: ((Exception) -> Unit)? = null

//    fun doOnSuccess((T) -> Unit)

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