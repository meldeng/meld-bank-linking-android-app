package io.meld.example.utils

import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.meld.example.network.PaginatedResource
import io.meld.example.utils.extension.orZero

/**
 * Sets the page change listener and provides the callback for page change
 * @param callback Provide a callback with page number
 * @return Instance of page change listener
 */
fun <T : Any, F> RecyclerView.setOnPageChangeListener(
    liveData: LiveData<PaginatedResource<T, F>>,
    callback: (page: Int) -> Unit
): OnPageChangeListener {

    var mPageNo = 1

    val listener: OnPageChangeListener = object : OnPageChangeListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            // check if pagination is enabled and current state is not loading
            if (paginationEnabled && liveData.value?.isLoading() == false && liveData.value?.isLastPage() == false) {
                // check for load more
                val layoutManager = recyclerView.layoutManager
                if (layoutManager is LinearLayoutManager) {
                    val lastItem = layoutManager.findLastVisibleItemPosition()
                    if (lastItem >= recyclerView.adapter?.itemCount.orZero() - 2) {
                        mPageNo++
                        callback(mPageNo)
                    }
                }
            }
        }

        override fun resetPage(toPage: Int) {
            mPageNo = toPage
        }
    }
    addOnScrollListener(listener)
    return listener
}

/**
 * Class used by the add on page change listener
 */
abstract class OnPageChangeListener : RecyclerView.OnScrollListener() {


    /**
     * States that pagination is enabled or not
     * Useful when there is no more data and can be set as false to disable it
     */
    var paginationEnabled = true

    /**
     * Resets the current page to provided page number.
     * Default is 1
     * @param toPage page no you want to change page to (optional) (default: 1)
     */
    abstract fun resetPage(toPage: Int = 1)

}