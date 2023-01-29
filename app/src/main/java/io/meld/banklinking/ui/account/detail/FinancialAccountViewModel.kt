package io.meld.banklinking.ui.account.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.meld.banklinking.network.Resource
import io.meld.sdk.MeldSDK
import io.meld.sdk.listener.OnCreateProcessorTokenListener
import io.meld.sdk.listener.OnFinancialAccountListener
import io.meld.sdk.model.MeldError
import io.meld.sdk.model.response.FinancialAccount
import io.meld.sdk.model.response.ProcessorTokenResponse

/**
 * Created by Dharmesh on 15-11-2022.
 *
 *
 */
class FinancialAccountViewModel : ViewModel() {

    private val mutableFinancialAccountLiveData =
        MutableLiveData<Resource<FinancialAccount, MeldError>>()

    val financialAccountLiveData: LiveData<Resource<FinancialAccount, MeldError>> =
        mutableFinancialAccountLiveData

    val financialAccount: LiveData<FinancialAccount> =
        Transformations.map(mutableFinancialAccountLiveData) { it.data }


    private val mutableProcessorTokenLiveData =
        MutableLiveData<Resource<ProcessorTokenResponse, MeldError>>()
    val processorTokenLiveData: LiveData<Resource<ProcessorTokenResponse, MeldError>> =
        mutableProcessorTokenLiveData

    fun getFinancialAccount(accountId: String) {
        mutableFinancialAccountLiveData.value = Resource.loading()
        MeldSDK.getFinancialAccount(accountId, object : OnFinancialAccountListener {
            override fun onFinancialAccount(response: FinancialAccount?, error: MeldError?) {
                response?.let {
                    mutableFinancialAccountLiveData.value = Resource.success(it)
                } ?: kotlin.run {
                    mutableFinancialAccountLiveData.value = Resource.error(error)
                }
            }
        })

    }


    fun createProcessorToken(
        financialAccountId: String,
        processor: String,
        serviceProvider: String?
    ) {
        mutableProcessorTokenLiveData.value = Resource.loading()
        MeldSDK.createProcessorToken(
            financialAccountId,
            processor,
            serviceProvider?.trim(),
            object : OnCreateProcessorTokenListener {
                override fun onCreateProcessorToken(
                    response: ProcessorTokenResponse?,
                    error: MeldError?
                ) {
                    response?.let {
                        mutableProcessorTokenLiveData.value = Resource.success(it)
                    } ?: kotlin.run {
                        mutableProcessorTokenLiveData.value = Resource.error(error)
                    }
                }
            })
    }
}



