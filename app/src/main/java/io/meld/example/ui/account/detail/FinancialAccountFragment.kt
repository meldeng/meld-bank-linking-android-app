package io.meld.example.ui.account.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.meld.example.R
import io.meld.example.data.MeldData
import io.meld.example.databinding.FragmentFinancialAccountBinding
import io.meld.example.network.ifFailure
import io.meld.example.network.ifLoading
import io.meld.example.network.ifSuccess
import io.meld.example.ui.transactions.SearchFinancialTransactionsFragment
import io.meld.example.utils.extension.hide
import io.meld.example.utils.extension.show
import io.meld.example.utils.showConnectError
import io.meld.example.utils.showMessage
import io.meld.example.utils.showNoInternetError
import io.meld.sdk.MeldSDK
import io.meld.sdk.enums.Processors
import io.meld.sdk.enums.Products
import io.meld.sdk.listener.OnCreateProcessorTokenListener
import io.meld.sdk.listener.OnFinancialAccountListener
import io.meld.sdk.model.MeldError
import io.meld.sdk.model.response.FinancialAccount
import io.meld.sdk.model.response.ProcessorTokenResponse
import io.meld.sdk.util.isInternetAvailable
import kotlin.reflect.KClass

class FinancialAccountFragment : Fragment() {

    private lateinit var mBinding: FragmentFinancialAccountBinding
    private lateinit var financialAccount: FinancialAccount
    private val mViewModel by viewModels<FinancialAccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentFinancialAccountBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //"WGunw6QmVjoeVbmzp1bVag"

        //Load spinner values
        val processors = Processors::class.enumConstantNames()
        val spinnerArrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.spinner_item, processors)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item) // The drop down view
        mBinding.spinnerProcessors.adapter = spinnerArrayAdapter

        val sageArgs: FinancialAccountFragmentArgs by navArgs()
        mViewModel.getFinancialAccount(sageArgs.id.orEmpty())

        mViewModel.financialAccountLiveData.observe(viewLifecycleOwner) {
            it.ifLoading {
                mBinding.progressbar.show()
            }
            it.ifSuccess {
                mBinding.progressbar.hide()
                it?.let {
                    financialAccount = it
                    mBinding.item = it
                }
            }
            it.ifFailure { throwable, errorData ->
                mBinding.progressbar.hide()
                requireContext().showConnectError(errorData)
            }
        }

        mViewModel.processorTokenLiveData.observe(viewLifecycleOwner) {
            it.ifLoading {
                mBinding.progressbar.show()
            }
            it.ifSuccess {
                mBinding.progressbar.hide()
                it?.let {
                    requireContext().showMessage(it.toString())
                }
            }
            it.ifFailure { throwable, errorData ->
                mBinding.progressbar.hide()
                requireContext().showConnectError(errorData)
            }
        }

        mBinding.buttonCreateProcessorToken.setOnClickListener {
            if (!requireContext().isInternetAvailable()) {
                requireContext().showNoInternetError()
            } else {
                val serviceProvider =
                    if (financialAccount.serviceProviderDetails.orEmpty()
                            .isNotEmpty()
                    ) financialAccount.serviceProviderDetails?.get(
                        0
                    )?.serviceProvider.orEmpty() else null

                mViewModel.createProcessorToken(
                    sageArgs.id.orEmpty(),
                    mBinding.spinnerProcessors.selectedItem.toString(),
                    serviceProvider
                )
            }
        }

        mBinding.buttonShowTransactions.setOnClickListener {
            if (!requireContext().isInternetAvailable()) {
                requireContext().showNoInternetError()
            } else {
                val action =
                    FinancialAccountFragmentDirections.actionFinancialAccountFragmentToSearchFinancialTransactionsFragment(
                        mBinding.item?.id
                    )
                findNavController().navigate(action)

            }
        }
    }

    fun KClass<out Enum<*>>.enumConstantNames() =
        this.java.enumConstants.map(Enum<*>::name)
}