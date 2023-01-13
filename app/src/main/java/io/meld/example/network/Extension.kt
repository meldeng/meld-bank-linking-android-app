package io.meld.example.network


/**
 * return true if the network call is in completed state
 * * @return boolean
 */
fun <T, F> Resource<T, F>.isEndState() =
    (state == Resource.State.ERROR) or (state == Resource.State.SUCCESS)

fun <T, F> Resource<T, F>.ifSuccess(func: (data: T?) -> Unit) {
    if (state == Resource.State.SUCCESS) {
        func.invoke(this.data)
    }
}


fun <T, F> Resource<T, F>.ifFailure(func: (throwable: Throwable?, errorData: F?) -> Unit) {
    if (state == Resource.State.ERROR) {
        func.invoke(this.exception, this.errorData)
    }
}

fun <T, F> Resource<T, F>.ifCancel(func: (throwable: Throwable?, errorData: F?) -> Unit) {
    if (state == Resource.State.CANCEL) {
        func.invoke(this.exception, this.errorData)
    }
}


fun <T, F> Resource<T, F>.ifLoading(func: () -> Unit) {
    if (state == Resource.State.LOADING || state == Resource.State.LOADING_MORE) {
        func.invoke()
    }
}

fun <T : Any, F> PaginatedResource<T, F>.ifLoading(func: () -> Unit) {
    if (state == Resource.State.LOADING || state == Resource.State.LOADING_MORE) {
        func.invoke()
    }
}

fun <T : Any, F> PaginatedResource<T, F>.ifComplete(func: () -> Unit) {
    if (isEndState()) {
        func.invoke()
    }
}

fun <T : Any, F> PaginatedResource<T, F>.ifFailure(func: (throwable: Throwable?, errorData: F?) -> Unit) {
    if (state == Resource.State.ERROR) {
        func.invoke(this.exception, this.errorData)
    }
}


fun <T, F, J> Resource<T, F>.map(transform: (T?) -> J?): Resource<J, F> {
    return when (state) {
        Resource.State.NONE -> Resource.empty<J, F>()
        Resource.State.LOADING -> Resource.loading(transform(data))
        Resource.State.SUCCESS -> Resource.success(transform(data))
        Resource.State.ERROR -> Resource.error(transform(data), exception, errorData)
        Resource.State.CANCEL -> Resource.cancel(transform(data), exception, errorData)
        Resource.State.LOADING_MORE -> Resource.loadingMore(transform(data))
    }
}

