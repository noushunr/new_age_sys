package com.bizify.ui.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bizify.R
import com.bizify.data.model.VehicleRequestModel
import com.bizify.databinding.FragmentAddVehicleBinding
import com.bizify.ui.viewmodel.MainViewModel
import com.bizify.ui.viewmodel.ViewModelFactory
import com.bizify.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shijil.imagepicker.RxImagePicker
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddVehicleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddVehicleFragment : Fragment()  , KodeinAware {
    override val kodein by kodein()
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var custId = 0
    var registration = ""
    private lateinit var navController: NavController
    lateinit var binding: FragmentAddVehicleBinding
    private lateinit var viewModel: MainViewModel
    private val factory: ViewModelFactory by instance()
    lateinit var loading : CustomProgressDialog

    lateinit var vImage : ByteArray
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
        binding = FragmentAddVehicleBinding.inflate(layoutInflater)
        navController = findNavController()
        loading = CustomProgressDialog(context)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
        binding.ivBack.setOnClickListener{
            navController.popBackStack()
        }
        binding.ivAdd.setOnClickListener {
            if (checkPermission()){
                RxImagePicker.with(activity).requestImage()
            }
        }
        RxImagePicker.with(activity).requestImageLiveData().observe(viewLifecycleOwner, Observer {
            binding.ivAdd.setImageURI(it)
            vImage = readBytes(requireContext(),it)!!
        })
        binding.btnAdd.setOnClickListener {
            when {
                binding.edtName.text.toString().isNullOrEmpty() -> Toast.makeText(requireContext(),"Please enter the name",
                    Toast.LENGTH_LONG).show()
                binding.etBrand.text.toString().isNullOrEmpty() -> Toast.makeText(requireContext(),"Please enter the vehicle brand",
                    Toast.LENGTH_LONG).show()
                binding.etModel.text.toString().isNullOrEmpty() -> Toast.makeText(requireContext(),"Please enter the vehicle model",
                    Toast.LENGTH_LONG).show()
                binding.etReg.text.toString().isNullOrEmpty() -> Toast.makeText(requireContext(),"Please enter the registration number",
                    Toast.LENGTH_LONG).show()
                binding.etEngine.text.toString().isNullOrEmpty() -> Toast.makeText(requireContext(),"Please enter the engine number",
                    Toast.LENGTH_LONG).show()
                binding.etChasis.text.toString().isNullOrEmpty() -> Toast.makeText(requireContext(),"Please enter the chasis number",
                    Toast.LENGTH_LONG).show()

                else -> {
                    loading.show(getString(R.string.text_loading))
                    var sessions = SessionUtils(requireContext())
                    val doc = VehicleRequestModel(
                        veh_Name  = binding.edtName.text.toString(),
                        veh_Brand =  binding.etBrand.text.toString(),
                        veh_Model = binding.etModel.text.toString(),
                        registartion = binding.etReg.text.toString(),
                        engineNo = binding.etEngine.text.toString(),
                        chasisNo = binding.etChasis.text.toString(),
//                        vImage = vImage

                    )

                    val gson = Gson()
                    val listString = gson.toJson(
                        doc,
                        object : TypeToken<VehicleRequestModel?>() {}.type
                    )
                    val jsonObject = JSONObject(listString)
                    var JSON = MediaType.parse("application/json; charset=utf-8")

                    var requestBody = RequestBody.create(JSON, listString)
                    lifecycleScope.launch {
                        try {
                            viewModel.addVehicles(requestBody, sessions.token!!)
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


            }
        }
        viewModel.addVehicles.removeObservers(viewLifecycleOwner)
        viewModel.addVehicles.observe(viewLifecycleOwner){
            if (loading.isShowing)
                loading.cancel()
            Toast.makeText(requireContext(),it?.message, Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
        return binding.root
    }

    private fun checkPermission(): Boolean {
        var permissions = arrayOf<String?>()
        var isStoragePermission = false
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            isStoragePermission = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        }
        return if (isStoragePermission ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (isStoragePermission) {
                permissions = if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                } else {
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissions = arrayOf(Manifest.permission.CAMERA)
                }
            }
            ActivityCompat.requestPermissions(
                requireActivity(), permissions,
                100
            )
            false
        } else {
            true
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddVehicleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddVehicleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @Throws(IOException::class)
    private fun readBytes(context: Context, uri: Uri): ByteArray? =
        context.contentResolver.openInputStream(uri)?.use { it.buffered().readBytes() }
}