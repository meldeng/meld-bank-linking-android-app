package io.meld.example.ui.connections.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.meld.MainActivity
import io.meld.example.data.MeldData
import io.meld.example.databinding.FragmentInstitutionConnectionBinding
import io.meld.example.network.ifFailure
import io.meld.example.network.ifLoading
import io.meld.example.network.ifSuccess
import io.meld.example.utils.extension.hide
import io.meld.example.utils.extension.show
import io.meld.example.utils.showConnectError
import io.meld.example.utils.showNoInternetError
import io.meld.sdk.util.isInternetAvailable

// the fragment initialization parameters
private const val ARG_CONNECTION_ID = "connection_id"

/**
 * Use the [InstitutionConnectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InstitutionConnectionFragment : Fragment() {
    private lateinit var mBinding: FragmentInstitutionConnectionBinding
    private val mViewModel by viewModels<InstitutionConnectionViewModel>()
    val args: InstitutionConnectionFragmentArgs by navArgs()
    private val navController by lazy { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentInstitutionConnectionBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.getInstitutionConnection(args.id.orEmpty())

        mViewModel.institutionConnectionLiveData.observe(viewLifecycleOwner) {
            it.ifLoading {
                mBinding.progressbar.show()
            }
            it.ifSuccess {
                mBinding.progressbar.hide()
                mBinding.item = it
                if (it?.status == "DELETED") {
                    mBinding.buttonConnectionDelete.hide()
                }
            }
        }


        mViewModel.connectionRefreshLiveData.observe(viewLifecycleOwner) {
            it.ifLoading {
                mBinding.progressbar.show()
            }
            it.ifSuccess {
                mBinding.progressbar.hide()
                mViewModel.getInstitutionConnection(args.id.orEmpty())
            }
            it.ifFailure { throwable, errorData ->
                mBinding.progressbar.hide()
                requireContext().showConnectError(errorData)
            }
        }

        mViewModel.connectionDeleteLiveData.observe(viewLifecycleOwner) {
            it.ifLoading {
                mBinding.progressbar.show()
            }
            it.ifSuccess {
                mBinding.progressbar.hide()

                //Clear connection Data
                MeldData.institutionId = null
                MeldData.connect = null
                MeldData.institutionName = null

                val builder = AlertDialog.Builder(requireContext())
                val message = "Successfully deleted"
                builder.setMessage(message)
                builder.setPositiveButton(
                    "OK"
                ) { p0, p1 ->
                    //delete connection data and open app again
                    startActivity(Intent(context, MainActivity::class.java).apply { this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) })
                }
                builder.create().show()
            }
            it.ifFailure { throwable, errorData ->
                mBinding.progressbar.hide()
                context?.showConnectError(errorData)
            }
        }

        mBinding.buttonConnectionRefresh.setOnClickListener {
            if (!requireContext().isInternetAvailable()) {
                requireContext().showNoInternetError()
            } else {
                mViewModel.connectionRefresh(
                    args.id.orEmpty(),
                    arrayOf<String>("ACCOUNT_DETAILS")
                )
            }
        }

        mBinding.buttonConnectionDelete.setOnClickListener {
            if (!requireContext().isInternetAvailable()) {
                requireContext().showNoInternetError()
            } else {
                mViewModel.connectionDelete(args.id.orEmpty())
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param connectionId Connection id.
         * @return A new instance of fragment InstitutionConnectionFragment.
         */
        @JvmStatic
        fun newInstance(connectionId: String) =
            InstitutionConnectionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CONNECTION_ID, connectionId)
                }
            }
    }
}