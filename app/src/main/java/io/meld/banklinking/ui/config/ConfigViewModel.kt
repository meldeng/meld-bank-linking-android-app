package io.meld.banklinking.ui.config

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.meld.banklinking.data.MeldData
import io.meld.banklinking.network.Resource
import io.meld.sdk.MeldSDK
import io.meld.sdk.listener.OnConnectListener
import io.meld.sdk.model.MeldError
import io.meld.sdk.model.response.ConnectResponse

class ConfigViewModel : ViewModel() {

    private val mutableConnectResponseLiveData =
        MutableLiveData<Resource<ConnectResponse, MeldError>>()

    val connectionLiveData: LiveData<Resource<ConnectResponse, MeldError>> =
        mutableConnectResponseLiveData

    val connection: LiveData<ConnectResponse> =
        Transformations.map(mutableConnectResponseLiveData) { it.data }


    fun getConnectToken(products: List<String>) {
        MeldSDK.getConnectToken(
            MeldData.externalCustomerId,
            true,
            products,
            listOf("US", "CA"),
            object : OnConnectListener {
                override fun onConnect(response: ConnectResponse?, error: MeldError?) {
                    response?.let {
                        mutableConnectResponseLiveData.value = Resource.success(it)
                    } ?: kotlin.run {
                        mutableConnectResponseLiveData.value = Resource.error(error)
                    }

                }
            })
    }

}