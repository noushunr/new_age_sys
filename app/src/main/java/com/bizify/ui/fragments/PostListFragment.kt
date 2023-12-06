package com.bizify.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bizify.R
import com.bizify.data.model.AodpList
import com.bizify.data.model.CreateJobResponse
import com.bizify.data.model.Services
import com.bizify.databinding.FragmentPostListBinding
import com.bizify.interfaces.NetworkListener
import com.bizify.interfaces.PostClick
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
 * Use the [PostListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostListFragment : Fragment(), KodeinAware, NetworkListener {
    override val kodein by kodein()
    private lateinit var viewModel: MainViewModel
    private val factory: ViewModelFactory by instance()
    private lateinit var binding: FragmentPostListBinding
    private lateinit var navController: NavController
    lateinit var loading : CustomProgressDialog
    lateinit var jobOrder : CreateJobResponse
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        viewModel.selectedServices.removeObservers(this)
        viewModel.selectedServices.observe(this){
            if (loading.isShowing)
                loading.cancel()


            var services = ""
            it?.forEach { it ->
                if (services.isNotEmpty())
                    services += ", "
                services += "${it.itemName}"
            }

            val text = "Hi, ${jobOrder.customer} please note below detail \n" +
                    "Job Card # ${jobOrder.voucherNo}\n" +
                    "Plate No # ${jobOrder.registartion}\n" +
                    "Service # $services"
            val shareText = Intent(Intent.ACTION_SEND)
            shareText.type = "text/plain"
            shareText.putExtra(Intent.EXTRA_TEXT, text)
            startActivity(Intent.createChooser(shareText, "Share Via"))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostListBinding.inflate(layoutInflater)
        loading = CustomProgressDialog(context)
        navController = findNavController()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var adapter = PostListAdapter(requireContext(), mutableListOf(), object : PostClick {
            override fun onItemClick(jobResponse: CreateJobResponse) {
                var action = PostListFragmentDirections.actionPostListFragmentToPostDetailFragment(
                    jobResponse
                )
                navController.navigate(action)
            }

            override fun onItemShare(job: CreateJobResponse) {

//                viewModel.selectedServices.removeObservers(viewLifecycleOwner)
                jobOrder = job

                getServices(job)
            }

        })

        binding?.rvList?.adapter = adapter
        adapter.submitList(mutableListOf())
        lifecycleScope?.launch {
            var sessionUtils = SessionUtils(requireContext())
//            if (!isConnected(requireContext())) {
//                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_LONG).show()
//            }else {
               
            loading.show(getString(R.string.text_loading))
                try {
                    binding.tvNoOrders.visibility = View.GONE
                    binding.rvList.visibility = View.GONE
                    viewModel?.getJobOrders(sessionUtils.token!!)
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
                       Toast.makeText(requireContext(), "UnAuthorized, Please login again..", Toast.LENGTH_LONG).show()

                       var sessionUtils = SessionUtils(requireContext())
                       sessionUtils.saveLoggedIn(false)
                       var action = PostListFragmentDirections.actionPostListFragmentToLoginFragment()
                       navController.navigate(action)
                   }else{
                       Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()

                   }
                }catch (exception : Exception){
                    if (loading.isShowing)
                        loading.cancel()
                   
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                }

//            }
        }
        binding?.floatingActionButton?.setOnClickListener {

            var action = PostListFragmentDirections.actionPostListFragmentToPostDetailFragment(
                CreateJobResponse()
            )
            navController.navigate(action)
        }
        binding.ivLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Successfully logout", Toast.LENGTH_LONG).show()
            var sessionUtils = SessionUtils(requireContext())
            sessionUtils.saveLoggedIn(false)
            var action = PostListFragmentDirections.actionPostListFragmentToLoginFragment()
            navController.navigate(action)
        }
        viewModel.jobOrders.removeObservers(viewLifecycleOwner)
        viewModel.jobOrders.observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
            if (loading.isShowing)
                loading.cancel()
            if (it?.isNullOrEmpty()!!) {
                binding.tvNoOrders.visibility = View.VISIBLE
                binding.rvList.visibility = View.GONE
            } else {
                binding.tvNoOrders.visibility = View.GONE
                binding.rvList.visibility = View.VISIBLE
            }
        }

//        context?.let {
//            viewModel?.getSavedPosts()?.observe(viewLifecycleOwner) {
//               
//                if (loading.isShowing)
//                    loading.cancel()
//            }
//        }
        viewModel?.error?.observe(viewLifecycleOwner) {
           
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            if (loading.isShowing)
                loading.cancel()
        }

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                adapter.filter.filter(binding.edtSearch.text.toString())
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.selectedServices.removeObservers(this)
    }
    fun getServices(jobOrder:CreateJobResponse){
        var sessions = SessionUtils(requireContext())
        lifecycleScope.launch {
            try {
                loading.show(getString(R.string.text_loading))
                viewModel.getOrderServiceList(sessions.token!!, jobOrder.voucherNo!!)

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
                    Toast.makeText(requireContext(), "UnAuthorized, Please login again..", Toast.LENGTH_LONG).show()

                    var sessionUtils = SessionUtils(requireContext())
                    sessionUtils.saveLoggedIn(false)
                    var action = PostListFragmentDirections.actionPostListFragmentToLoginFragment()
                    navController.navigate(action)
                }else{
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()

                }
            }catch (exception : Exception){
                if (loading.isShowing)
                    loading.cancel()

                Toast.makeText(requireContext(), "Unable to connect now. Please check your connectivity and try again..", Toast.LENGTH_LONG).show()
            }
        }

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PostListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onStarted() {
        
    }

    override fun onSuccess() {
        
    }

    override fun onFailure() {
        
    }

    override fun onError() {
        
    }

    override fun onNoNetwork() {
        
    }
}