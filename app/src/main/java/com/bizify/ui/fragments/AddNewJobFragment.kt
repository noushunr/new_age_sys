package com.bizify.ui.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bizify.R
import com.bizify.data.model.Customers
import com.bizify.data.model.JobReqModel
import com.bizify.data.model.LoginModel
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
 * Use the [AddNewJobFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddNewJobFragment : Fragment() , KodeinAware, AdapterView.OnItemSelectedListener {
    override val kodein by kodein()
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var custId = 0
    var registration = ""
    private lateinit var navController: NavController
    lateinit var binding: FragmentAddNewJobBinding
    private lateinit var viewModel: MainViewModel
    private val factory: ViewModelFactory by instance()
    lateinit var loading : CustomProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddNewJobBinding.inflate(layoutInflater)
        navController = findNavController()
        loading = CustomProgressDialog(context)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
        binding.ivBack.setOnClickListener{
            navController.popBackStack()
        }
        binding.mySpinner!!.setOnItemSelectedListener(this)

        // Create an ArrayAdapter using a simple spinner layout and languages array


        lifecycleScope.launch {
            var sessions = SessionUtils(requireContext())
            if (!isConnected(requireContext())){
                Toast.makeText(requireContext(),"No internet connection", Toast.LENGTH_LONG).show()
            } else {
                loading.show(getString(R.string.text_loading))
                try {
                    viewModel.getServiceList(sessions.token!!)
                }catch (exception : ApiException){
                    if (loading.isShowing)
                        loading.cancel()
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                }catch (exception : NoInternetException){
                    if (loading.isShowing)
                        loading.cancel()
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                }catch (exception : ErrorBodyException){
                    if (loading.isShowing)
                        loading.cancel()
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                }catch (exception : Exception){
                    if (loading.isShowing)
                        loading.cancel()
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.services.removeObservers(viewLifecycleOwner)
        viewModel.services.observe(viewLifecycleOwner){
            if (loading.isShowing)
                loading.cancel()
            var services = arrayListOf<String>()
            it?.forEach {
                services.add(it.itemName!!)
            }
            val aa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, services)
            // Set layout to use when the list of choices appear
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Set Adapter to Spinner
            binding.mySpinner.adapter = aa
        }
        binding.edtName.setOnClickListener {
            var action = AddNewJobFragmentDirections.actionNewJobToCustomerListFragment()
            navController.navigate(action)
        }
        binding.etVehicle.setOnClickListener {
            var action = AddNewJobFragmentDirections.actionNewJobToVehicleListFragment()
            navController.navigate(action)
        }
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("id")
            ?.observe(viewLifecycleOwner) { data ->
                custId = data
                // Do something with the data.
            }
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("name")
            ?.observe(viewLifecycleOwner) { data ->
                binding.edtName.setText(data)
                // Do something with the data.
            }
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("vehicle_id")
            ?.observe(viewLifecycleOwner) { data ->
                // Do something with the data.
            }
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("vehicleReg")
            ?.observe(viewLifecycleOwner) { data ->
                // Do something with the data.
                registration = data
            }
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("vehicleName")
            ?.observe(viewLifecycleOwner) { data ->
                binding.etVehicle.setText(data)
                // Do something with the data.
            }
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("vehicleBrand")
            ?.observe(viewLifecycleOwner) { data ->
                binding.etVehicleName.setText(data)
                // Do something with the data.
            }
        binding.btnAdd.setOnClickListener {
            if (custId == 0) {
                Toast.makeText(requireContext(), "Please choose a customer", Toast.LENGTH_LONG)
                    .show()
            } else if (registration.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please choose a vehicle", Toast.LENGTH_LONG)
                    .show()
            } else if (binding.edtType.text.toString().isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please enter the Job Type", Toast.LENGTH_LONG)
                    .show()
                binding.edtType.error = "Please enter the Job Type"
            } else if (binding.edtOdoNo.text.toString().isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please enter the Odo Number", Toast.LENGTH_LONG)
                    .show()
                binding.edtOdoNo.error = "Please enter the Odo Number"
            } else if (binding.edtComplaint.text.toString().isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Customer Complaint filed Cant be empty",
                    Toast.LENGTH_LONG
                ).show()
                binding.edtComplaint.error = "Customer Complaint filed Cant be empty"
            } else if (binding.edtInspection.text.toString().isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Tech Inspection filed Cant be empty",
                    Toast.LENGTH_LONG
                ).show()
                binding.edtInspection.error = "Tech Inspection filed Cant be empty"
            } else if (!isConnected(requireContext())){
                Toast.makeText(requireContext(),"No internet connection",Toast.LENGTH_LONG).show()
            } else {
                loading.show(getString(R.string.text_loading))
                var sessions = SessionUtils(requireContext())
                val doc = JobReqModel(
                    cust_Id = custId,
                    start_Date = LocalDateTime.now().toString(),
                    v_Date = LocalDateTime.now().toString(),
                    jobType = binding.edtType.text.toString(),
                    odoMeter = binding.edtOdoNo.text.toString(),
                    customer_Complaint = binding.edtComplaint.text.toString(),
                    plate_ID = 2,
                    refNo = "",
                    registartion = registration,
                    userID = sessions.userId?.toInt(),
                    tech_Inspection = binding.edtInspection.text.toString()
                )

                val gson = Gson()
                val listString = gson.toJson(
                    doc,
                    object : TypeToken<JobReqModel?>() {}.type
                )
                val jsonObject = JSONObject(listString)
                var JSON = MediaType.parse("application/json; charset=utf-8")

                var requestBody = RequestBody.create(JSON, listString)
                lifecycleScope.launch {
                    try {
                        viewModel.addJob(requestBody, sessions.token!!)
                    } catch (exception : ApiException){
                        if (loading.isShowing)
                            loading.cancel()
                        Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                    }catch (exception : NoInternetException){
                        if (loading.isShowing)
                            loading.cancel()
                        Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                    }catch (exception : ErrorBodyException){
                        if (loading.isShowing)
                            loading.cancel()
                        Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                    }catch (exception : Exception){
                        if (loading.isShowing)
                            loading.cancel()
                        Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        viewModel.createJobResponse.removeObservers(viewLifecycleOwner)
        viewModel.createJobResponse.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(),it.message?:"Success",Toast.LENGTH_LONG).show()
            if (loading.isShowing)
                loading.cancel()
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
         * @return A new instance of fragment AddNewJobFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddNewJobFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}