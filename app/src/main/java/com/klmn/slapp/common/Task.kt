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

    fun execute() = executor.execute {
        var result: T? = null
        try { result = task() }
        catch (exception: Exception) { onException?.invoke(exception) }
        // this line is outside of the try block in case onSuccess raises an Exception
        result?.let { onSuccess?.invoke(it) }
    }
}

fun <T> Executor.task(task: () -> T) = Task(this, task)