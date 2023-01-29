package io.meld.banklinking.ui.connections.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.meld.banklinking.network.Resource
import io.meld.sdk.MeldSDK
import io.meld.sdk.listener.OnDeleteConnectionListener
import io.meld.sdk.listener.OnInstitutionConnectionListener
import io.meld.sdk.listener.OnRefreshAccountListener
import io.meld.sdk.model.MeldError
import io.meld.sdk.model.response.InstitutionConnection
import io.meld.sdk.model.response.RefreshAccountResponse

/**
 * Created by Dharmesh on 15-11-2022.
 *
 *
 */
class InstitutionConnectionViewModel : ViewModel() {

    private val mutableInstitutionConnectionLiveData =
        MutableLiveData<Resource<InstitutionConnection, MeldError>>()

    val institutionConnectionLiveData: LiveData<Resource<InstitutionConnection, MeldError>> =
        mutableInstitutionConnectionLiveData

    val institutionConnection: LiveData<InstitutionConnection> =
        Transformations.map(mutableInstitutionConnectionLiveData) { it.data }

    private val mutableConnectionRefreshLiveData =
        MutableLiveData<Resource<RefreshAccountResponse, MeldError>>()

    val connectionRefreshLiveData: LiveData<Resource<RefreshAccountResponse, MeldError>> =
        mutableConnectionRefreshLiveData


    private val mutableConnectionDeleteLiveData =
        MutableLiveData<Resource<Any, MeldError>>()

    val connectionDeleteLiveData: LiveData<Resource<Any, MeldError>> =
        mutableConnectionDeleteLiveData


    fun getInstitutionConnection(connectionId: String) {
        mutableInstitutionConnectionLiveData.value = Resource.loading()
        MeldSDK.getConnection(connectionId, object : OnInstitutionConnectionListener {
            override fun onInstitutionConnection(
                response: InstitutionConnection?,
                error: MeldError?
            ) {
                response?.let {
                    mutableInstitutionConnectionLiveData.value = Resource.success(it)
                } ?: kotlin.run {
                    mutableInstitutionConnectionLiveData.value = Resource.error(error)
                }
            }
        })
    }


    fun connectionRefresh(connectionId: String, products: Array<String>) {
        mutableConnectionRefreshLiveData.value = Resource.loading()
        MeldSDK.connectionRefresh(connectionId, products, object : OnRefreshAccountListener {
            override fun onRefreshAccount(response: RefreshAccountResponse?, error: MeldError?) {
                response?.let {
                    mutableConnectionRefreshLiveData.value = Resource.success(it)
                } ?: kotlin.run {
                    mutableConnectionRefreshLiveData.value = Resource.error(error)
                }
            }
        })
    }

    fun connectionDelete(connectionId: String) {
        mutableConnectionDeleteLiveData.value = Resource.loading()
        MeldSDK.deleteConnection(connectionId, object : OnDeleteConnectionListener {
            override fun onDeleteConnection(response: Any?, error: MeldError?) {
                if (error != null) {
                    mutableConnectionDeleteLiveData.value = Resource.error(error)
                } else {
                    mutableConnectionDeleteLiveData.value = Resource.success(response)
                }
            }
        })
    }
}



