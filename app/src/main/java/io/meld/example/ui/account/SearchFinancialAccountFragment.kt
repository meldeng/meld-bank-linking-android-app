package io.meld.example.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ravikoradiya.liveadapter.LiveAdapter
import io.meld.example.BR
import io.meld.example.R
import io.meld.example.databinding.FragmentSearchFinancialAccountBinding
import io.meld.example.data.MeldData
import io.meld.example.databinding.RowFinancialAccountBinding
import io.meld.example.ui.account.detail.FinancialAccountFragment
import io.meld.example.network.ifComplete
import io.meld.example.network.ifFailure
import io.meld.example.network.ifLoading
import io.meld.example.ui.account.detail.FinancialAccountFragmentDirections
import io.meld.example.utils.extension.hide
import io.meld.example.utils.extension.show
import io.meld.example.utils.setOnPageChangeListener
import io.meld.example.utils.showConnectError
import io.meld.example.utils.showNoInternetError
import io.meld.sdk.model.response.FinancialAccount
import io.meld.sdk.util.isInternetAvailable

class SearchFinancialAccountFragment : Fragment() {

    private lateinit var mBinding: FragmentSearchFinancialAccountBinding
    private val mViewModel by viewModels<SearchFinancialAccountViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentSearchFinancialAccountBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.financialAccountsLiveData.observe(viewLifecycleOwner) {
            it.ifLoading {
                mBinding.progressbar.show()
            }
            it.ifComplete {
                mBinding.progressbar.hide()
                if (it.data.isEmpty()) {
                    mBinding.textNoData.show()
                } else {
                    mBinding.textNoData.hide()
                }
            }
            it.ifFailure { throwable, errorData ->
                mBinding.progressbar.hide()
                requireContext().showConnectError(errorData)
            }
        }

        mBinding.rvAccounts.setOnPageChangeListener(mViewModel.financialAccountsLiveData) {
            mViewModel.loadMoreAccounts()
        }

        setListData()
    }

    private fun setListData() {
        LiveAdapter(mViewModel.financialAccounts, viewLifecycleOwner, BR.item)
            .map<FinancialAccount, RowFinancialAccountBinding>(R.layout.row_financial_account) {
                onClick {
                    val item = it.binding.item

                    item?.let {
                        if (!requireContext().isInternetAvailable()) {
                            requireContext().showNoInternetError()
                        } else {
                            val action =
                                SearchFinancialAccountFragmentDirections.actionSearchFinancialAccountFragmentToFinancialAccountFragment(
                                    item.id
                                )
                            findNavController().navigate(action)
                        }
                    }

                }
            }
            .into(mBinding.rvAccounts)
    }

}