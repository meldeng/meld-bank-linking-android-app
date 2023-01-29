package io.meld.banklinking.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.meld.banklinking.data.MeldData
import io.meld.banklinking.network.PaginatedResource
import io.meld.sdk.MeldSDK
import io.meld.sdk.enums.Position
import io.meld.sdk.listener.OnSearchFinancialAccountListener
import io.meld.sdk.model.MeldError
import io.meld.sdk.model.response.FinancialAccount
import io.meld.sdk.model.response.SearchFinancialAccountResponse

/**
 * Created by Dharmesh on 15-11-2022.
 *
 *
 */
class SearchFinancialAccountViewModel : ViewModel() {
    private val mutableFinancialAccountsLiveData =
        MutableLiveData<PaginatedResource<FinancialAccount, MeldError>>()

    val financialAccountsLiveData: LiveData<PaginatedResource<FinancialAccount, MeldError>> =
        mutableFinancialAccountsLiveData
    val financialAccounts: LiveData<List<FinancialAccount>> =
        Transformations.map(mutableFinancialAccountsLiveData) { it.data }


    private fun searchFinancialAccounts(paginationKey: String? = null) {
        val resource = mutableFinancialAccountsLiveData.value ?: PaginatedResource.getInstance()
        mutableFinancialAccountsLiveData.value = resource.loading()

        MeldSDK.searchFinancialAccount(
            customerId = MeldData.connect?.customerId,
            institutionId = null,
            connectionId = null, //MeldData.connect?.id,
            limit = 20,
            position = if (paginationKey == null) null else Position.BEFORE(paginationKey),
            listener = object : OnSearchFinancialAccountListener {
                override fun onSearchFinancialAccount(
                    response: SearchFinancialAccountResponse?,
                    error: MeldError?
                ) {
                    response?.let {
                        mutableFinancialAccountsLiveData.value =
                            resource.success(it.financialAccounts, it.count, it.remaining)
                    } ?: kotlin.run {
                        mutableFinancialAccountsLiveData.value = resource.error(error)
                    }
                }
            }
        )
    }


    public fun loadMoreAccounts() {
        searchFinancialAccounts(mutableFinancialAccountsLiveData.value?.data?.lastOrNull()?.key)
    }

    init {
        searchFinancialAccounts()
    }
}



