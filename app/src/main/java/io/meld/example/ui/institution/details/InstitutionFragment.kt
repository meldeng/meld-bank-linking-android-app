package io.meld.example.ui.institution.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import io.meld.example.databinding.FragmentInstitutionBinding
import io.meld.example.data.MeldData
import io.meld.example.network.ifComplete
import io.meld.example.network.ifFailure
import io.meld.example.network.ifLoading
import io.meld.example.network.ifSuccess
import io.meld.example.ui.connections.detail.InstitutionConnectionFragmentArgs
import io.meld.example.utils.extension.hide
import io.meld.example.utils.extension.show
import io.meld.example.utils.showConnectError
import io.meld.example.utils.showNoInternetError
import io.meld.sdk.MeldSDK
import io.meld.sdk.listener.OnInstitutionListener
import io.meld.sdk.model.MeldError
import io.meld.sdk.model.response.Institution
import io.meld.sdk.util.isInternetAvailable

class InstitutionFragment : Fragment() {

    private lateinit var mBinding: FragmentInstitutionBinding
    private val mViewModel by viewModels<InstitutionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentInstitutionBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeArgs: InstitutionFragmentArgs by navArgs()
        mViewModel.getInstitution(safeArgs.id.orEmpty())

        mViewModel.institutionsLiveData.observe(viewLifecycleOwner) {
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