package io.meld.example.ui.institution.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.meld.example.network.PaginatedResource
import io.meld.example.network.Resource
import io.meld.example.network.ifSuccess
import io.meld.sdk.MeldSDK
import io.meld.sdk.enums.Position
import io.meld.sdk.listener.OnInstitutionListener
import io.meld.sdk.listener.OnSearchInstituteListener
import io.meld.sdk.model.MeldError
import io.meld.sdk.model.response.FinancialAccount
import io.meld.sdk.model.response.Institution
import io.meld.sdk.model.response.SearchInstitutionResponse

/**
 * Created by Dharmesh on 15-11-2022.
 *
 *
 */
class InstitutionViewModel : ViewModel() {

    private val mutableInstitutionsLiveData =
        MutableLiveData<Resource<Institution, MeldError>>()

    val institutionsLiveData: LiveData<Resource<Institution, MeldError>> =
        mutableInstitutionsLiveData


    fun getInstitution(institutionId: String) {
        mutableInstitutionsLiveData.value = Resource.loading()
        MeldSDK.getInstitution(institutionId, object : OnInstitutionListener {
            override fun onInstitution(response: Institution?, error: MeldError?) {
                response?.let {
                    mutableInstitutionsLiveData.value = Resource.success(it)
                } ?: kotlin.run {
                    mutableInstitutionsLiveData.value = Resource.error(error)
                }
            }
        })
    }

}



