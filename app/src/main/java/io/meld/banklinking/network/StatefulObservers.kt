package io.meld.banklinking.network

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.meld.banklinking.utils.extension.orFalse


fun <J : Resource<T, F>, T, F> LiveData<J>.observeStatefully(
    lifecycleOwner: LifecycleOwner,
    observerFunction: (resource: J) -> Unit
) {
    val liveData = this
    val internalObserver = object : Observer<J> {
        override fun onChanged(resource: J) {
            observerFunction.invoke(resource)
            if (resource.isEndState().orFalse()) {
                liveData.removeObserver(this)
            }
        }
    }
    observe(lifecycleOwner, internalObserver)
}
