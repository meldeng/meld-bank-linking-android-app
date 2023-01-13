package io.meld.example.network


interface PaginatedStatefulResource<T, F> {

    /**
     * This method return if the resource is in it's end state. All the events had occurred
     * and there are no other events left to follow for them.
     *
     * @return true/false based if the current state is end state or not
     */
    fun isEndState(): Boolean

    fun isLastPage(): Boolean

    fun isLoading(): Boolean

}

class PaginatedResource<T : Any, F> private constructor() : PaginatedStatefulResource<T, F> {
    var state: Resource.State? = null
        private set
    var data: ArrayList<T> = ArrayList()
    var exception: Throwable? = null
    var errorData: F? = null
    var count: Int? = 0
    var remaining: Int? = 0


    companion object {
        fun <T : Any, F> getInstance() = PaginatedResource<T, F>()
    }


    fun success(data: List<T>?, count: Int, remaining: Int): PaginatedResource<T, F> {
        this.data.addAll(data.orEmpty())
        this.state = Resource.State.SUCCESS
        this.count = count
        this.remaining = remaining
        return this
    }

    fun error(
        data: List<T>?,
        throwable: Throwable?,
        errorData: F? = null
    ): PaginatedResource<T, F> {
        this.data.addAll(data.orEmpty())
        this.exception = throwable;
        state =
            Resource.State.ERROR
        this.errorData = errorData
        return this
    }

    fun error(

        errorData: F? = null
    ): PaginatedResource<T, F> {

        state =
            Resource.State.ERROR
        this.errorData = errorData
        return this
    }

    fun loading(data: List<T>? = null): PaginatedResource<T, F> {
        this.data.addAll(data.orEmpty())
        this.state =
            if (this.data.isEmpty()) Resource.State.LOADING else Resource.State.LOADING_MORE; return this
    }


    override fun isEndState(): Boolean =
        (state == Resource.State.ERROR) or (state == Resource.State.SUCCESS)

    override fun isLastPage(): Boolean =
        this.remaining == 0

    override fun isLoading(): Boolean =
        (state == Resource.State.LOADING) or (state == Resource.State.LOADING_MORE)
}