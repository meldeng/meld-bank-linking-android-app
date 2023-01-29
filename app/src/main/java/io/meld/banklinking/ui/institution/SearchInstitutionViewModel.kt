package io.meld.banklinking.ui.institution

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.meld.banklinking.network.PaginatedResource
import io.meld.sdk.MeldSDK
import io.meld.sdk.enums.Position
import io.meld.sdk.listener.OnSearchInstituteListener
import io.meld.sdk.model.MeldError
import io.meld.sdk.model.response.Institution
import io.meld.sdk.model.response.SearchInstitutionResponse

/**
 * Created by Dharmesh on 15-11-2022.
 *
 *
 */
class SearchInstitutionViewModel : ViewModel() {
    private val mutableInstitutionsLiveData =
        MutableLiveData<PaginatedResource<Institution, MeldError>>()

    val institutionsLiveData: LiveData<PaginatedResource<Institution, MeldError>> =
        mutableInstitutionsLiveData
    val institutions: LiveData<List<Institution>> =
        Transformations.map(mutableInstitutionsLiveData) { it.data }


    private fun searchInstitution(paginationKey: String? = null) {
        val resource = mutableInstitutionsLiveData.value ?: PaginatedResource.getInstance()
        mutableInstitutionsLiveData.value = resource.loading()
        MeldSDK.searchInstitutions(
            limit = 20,
            position = if (paginationKey == null) null else Position.AFTER(paginationKey),
            listener = object : OnSearchInstituteListener {
                override fun onSearchInstitute(
                    response: SearchInstitutionResponse?,
                    error: MeldError?
                ) {
                    response?.let {
                        mutableInstitutionsLiveData.value =
                            resource.success(it.institutions, it.count, it.remaining)
                    } ?: kotlin.run {
                        mutableInstitutionsLiveData.value = resource.error(error)
                    }
                }
            })
    }


    public fun loadMoreInstitutions() {
        searchInstitution(mutableInstitutionsLiveData.value?.data?.lastOrNull()?.key)
    }

    init {
        searchInstitution()
    }
}



