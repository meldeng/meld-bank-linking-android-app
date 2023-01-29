package io.meld.banklinking.ui.connections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.meld.banklinking.data.MeldData
import io.meld.banklinking.network.PaginatedResource
import io.meld.sdk.MeldSDK
import io.meld.sdk.enums.Position
import io.meld.sdk.listener.OnSearchInstitutionConnectionListener
import io.meld.sdk.model.MeldError
import io.meld.sdk.model.response.InstitutionConnection
import io.meld.sdk.model.response.SearchInstitutionConnectionResponse

/**
 * Created by Dharmesh on 15-11-2022.
 *
 *
 */
class SearchInstitutionConnectionsViewModel : ViewModel() {

    private val mutableConnectionsLiveData =
        MutableLiveData<PaginatedResource<InstitutionConnection, MeldError>>()

    val connectionsLiveData: LiveData<PaginatedResource<InstitutionConnection, MeldError>> =
        mutableConnectionsLiveData
    val connections: LiveData<List<InstitutionConnection>> =
        Transformations.map(mutableConnectionsLiveData) { it.data }

    private fun searchInstitutionConnection(paginationKey: String?) {
        val resource = mutableConnectionsLiveData.value ?: PaginatedResource.getInstance()
        mutableConnectionsLiveData.value = resource.loading()
        MeldSDK.searchInstitutionConnections(
            customerId = MeldData.connect?.customerId,
            institutionId = MeldData.institutionId,
            statuses = null,
            serviceProviders = null,
            limit = 20,
            position = if (paginationKey == null) null else Position.BEFORE(paginationKey),
            listener = object : OnSearchInstitutionConnectionListener {
                override fun onSearchInstitutionConnection(
                    response: SearchInstitutionConnectionResponse?,
                    error: MeldError?
                ) {
                    response?.let {
                        mutableConnectionsLiveData.value =
                            resource.success(it.connections, it.count, it.remaining)
                    } ?: kotlin.run {
                        mutableConnectionsLiveData.value = resource.error(error)
                    }
                }
            })
    }

    fun loadMoreConnections() {
        searchInstitutionConnection(mutableConnectionsLiveData.value?.data?.lastOrNull()?.key)
    }

    init {
        searchInstitutionConnection(null)
    }

}