package io.meld.banklinking.ui.connections

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
import io.meld.banklinking.databinding.FragmentSearchConnectionsBinding
import io.meld.banklinking.databinding.RowInstitutionConnectionBinding
import io.meld.banklinking.network.ifComplete
import io.meld.banklinking.network.ifFailure
import io.meld.banklinking.network.ifLoading
import io.meld.banklinking.utils.extension.hide
import io.meld.banklinking.utils.extension.show
import io.meld.banklinking.utils.setOnPageChangeListener
import io.meld.banklinking.utils.showConnectError
import io.meld.banklinking.utils.showNoInternetError
import io.meld.sdk.model.response.InstitutionConnection
import io.meld.sdk.util.isInternetAvailable


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class SearchConnectionsFragment : Fragment() {

    private lateinit var mBinding: FragmentSearchConnectionsBinding
    private val mViewModel by viewModels<SearchInstitutionConnectionsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentSearchConnectionsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.connectionsLiveData.observe(viewLifecycleOwner) {
            it.ifLoading {
                mBinding.progressbar.show()
            }

            it.ifComplete {
                mBinding.progressbar.hide()
            }
            it.ifFailure { throwable, errorData ->
                mBinding.progressbar.hide()
                requireContext().showConnectError(errorData)
            }

        }

        mBinding.recyclerViewConnections.setOnPageChangeListener(mViewModel.connectionsLiveData) {
            mViewModel.loadMoreConnections()
        }
        setData()
    }

    private fun setData() {
        LiveAdapter(mViewModel.connections, viewLifecycleOwner, BR.item)
            .map<InstitutionConnection, RowInstitutionConnectionBinding>(R.layout.row_institution_connection) {
                onClick {
                    val item = it.binding.item
                    item?.let {
                        if (!requireContext().isInternetAvailable()) {
                            requireContext().showNoInternetError()
                        } else {
                            val action = SearchConnectionsFragmentDirections.actionSearchConnectionsFragmentToInstitutionConnectionFragment(item.id)
                            findNavController().navigate(action)
                        }
                    }
                }
            }.into(mBinding.recyclerViewConnections)
    }
}