package io.meld.example.ui.transactions.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.meld.example.network.Resource
import io.meld.sdk.MeldSDK
import io.meld.sdk.listener.OnAccountTransactionListener
import io.meld.sdk.listener.OnInstitutionListener
import io.meld.sdk.model.MeldError
import io.meld.sdk.model.response.FinancialAccountTransaction
import io.meld.sdk.model.response.Institution

/**
 * Created by Dharmesh on 15-11-2022.
 *
 *
 */
class FinancialTransactionViewModel : ViewModel() {

    private val mutableFinancialTransactionLiveData =
        MutableLiveData<Resource<FinancialAccountTransaction, MeldError>>()

    val financialTransactionLiveData: LiveData<Resource<FinancialAccountTransaction, MeldError>> =
        mutableFinancialTransactionLiveData

    val financialTransaction: LiveData<FinancialAccountTransaction> =
        Transformations.map(mutableFinancialTransactionLiveData) { it.data }


    fun getFinancialTransaction(transactionId: String) {
        mutableFinancialTransactionLiveData.value = Resource.loading()
        MeldSDK.getTransaction(transactionId, object : OnAccountTransactionListener {
            override fun onAccountTransaction(
                response: FinancialAccountTransaction?, error: MeldError?
            ) {
                response?.let {
                    mutableFinancialTransactionLiveData.value = Resource.success(it)
                } ?: kotlin.run {
                    mutableFinancialTransactionLiveData.value = Resource.error(error)
                }
            }
        })
    }

}



