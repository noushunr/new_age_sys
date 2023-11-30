package com.bizify.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bizify.R
import com.bizify.data.model.CreateJobResponse
import com.bizify.data.model.JobReqModel
import com.bizify.data.model.Services
import com.bizify.databinding.FragmentAddNewJobBinding
import com.bizify.interfaces.ServiceItem
import com.bizify.ui.adapter.ServiceListAdapter
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
    var services = arrayListOf<String>()
    var serviceList = mutableListOf<Services>()
    var selectedService = mutableListOf<Services>()

    var materials = arrayListOf<String>()
    var materialList = mutableListOf<Services>()
    var selectedMaterials = mutableListOf<Services>()

    var statuses = arrayListOf<String>()

    var selectedStatus = "In Progress"
    var serviceId = 0
    lateinit var adapter: ServiceListAdapter
    lateinit var materialsAdapter: ServiceListAdapter

    val args : AddNewJobFragmentArgs by navArgs()
    lateinit var jobResponse: CreateJobResponse

    var isUpdate = false
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        loading = CustomProgressDialog(context)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
        jobResponse = args.jobItem
        adapter = ServiceListAdapter(requireContext(), selectedService,object : ServiceItem {
            override fun onItemAdd(position: Int, quantity: String,isQuantity: Boolean,unitPriceName: String) {
                showEditTextDialog(requireContext(),isQuantity,position,quantity,unitPriceName)
//                adapter.notifyItemChanged(position)
            }

            override fun onItemRemove(position: Int) {
                selectedService.removeAt(position)
                adapter.notifyItemRemoved(position)
            }


        })
        binding?.rvList?.adapter = adapter
        adapter.submitList(selectedService)
        materialsAdapter = ServiceListAdapter(requireContext(), selectedMaterials,object : ServiceItem {
            override fun onItemAdd(position: Int, quantity: String,isQuantity: Boolean,unitPriceName: String) {
                showMaterialsEditTextDialog(requireContext(),isQuantity,position,quantity,unitPriceName)
//                adapter.notifyItemChanged(position)
            }

            override fun onItemRemove(position: Int) {
                selectedMaterials.removeAt(position)
                materialsAdapter.notifyItemRemoved(position)
            }


        })
        binding?.rvMaterials?.adapter = materialsAdapter
        materialsAdapter.submitList(selectedMaterials)
        if (jobResponse.service_ID!! >0){
            serviceId = jobResponse.service_ID!!
            binding.edtName.setText("${jobResponse.customer!!}")
            binding.etVehicle.setText(jobResponse.registartion?:"")
            binding.edtType.setText(jobResponse.jobType)
            binding.edtOdoNo.setText(jobResponse.odoMeter)
            selectedStatus = jobResponse.status?:""
            var position = if (jobResponse.status.equals("In Progress",ignoreCase = true)) 0 else 1
            binding.mySpinner.setSelection(position)
            binding.edtComplaint.setText(jobResponse.customer_Complaint)
            binding.edtInspection.setText(jobResponse.tech_Inspection)
            binding.carTopRemarks.setText(jobResponse.carTopRemarks?:"")
            binding.carBottomRemarks.setText(jobResponse.carBottomRemarks?:"")
            binding.carFrontRemarks.setText(jobResponse.carFrontRemarks?:"")
            binding.carLeftRemarks.setText(jobResponse.carLeftRemarks?:"")
            binding.carRearRemarks.setText(jobResponse.carRearRemarks?:"")
            binding.carRightRemarks.setText(jobResponse.carRightRemarks?:"")
            custId = jobResponse.cust_Id!!
            registration = jobResponse.registartion!!
            isUpdate = true
            var sessions = SessionUtils(requireContext())
            lifecycleScope.launch {
                viewModel.getOrderServiceList(sessions.token!!, jobResponse.voucherNo!!)
                viewModel.getOrderMaterialList(sessions.token!!, jobResponse.voucherNo!!)
            }
        }

        binding.ivBack.setOnClickListener{
            navController.popBackStack()
        }
        binding.mySpinner!!.setOnItemSelectedListener(this)

        binding.tvServices.setOnClickListener {
            showServices()
        }
        binding.tvMaterials.setOnClickListener {
            showMaterials()
        }
        statuses.clear()
        statuses.add("In Progress")
        statuses.add("Completed")
        val aa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        binding.mySpinner.adapter = aa


        lifecycleScope.launch {
            var sessions = SessionUtils(requireContext())
            if (!isConnected(requireContext())){
                Toast.makeText(requireContext(),"No internet connection", Toast.LENGTH_LONG).show()
            } else {
                loading.show(getString(R.string.text_loading))
                try {
                    viewModel.getServiceList(sessions.token!!)
                    viewModel.getMaterialList(sessions.token!!)
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
                    if (exception.message?.equals("401")!!){
                        Toast.makeText(requireContext(), "Un Authorized, Please login again..", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()

                    }
                }catch (exception : Exception){
                    if (loading.isShowing)
                        loading.cancel()
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                }
            }
        }



        viewModel.services.observe(viewLifecycleOwner){
            serviceList = it as MutableList<Services>
            services.clear()
            if (loading.isShowing)
                loading.cancel()
            it?.forEach {
                services.add(it.itemName!!)
            }
            viewModel.services.removeObservers(viewLifecycleOwner)
//            adapter.submitList(it)

        }

        viewModel.materials.observe(viewLifecycleOwner){
            materialList = it as MutableList<Services>
            materials.clear()
            if (loading.isShowing)
                loading.cancel()
            it?.forEach {
                materials.add(it.itemName!!)
            }
            viewModel.materials.removeObservers(viewLifecycleOwner)
        }

        viewModel.selectedServices.observe(viewLifecycleOwner){
            selectedService.addAll(it)
            if (loading.isShowing)
                loading.cancel()

            viewModel.selectedServices.removeObservers(viewLifecycleOwner)
            adapter.notifyDataSetChanged()
//            adapter.submitList(it)

        }

        viewModel.selectedMaterials.observe(viewLifecycleOwner){
            selectedMaterials.addAll(it)
            if (loading.isShowing)
                loading.cancel()
//            materialsAdapter.submitList(it)
            materialsAdapter.notifyDataSetChanged()
            viewModel.selectedMaterials.removeObservers(viewLifecycleOwner)
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
            }
            else if (selectedService.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please choose a service", Toast.LENGTH_LONG)
                    .show()
            }
//            else if (binding.edtType.text.toString().isNullOrEmpty()) {
//                Toast.makeText(requireContext(), "Please enter the Job Type", Toast.LENGTH_LONG)
//                    .show()
//                binding.edtType.error = "Please enter the Job Type"
//            } else if (binding.edtOdoNo.text.toString().isNullOrEmpty()) {
//                Toast.makeText(requireContext(), "Please enter the Odo Number", Toast.LENGTH_LONG)
//                    .show()
//                binding.edtOdoNo.error = "Please enter the Odo Number"
//            } else if (binding.edtComplaint.text.toString().isNullOrEmpty()) {
//                Toast.makeText(
//                    requireContext(),
//                    "Customer Complaint filed Cant be empty",
//                    Toast.LENGTH_LONG
//                ).show()
//                binding.edtComplaint.error = "Customer Complaint filed Cant be empty"
//            } else if (binding.edtInspection.text.toString().isNullOrEmpty()) {
//                Toast.makeText(
//                    requireContext(),
//                    "Tech Inspection filed Cant be empty",
//                    Toast.LENGTH_LONG
//                ).show()
//                binding.edtInspection.error = "Tech Inspection filed Cant be empty"
//            }
            else if (!isConnected(requireContext())){
                Toast.makeText(requireContext(),"No internet connection",Toast.LENGTH_LONG).show()
            } else {
                loading.show(getString(R.string.text_loading))
                var sessions = SessionUtils(requireContext())
                var jobType = ""
                if(selectedService.isNotEmpty())
                    jobType = selectedService[0].itemName!!
                val doc = JobReqModel(
                    service_ID = serviceId,
                    cust_Id = custId,
                    start_Date = LocalDateTime.now().toString(),
                    v_Date = LocalDateTime.now().toString(),
                    services = selectedService,
                    materials = selectedMaterials,
                    jobType = jobType,
                    odoMeter = binding.edtOdoNo.text.toString(),
                    customer_Complaint = binding.edtComplaint.text.toString(),
                    plate_ID = 2,
                    refNo = "",
                    registartion = registration,
                    userID = sessions.userId?.toInt(),
                    tech_Inspection = binding.edtInspection.text.toString(),
                    carTopRemarks = binding.carTopRemarks.text.toString(),
                    carRightRemarks = binding.carRightRemarks.text.toString(),
                    carLeftRemarks = binding.carLeftRemarks.text.toString(),
                    carBottomRemarks = binding.carBottomRemarks.text.toString(),
                    carFrontRemarks = binding.carFrontRemarks.text.toString(),
                    carRearRemarks = binding.carRearRemarks.text.toString(),
                    status = selectedStatus

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
            var message = if (!isUpdate) "Job Order created successfully" else "Job Order updated successfully"
            if(it.voucherNo.isNullOrEmpty()){
                Toast.makeText(requireContext(),it.message?:"",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(requireContext(),it.message?:message,Toast.LENGTH_LONG).show()
                navController.popBackStack()
            }

            if (loading.isShowing)
                loading.cancel()

        }
    }
    fun clear(){
        custId = 0
        registration = ""
        binding.edtName.setText("")
        binding.etVehicle.setText("")
        binding.etVehicleName.setText("")
        binding.edtType.setText("")
        binding.edtComplaint.setText("")
        binding.edtOdoNo.setText("")
        binding.edtInspection.setText("")
        binding.carFrontRemarks.setText("")
        binding.carBottomRemarks.setText("")
        binding.carLeftRemarks.setText("")
        binding.carRearRemarks.setText("")
        binding.carRightRemarks.setText("")
        binding.carTopRemarks.setText("")
        selectedService.clear()
        selectedMaterials.clear()
        adapter.notifyDataSetChanged()
        materialsAdapter.notifyDataSetChanged()
        binding.scrollView.post {
            binding.scrollView.smoothScrollTo(0,binding.edtName.top)
        }
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

        selectedStatus = statuses[p2]

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    private fun showServices(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Service List")

        builder.setItems(services.toTypedArray()) { dialog, which ->
            var itemFound = false
            selectedService?.let {
                selectedService.forEach {
                    if (it.itemId == serviceList[which].itemId){
                        itemFound = true
                        return@forEach
                    }
                }
            }
            if (itemFound){
                Toast.makeText(requireContext(),"The selected service is already added",Toast.LENGTH_LONG).show()
            }else {
                selectedService.add(serviceList[which])
                adapter
//                binding.rvList.setItemViewCacheSize(selectedService.size)
                adapter.notifyDataSetChanged()
            }
        }

// create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun showEditTextDialog(context: Context,isQuantity:Boolean, position: Int,text:String,unitPriceName:String) {
        val builder = AlertDialog.Builder(context)
        if (isQuantity)
            builder.setTitle("Enter the Quantity ")
        else
            builder.setTitle("Enter the Unit Price (in $unitPriceName)")

        // Set up the input
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.setText(text)
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK") { _, _ ->
            // Handle OK button click
            val enteredText = input.text.toString()
            if (isQuantity)
                selectedService[position].quantity = enteredText
            else
                selectedService[position].unitPrice = enteredText
            adapter.notifyItemChanged(position)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            // Handle Cancel button click
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showMaterials(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Material List")

        builder.setItems(materials.toTypedArray()) { dialog, which ->
            var itemFound = false
            selectedMaterials?.let {
                selectedMaterials.forEach {
                    if (it.itemId == materialList[which].itemId){
                        itemFound = true
                        return@forEach
                    }
                }
            }
            if (itemFound){
                Toast.makeText(requireContext(),"The selected service is already added",Toast.LENGTH_LONG).show()
            }else {
                selectedMaterials.add(materialList[which])
                binding.rvMaterials.setItemViewCacheSize(selectedMaterials.size)
                materialsAdapter.notifyDataSetChanged()
            }
        }

// create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }
    private fun showMaterialsEditTextDialog(context: Context,isQuantity:Boolean, position: Int,text:String,unitPriceName:String) {
        val builder = AlertDialog.Builder(context)
        if (isQuantity)
            builder.setTitle("Enter the Quantity")
        else
            builder.setTitle("Enter the Unit Price (in $unitPriceName)")

        // Set up the input
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.setText(text)
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK") { _, _ ->
            // Handle OK button click
            val enteredText = input.text.toString()
            if (isQuantity)
                selectedMaterials[position].quantity = enteredText
            else
                selectedMaterials[position].unitPrice = enteredText
            materialsAdapter.notifyItemChanged(position)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            // Handle Cancel button click
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }
}