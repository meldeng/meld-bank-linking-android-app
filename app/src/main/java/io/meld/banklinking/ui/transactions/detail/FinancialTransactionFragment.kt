package io.meld.banklinking.ui.transactions.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import io.meld.banklinking.databinding.FragmentFinancialTransactionBinding
import io.meld.banklinking.network.ifFailure
import io.meld.banklinking.network.ifLoading
import io.meld.banklinking.network.ifSuccess
import io.meld.banklinking.utils.extension.hide
import io.meld.banklinking.utils.extension.show
import io.meld.banklinking.utils.showConnectError

class FinancialTransactionFragment : Fragment() {

    private lateinit var mBinding: FragmentFinancialTransactionBinding
    private val mViewModel by viewModels<FinancialTransactionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentFinancialTransactionBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeArgs : FinancialTransactionFragmentArgs by navArgs()
        mViewModel.getFinancialTransaction(safeArgs.id.orEmpty())

        mViewModel.financialTransactionLiveData.observe(viewLifecycleOwner) {
            it.ifLoading {
                mBinding.progressbar.show()
            }
            it.ifSuccess {
                mBinding.progressbar.hide()
                mBinding.item = it
            }
            it.ifFailure { throwable, errorData ->
                mBinding.progressbar.hide()
                requireContext().showConnectError(errorData)
            }
        }
    }
}
