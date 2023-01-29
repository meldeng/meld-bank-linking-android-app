package io.meld.banklinking.ui.transactions

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ravikoradiya.liveadapter.LiveAdapter
import io.meld.banklinking.BR
import io.meld.banklinking.R
import io.meld.banklinking.databinding.FragmentSearchFinancialTransactionsBinding
import io.meld.banklinking.databinding.RowFinancialAccountTransactionBinding
import io.meld.banklinking.network.ifComplete
import io.meld.banklinking.network.ifFailure
import io.meld.banklinking.network.ifLoading
import io.meld.banklinking.utils.extension.hide
import io.meld.banklinking.utils.extension.show
import io.meld.banklinking.utils.setOnPageChangeListener
import io.meld.banklinking.utils.showConnectError
import io.meld.banklinking.utils.showNoInternetError
import io.meld.sdk.model.response.FinancialAccountTransaction
import io.meld.sdk.util.isInternetAvailable
import java.text.SimpleDateFormat
import java.util.*

class SearchFinancialTransactionsFragment : Fragment() {
    private val TAG = "SearchFinancialTransact"

    private lateinit var mBinding: FragmentSearchFinancialTransactionsBinding
    private val safeArgs: SearchFinancialTransactionsFragmentArgs by navArgs()
    private val mViewModel by viewModels<SearchFinancialTransactionsViewModel> {
        SearchFinancialTransactionsViewModel.getFactory(
            safeArgs.id.orEmpty()
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentSearchFinancialTransactionsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mViewModel.financialAccountTransactionsLiveData.observe(viewLifecycleOwner) {
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

        mBinding.rvAccounts.setOnPageChangeListener(mViewModel.financialAccountTransactionsLiveData) {
            mViewModel.loadMoreTransactions(safeArgs.id.orEmpty())
        }

        setListData()

        mBinding.buttonSearch.setOnClickListener {

            val startDate = mBinding.textStartDate.text.toString()
            val endDate = mBinding.textEndDate.text.toString()

            mViewModel.searchFinancialAccountTransactions(
                safeArgs.id.orEmpty(),
                startDate,
                endDate,
                null
            )

        }
        mBinding.textStartDate.setOnClickListener {
            val cal = Calendar.getInstance()   // used for initializing the date picker
            val datePickerDialog = DatePickerDialog(
                requireContext(), DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, y)
                    selectedDate.set(Calendar.MONTH, m)
                    selectedDate.set(Calendar.DAY_OF_MONTH, d)
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    mBinding.textStartDate.setText(formatter.format(selectedDate.time))
//                    mBinding.textStartDate.setText("${y}-${m}-${d}")
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()

        }

        mBinding.textEndDate.setOnClickListener {
            val cal = Calendar.getInstance()   // used for initializing the date picker
            val datePickerDialog = DatePickerDialog(
                requireContext(), DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, y)
                    selectedDate.set(Calendar.MONTH, m)
                    selectedDate.set(Calendar.DAY_OF_MONTH, d)
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    mBinding.textEndDate.setText(formatter.format(selectedDate.time))
//                    mBinding.textEndDate.setText("${y}-${m}-${d}")
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()

        }
    }

    private fun setListData() {
        LiveAdapter(mViewModel.financialAccountTransactions, viewLifecycleOwner, BR.item)
            .map<FinancialAccountTransaction, RowFinancialAccountTransactionBinding>(
                R.layout.row_financial_account_transaction
            ) {
                onClick {
                    val item = it.binding.item

                    item?.let {

                        if (!requireContext().isInternetAvailable()) {
                            requireContext().showNoInternetError()
                        } else {
                            val action =
                                SearchFinancialTransactionsFragmentDirections.actionSearchFinancialTransactionsFragmentToFinancialTransactionFragment(
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