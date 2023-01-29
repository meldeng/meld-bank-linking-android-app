package io.meld.banklinking.ui.institution

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ravikoradiya.liveadapter.LiveAdapter
import io.meld.banklinking.BR
import io.meld.banklinking.R
import io.meld.banklinking.databinding.FragmentSearchInstitutionBinding
import io.meld.banklinking.databinding.RowInstitutionsBinding
import io.meld.banklinking.network.ifComplete
import io.meld.banklinking.network.ifFailure
import io.meld.banklinking.network.ifLoading
import io.meld.banklinking.utils.extension.hide
import io.meld.banklinking.utils.extension.show
import io.meld.banklinking.utils.setOnPageChangeListener
import io.meld.banklinking.utils.showConnectError
import io.meld.banklinking.utils.showNoInternetError
import io.meld.sdk.model.response.Institution
import io.meld.sdk.util.isInternetAvailable

class SearchInstitutionFragment : Fragment() {
    private lateinit var mBinding: FragmentSearchInstitutionBinding
    private val mViewModel by viewModels<SearchInstitutionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentSearchInstitutionBinding.inflate(inflater, container, false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.institutionsLiveData.observe(viewLifecycleOwner) {
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

        mBinding.rvAccounts.setOnPageChangeListener(mViewModel.institutionsLiveData) {
            mViewModel.loadMoreInstitutions()
        }

        setData()
    }

    private fun setData() {
        LiveAdapter(mViewModel.institutions, viewLifecycleOwner, BR.item)
            .map<Institution, RowInstitutionsBinding>(R.layout.row_institutions) {
                onClick {
                    val item = it.binding.item

                    if (!requireContext().isInternetAvailable()) {
                        requireContext().showNoInternetError()
                    } else {
                        val action =
                            SearchInstitutionFragmentDirections.actionSearchInstitutionFragmentToInstitutionFragment(
                                item?.id
                            )
                        findNavController().navigate(action)

                    }
                }
            }
            .into(mBinding.rvAccounts)
    }
}