package io.meld.banklinking.ui.config

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import io.meld.MainActivity
import io.meld.banklinking.databinding.FragmentConfigBinding
import io.meld.banklinking.R
import io.meld.banklinking.data.MeldData
import io.meld.banklinking.network.ifFailure
import io.meld.banklinking.network.ifLoading
import io.meld.banklinking.network.ifSuccess
import io.meld.banklinking.utils.extension.hide
import io.meld.banklinking.utils.extension.hideKeyboard
import io.meld.banklinking.utils.extension.orTrue
import io.meld.banklinking.utils.extension.show
import io.meld.banklinking.utils.showConnectError
import io.meld.banklinking.utils.showMessage
import io.meld.sdk.MeldSDK
import io.meld.sdk.enums.Products
import io.meld.sdk.env.Environment
import io.meld.sdk.model.MeldConfiguration
import io.meld.sdk.util.isInternetAvailable

class ConfigFragment : Fragment() {

    private lateinit var mBinding: FragmentConfigBinding
    private val mViewModel by viewModels<ConfigViewModel>()
    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentConfigBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if(MeldData.apiKey?.isEmpty().orTrue()){
            MeldData.apiKey = "W2a8zHdgnNoJQVWNyhFhBx:BoyNyR2Vzkuv1Ty6oLxBfvYGgQhhkGQBs4uhB"
        }
        //Set default data
        mBinding.etAPIKey.setText(MeldData.apiKey)

        MeldData.environment?.toIntOrNull()?.let {
            mBinding.rgEnvironment.check(it)
        }
        //Set default External customer Id
        mBinding.etExternalCustomerId.setText(MeldData.externalCustomerId)

        //set products data
        mBinding.chkBalance.text = Products.BALANCES.name
        mBinding.chkTransactions.text = Products.TRANSACTIONS.name


        //set selected data
        MeldData.products?.forEach { product ->
            when (Products.valueOf(product)) {
                Products.BALANCES -> mBinding.chkBalance.isChecked = true
                Products.TRANSACTIONS -> mBinding.chkTransactions.isChecked = true
                Products.OWNERS -> mBinding.chkBalance.isChecked = true
                Products.IDENTIFIERS -> mBinding.chkBalance.isChecked = true
                Products.INVESTMENT_HOLDINGS -> mBinding.chkBalance.isChecked = true
                Products.INVESTMENT_TRANSACTIONS -> mBinding.chkBalance.isChecked = true
            }
        }

        mViewModel.connectionLiveData.observe(viewLifecycleOwner) {
            it.ifLoading {
                mBinding.progressbar.show()
            }
            it.ifSuccess {
                mBinding.progressbar.hide()
                MeldData.connect = it
                startActivity(Intent(context, MainActivity::class.java))
                activity?.finish()
            }
            it.ifFailure { throwable, errorData ->
                mBinding.progressbar.hide()
                requireContext().showConnectError(errorData)
            }
        }

        mBinding.btnConnect.setOnClickListener {
            mBinding.root.hideKeyboard()
            if (!requireContext().isInternetAvailable()) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage(getString(R.string.connection_error_message))
                builder.setPositiveButton(
                    getString(R.string.text_ok)
                ) { p0, p1 -> }
                builder.create().show()
                return@setOnClickListener
            }

            if (mBinding.rgEnvironment.checkedRadioButtonId == -1) {
                Toast.makeText(
                    requireContext(),
                    "Please select environment",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }


            val apiKey = mBinding.etAPIKey.text.toString()
            if (apiKey.isEmpty()) {
                mBinding.tilApiKey.error = "Please enter api key"
                Toast.makeText(
                    requireContext(),
                    "Please enter api key",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            mBinding.tilApiKey.error = null


            val externalId = mBinding.etExternalCustomerId.text.toString()
            if (externalId.isEmpty()) {
                mBinding.tilExternalCustomerId.error = "Please enter valid external customer id"
                Toast.makeText(
                    requireContext(),
                    "Please enter valid external customer id",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            mBinding.tilExternalCustomerId.error = null


            val selectedProducts = createProductsList()
            if (selectedProducts.size == 0) {
                requireContext().showMessage("Select at least one product")
                return@setOnClickListener
            }

            mBinding.progressbar.visibility = View.VISIBLE

            MeldData.environment = (mBinding.rgEnvironment.checkedRadioButtonId ?: -1).toString()
            MeldData.externalCustomerId = externalId
            MeldData.apiKey = apiKey
            MeldData.products = selectedProducts
            reInitSdk()
            mViewModel.getConnectToken(products = selectedProducts)
        }

    }

    private fun createProductsList(): ArrayList<String> {
        val selectedProducts = ArrayList<String>()

        if (mBinding.chkBalance.isChecked) {
            selectedProducts.add(Products.BALANCES.name)
        }

        if (mBinding.chkTransactions.isChecked) {
            selectedProducts.add(Products.TRANSACTIONS.name)
        }
        return selectedProducts;

    }

    private fun reInitSdk() {


        MeldData.connect = null
        MeldData.institutionId = null
        MeldData.institutionName = null

        var environment: Environment = Environment.QA
        when (mBinding.rgEnvironment.checkedRadioButtonId) {
            R.id.rbProduction -> {
                environment = Environment.PRODUCTION
            }
            R.id.rbSandbox -> {
                environment = Environment.SANDBOX
            }
            R.id.rbQa -> {
                environment = Environment.QA
            }
            else -> {
            }
        }

        val meldConfiguration = MeldConfiguration(
            apiVersion = "2022-09-21",
            isDebugLogEnabled = true
        )

        MeldSDK.init(
            apiKey = MeldData.apiKey.orEmpty(),
            externalCustomerId = MeldData.externalCustomerId,
            environment = environment,
            meldConfiguration
        )
    }


}