package com.bizify.ui.fragments

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bizify.R
import com.bizify.data.model.CustomerRequest
import com.bizify.data.model.JobReqModel
import com.bizify.databinding.FragmentAddCustomerBinding
import com.bizify.databinding.FragmentAddNewJobBinding
import com.bizify.ui.viewmodel.MainViewModel
import com.bizify.ui.viewmodel.ViewModelFactory
import com.bizify.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.time.LocalDateTime

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddCustomerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddCustomerFragment : Fragment(), KodeinAware {
    override val kodein by kodein()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var custId = 0
    var registration = ""
    private lateinit var navController: NavController
    lateinit var binding: FragmentAddCustomerBinding
    private lateinit var viewModel: MainViewModel
    private val factory: ViewModelFactory by instance()
    lateinit var loading: CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddCustomerBinding.inflate(layoutInflater)
        navController = findNavController()
        loading = CustomProgressDialog(context)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
        binding.ivBack.setOnClickListener {
            navController.popBackStack()
        }
        binding.btnAdd.setOnClickListener {
            when {
                binding.edtName.text.toString().isNullOrEmpty() -> Toast.makeText(
                    requireContext(),
                    "Please enter the name",
                    Toast.LENGTH_LONG
                ).show()
//                binding.etEmail.text.toString().isNullOrEmpty() -> Toast.makeText(
//                    requireContext(),
//                    "Please enter the email address",
//                    Toast.LENGTH_LONG
//                ).show()
                binding.etMobile.text.toString().isNullOrEmpty() -> Toast.makeText(
                    requireContext(),
                    "Please enter the mobile number",
                    Toast.LENGTH_LONG
                ).show()
                binding.etMobile.text.toString().trim().length != 10 -> Toast.makeText(
                    requireContext(),
                    "Please enter a valid phone number",
                    Toast.LENGTH_LONG
                ).show()
                !Patterns.EMAIL_ADDRESS.matcher(binding.etEmail?.text.toString().trim())
                    .matches() -> Toast.makeText(
                    requireContext(),
                    "Please enter a valid email address",
                    Toast.LENGTH_LONG
                ).show()
                else -> {
                    loading.show(getString(R.string.text_loading))
                    var sessions = SessionUtils(requireContext())
                    val doc = CustomerRequest(
                        csName = binding.edtName.text.toString(),
                        email = binding.etEmail.text.toString(),
                        mobile = binding.etMobile.text.toString()
                    )

                    val gson = Gson()
                    val listString = gson.toJson(
                        doc,
                        object : TypeToken<CustomerRequest?>() {}.type
                    )
                    val jsonObject = JSONObject(listString)
                    var JSON = MediaType.parse("application/json; charset=utf-8")

                    var requestBody = RequestBody.create(JSON, listString)
                    lifecycleScope.launch {
                        try {
                            viewModel.addCustomer(requestBody, sessions.token!!)
                        } catch (exception: ApiException) {
                            if (loading.isShowing)
                                loading.cancel()
                            Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG)
                                .show()
                        } catch (exception: NoInternetException) {
                            if (loading.isShowing)
                                loading.cancel()
                            Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG)
                                .show()
                        } catch (exception: ErrorBodyException) {
                            if (loading.isShowing)
                                loading.cancel()
                            Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG)
                                .show()
                        } catch (exception: Exception) {
                            if (loading.isShowing)
                                loading.cancel()
                            Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }


            }
        }
        viewModel.addCustomerResponse.removeObservers(viewLifecycleOwner)
        viewModel.addCustomerResponse.observe(viewLifecycleOwner) {
            if (loading.isShowing)
                loading.cancel()
            Toast.makeText(requireContext(), it?.message?:"Customer added successfully", Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddCustomerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddCustomerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}