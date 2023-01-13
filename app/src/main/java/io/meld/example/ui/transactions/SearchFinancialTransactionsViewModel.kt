package io.meld.example.ui.transactions

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import io.meld.example.data.MeldData
import io.meld.example.network.PaginatedResource
import io.meld.sdk.MeldSDK
import io.meld.sdk.enums.Position
import io.meld.sdk.listener.OnSearchAccountTransactionListener
import io.meld.sdk.model.MeldError
import io.meld.sdk.model.response.FinancialAccountTransaction
import io.meld.sdk.model.response.SearchAccountTransactionResponse

/**
 * Created by Dharmesh on 15-11-2022.
 *
 *
 */
class SearchFinancialTransactionsViewModel(id: String) : ViewModel() {
    private val mutableFinancialTransactionsLiveData =
        MutableLiveData<PaginatedResource<FinancialAccountTransaction, MeldError>>()

    val financialAccountTransactionsLiveData: LiveData<PaginatedResource<FinancialAccountTransaction, MeldError>> =
        mutableFinancialTransactionsLiveData
    val financialAccountTransactions: LiveData<List<FinancialAccountTransaction>> =
        Transformations.map(mutableFinancialTransactionsLiveData) { it.data }


    public fun searchFinancialAccountTransactions(
        financialAccountId: String,
        startDate: String?,
        endDate: String?,
        paginationKey: String? = null
    ) {
        val resource = mutableFinancialTransactionsLiveData.value ?: PaginatedResource.getInstance()
        mutableFinancialTransactionsLiveData.value = resource.loading()

        MeldSDK.searchAccountTransactions(
            customerId = MeldData.connect?.customerId,
            financialAccountId = financialAccountId,
            startDate = startDate,
            endDate = endDate,
            limit = 20,
            position = if (paginationKey == null) null else Position.BEFORE(paginationKey),
            listener = object : OnSearchAccountTransactionListener {
                override fun onSearchAccountTransaction(
                    response: SearchAccountTransactionResponse?,
                    error: MeldError?
                ) {
                    response?.let {
                        mutableFinancialTransactionsLiveData.value =
                            resource.success(
                                it.financialAccountTransactions,
                                it.count,
                                it.remaining
                            )
                    } ?: kotlin.run {
                        mutableFinancialTransactionsLiveData.value = resource.error(error)
                    }
                }
            }
        )
    }


    public fun loadMoreTransactions(accountId: String) {
        searchFinancialAccountTransactions(accountId, null, null, null)
    }

    init {
        searchFinancialAccountTransactions(id, null, null, null)
    }

    // Define ViewModel factory in a companion object
    companion object {
        public fun getFactory(id: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    return SearchFinancialTransactionsViewModel(id) as T
                }
            }
    }
}



