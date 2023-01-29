package io.meld.banklinking.ui.home

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.meld.banklinking.databinding.FragmentHomeBinding
import io.meld.banklinking.R
import io.meld.banklinking.data.MeldData
import io.meld.banklinking.utils.extension.hide
import io.meld.banklinking.utils.extension.show
import io.meld.banklinking.utils.showConnectError
import io.meld.banklinking.utils.showMessage
import io.meld.sdk.MeldSDK
import io.meld.sdk.enums.Products
import io.meld.sdk.listener.OnConnectListener
import io.meld.sdk.listener.OnHandOverListener
import io.meld.sdk.model.MeldError
import io.meld.sdk.model.response.ConnectResponse
import io.meld.sdk.model.response.ConnectionWidgetResponse
import io.meld.sdk.util.isInternetAvailable
import kotlin.reflect.KClass


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private val navController by lazy { findNavController() }
    private lateinit var binding: FragmentHomeBinding
    private val selectedProducts = ArrayList<String>() // Where we track the selected items

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Set default External customer Id
        binding.tvExternalCustomerId.text = MeldData.externalCustomerId

        //Load spinner values
        MeldData.products?.let {
            if (it.isNotEmpty()) binding.textSelectedProducts.show() else binding.textSelectedProducts.hide()
            selectedProducts.clear()
            selectedProducts.addAll(it)
            binding.textSelectedProducts.text = it.joinToString()
        }

        binding.viewProducts.setOnClickListener {
            showProductsDialog()
        }
        MeldSDK.setHandOverListener(this, object : OnHandOverListener {
            override fun cancel() {
                binding.progressbar.visibility = View.GONE
            }

            override fun handOver(connectionWidgetResponse: ConnectionWidgetResponse?) {
                MeldData.institutionName = connectionWidgetResponse?.institutionName
                MeldData.institutionId = connectionWidgetResponse?.institutionId
                binding.progressbar.visibility = View.GONE
                setConnectionData()
            }
        })

        binding.btnConnect.setOnClickListener {
            if (!context.isInternetAvailable()) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage(getString(R.string.connection_error_message))
                builder.setPositiveButton(
                    getString(R.string.text_ok)
                ) { p0, p1 -> }
                builder.create().show()
                return@setOnClickListener
            }

            if (MeldData.connect == null) {
                context?.showMessage("Please establish connection before proceeding further")
                return@setOnClickListener
            }
            binding.progressbar.visibility = View.VISIBLE

            getConnectToken(selectedProducts) { connectResponse ->
                connectResponse?.let {
                    MeldSDK.connectController(
                        connectResponse.connectToken, requireContext()
                    )
                }
            }
        }

        binding.btnConnectionDemo.setOnClickListener {
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToSearchInstitutionConnectionsFragment())
        }
        binding.btnInstitutionsDemo.setOnClickListener {
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToSearchInstitutionFragment())
        }

        binding.btnAccountsDemo.setOnClickListener {
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToSearchFinancialAccountFragment())
        }

    }


    override fun onResume() {
        super.onResume()
        setConnectionData()
    }

    private fun setConnectionData() {
        //Set connection details
        MeldData.connect?.let { connectResponse ->
            binding.btnAccountsDemo.isEnabled = true
            binding.btnAccountsDemo.alpha = 1f
            binding.tvConnection.text = "Customer ID: " + connectResponse.customerId
        } ?: kotlin.run {
            binding.btnAccountsDemo.isEnabled = false
            binding.btnAccountsDemo.alpha = 0.7f
            binding.tvConnection.text = ""
        }

        MeldData.institutionId?.let {
            binding.tvInstitutionData.text =
                "${MeldData.institutionName.orEmpty()} \n${MeldData.institutionId}"
        } ?: kotlin.run {
            binding.tvInstitutionData.text = ""
        }

        MeldData.products?.let {
            if (it.isNotEmpty()) binding.textSelectedProducts.show() else binding.textSelectedProducts.hide()
            selectedProducts.clear()
            selectedProducts.addAll(it)
            binding.textSelectedProducts.text = it.joinToString()
        }
    }

    /**
     * @param callback callback function for getting connection token
     * if connection token is already available than it will return the same
     * and if not than first it will fetch the connection token and than it will call the callback
     */
    private fun getConnectToken(
        products: List<String>,
        callback: (connectResponse: ConnectResponse?) -> Unit
    ) {
        MeldSDK.getConnectToken(
            MeldData.externalCustomerId,
            true,
            products,
            listOf("US", "CA"),
            object : OnConnectListener {
                override fun onConnect(response: ConnectResponse?, error: MeldError?) {
                    response?.let {
                        MeldData.connect = it
                        //connect token received
                        callback.invoke(it)
                    } ?: kotlin.run {
                        //Something wrong with get token
                        binding.progressbar.visibility = View.GONE
                        context?.showConnectError(error)
                    }

                }
            })
    }


    private fun showProductsDialog() {
        val products = Products::class.enumConstantNames()
        val oldSelectedData = ArrayList<String>()
        oldSelectedData.addAll(selectedProducts)
        val checkedItems = BooleanArray(products.size)
        products.forEachIndexed { index, s ->
            if (selectedProducts.contains(s)) {
                checkedItems[index] = true
            }
        }
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select your products")
            .setMultiChoiceItems(
                products.toTypedArray(), checkedItems
            ) { dialog, which, isChecked ->
                if (isChecked && !selectedProducts.contains(products[which])) {
                    checkedItems[which] = true
                    selectedProducts.add(products[which])
                } else if (selectedProducts.contains(products[which])) {
                    selectedProducts.remove(products[which])
                    checkedItems[which] = false
                }
            }
            // Set the action buttons
            .setPositiveButton("Ok",
                DialogInterface.OnClickListener { dialog, id ->
                    MeldData.products = selectedProducts
                    if (selectedProducts.size > 0) binding.textSelectedProducts.show() else binding.textSelectedProducts.hide()
                    binding.textSelectedProducts.text = "${selectedProducts.joinToString()}"
                })
            .setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, id ->
                    selectedProducts.clear()
                    selectedProducts.addAll(oldSelectedData)
                })

        builder.create()
        builder.show()

    }

    fun KClass<out Enum<*>>.enumConstantNames() =
        this.java.enumConstants.map(Enum<*>::name)

}