package com.bizify.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bizify.R
import com.bizify.data.model.Customers
import com.bizify.data.model.Vehicles
import com.bizify.databinding.FragmentCustomerListBinding
import com.bizify.databinding.FragmentVehicleListBinding
import com.bizify.interfaces.CustClick
import com.bizify.interfaces.VehicleClick
import com.bizify.ui.adapter.CustomerAdapter
import com.bizify.ui.adapter.VehicleAdapter
import com.bizify.ui.viewmodel.MainViewModel
import com.bizify.ui.viewmodel.ViewModelFactory
import com.bizify.utils.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VehicleListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VehicleListFragment : Fragment() , KodeinAware, SearchView.OnQueryTextListener {
    override val kodein by kodein()
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentVehicleListBinding
    private lateinit var viewModel: MainViewModel
    private val factory: ViewModelFactory by instance()
    lateinit var loading : CustomProgressDialog
    private lateinit var navController: NavController
    lateinit var adapter : VehicleAdapter
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
        binding = FragmentVehicleListBinding.inflate(layoutInflater)
        navController = findNavController()
        binding.ivBack.setOnClickListener{
            navController.popBackStack()
        }
        binding.floatingActionButton.setOnClickListener {
            val action = VehicleListFragmentDirections.actionVehicleListToAddVehicleFragment()
            navController.navigate(action)
        }
        loading = CustomProgressDialog(context)

        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
        binding.idSV.setOnQueryTextListener(this)
        var adapter = VehicleAdapter(requireContext(), mutableListOf(),object : VehicleClick {
            override fun onItemClick(customers: Vehicles) {

                navController.previousBackStackEntry?.savedStateHandle?.set("id", customers.id)
                navController.previousBackStackEntry?.savedStateHandle?.set("vehicleName", customers.veh_Name)
                navController.previousBackStackEntry?.savedStateHandle?.set("vehicleBrand", customers.veh_Brand)
                navController.previousBackStackEntry?.savedStateHandle?.set("vehicleReg", customers.registartion)
                navController.popBackStack()
            }

        })
        binding?.rvList?.adapter = adapter
        lifecycleScope.launch {
            var sessions = SessionUtils(requireContext())
            if (!isConnected(requireContext())){
                Toast.makeText(requireContext(),"No internet connection", Toast.LENGTH_LONG).show()
            } else {
                loading.show(getString(R.string.text_loading))
                try {
                    viewModel.getVehicleList(sessions.token!!)
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
        viewModel.vehiclesList.removeObservers(viewLifecycleOwner)
        viewModel.vehiclesList.observe(viewLifecycleOwner){
            if (loading.isShowing)
                loading.cancel()
            adapter.submitList(it as MutableList<Vehicles>?)
        }

        adapter.submitList(mutableListOf())

        binding.edtSearch.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                adapter.filter.filter(binding.edtSearch.text.toString())
            }

        })
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VehicleListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VehicleListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (this::adapter.isInitialized)
            adapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (this::adapter.isInitialized)
            adapter.filter.filter(newText)
        return false
    }
}