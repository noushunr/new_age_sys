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
import com.bizify.data.model.AodpList
import com.bizify.data.model.Customers
import com.bizify.databinding.FragmentCustomerListBinding
import com.bizify.interfaces.CustClick
import com.bizify.interfaces.PostClick
import com.bizify.ui.adapter.CustomerAdapter
import com.bizify.ui.adapter.PostListAdapter
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
 * Use the [CustomerListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CustomerListFragment : Fragment(), KodeinAware, SearchView.OnQueryTextListener {
    override val kodein by kodein()
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentCustomerListBinding
    private lateinit var viewModel: MainViewModel
    private val factory: ViewModelFactory by instance()
    lateinit var loading : CustomProgressDialog
    private lateinit var navController: NavController
    private lateinit var adapter : CustomerAdapter
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
        binding = FragmentCustomerListBinding.inflate(layoutInflater)
        navController = findNavController()
        loading = CustomProgressDialog(context)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
        binding.ivBack.setOnClickListener{
            navController.popBackStack()
        }
        binding.floatingActionButton.setOnClickListener {
            val action = CustomerListFragmentDirections.actionCustomerListToAddCustomerFragment()
            navController.navigate(action)
        }
        binding.idSV.setOnQueryTextListener(this)
        adapter = CustomerAdapter(requireContext(), mutableListOf(),object : CustClick {
            override fun onItemClick(customers: Customers) {

                navController.previousBackStackEntry?.savedStateHandle?.set("id", customers.id)
                navController.previousBackStackEntry?.savedStateHandle?.set("name", customers.csName)
                navController.popBackStack()
            }

        })
        binding?.rvList?.adapter = adapter
        lifecycleScope.launch {
            var sessions = SessionUtils(requireContext())
            if (!isConnected(requireContext())){
                Toast.makeText(requireContext(),"No internet connection", Toast.LENGTH_LONG).show()
            }else{
                loading.show(getString(R.string.text_loading))
                try {
                    viewModel.getCustomerList(sessions.token!!)
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
        viewModel.customerList.removeObservers(viewLifecycleOwner)
        viewModel.customerList.observe(viewLifecycleOwner){
            if (loading.isShowing)
                loading.cancel()
            if (it.isNullOrEmpty()){
                binding.idSV.visibility = View.GONE
                binding.edtSearch.visibility = View.GONE
            } else{
                binding.idSV.visibility = View.GONE
                binding.edtSearch.visibility = View.VISIBLE
            }
            adapter.submitList(it as MutableList<Customers>?)
        }

        adapter.submitList(mutableListOf())
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
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
         * @return A new instance of fragment CustomerListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CustomerListFragment().apply {
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