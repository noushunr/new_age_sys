package com.bizify.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bizify.data.model.AodpList
import com.bizify.databinding.FragmentPostListBinding
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
class PostListFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private lateinit var viewModel: MainViewModel
    private val factory: ViewModelFactory by instance()
    private lateinit var binding: FragmentPostListBinding
    private lateinit var navController: NavController

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        binding = FragmentPostListBinding.inflate(layoutInflater)
        navController = findNavController()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
        var adapter = PostListAdapter(requireContext(), mutableListOf(), object : PostClick {
            override fun onItemClick(aodpList: AodpList) {

            }

        })

        binding?.rvList?.adapter = adapter
        adapter.submitList(mutableListOf())
        lifecycleScope?.launch {
            var sessionUtils = SessionUtils(requireContext())
//            if (!isConnected(requireContext())) {
//                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_LONG).show()
//            }else {
                binding?.progressCircular?.visibility = View.VISIBLE
                try {
                    viewModel?.getJobOrders(sessionUtils.token!!)
                }catch (exception : ApiException){
                    binding?.progressCircular?.visibility = View.GONE
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                }catch (exception : NoInternetException){
                    binding?.progressCircular?.visibility = View.GONE
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                }catch (exception : ErrorBodyException){
                    binding?.progressCircular?.visibility = View.GONE
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                }catch (exception : Exception){
                    binding?.progressCircular?.visibility = View.GONE
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                }

//            }
        }
        binding?.floatingActionButton?.setOnClickListener {
            Toast.makeText(requireContext(), "Successfully logout", Toast.LENGTH_LONG).show()
            var action = PostListFragmentDirections.actionPostListFragmentToPostDetailFragment()
            navController.navigate(action)
        }
        binding.ivLogout.setOnClickListener {
            var sessionUtils = SessionUtils(requireContext())
            sessionUtils.saveLoggedIn(false)
            var action = PostListFragmentDirections.actionPostListFragmentToLoginFragment()
            navController.navigate(action)
        }
        viewModel.jobOrders.removeObservers(viewLifecycleOwner)
        viewModel.jobOrders.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if (it?.isNullOrEmpty()!!) {
                binding.tvNoOrders.visibility = View.VISIBLE
                binding.rvList.visibility = View.GONE
            } else {
                binding.tvNoOrders.visibility = View.GONE
                binding.rvList.visibility = View.VISIBLE
            }
        }
        context?.let {
            viewModel?.getSavedPosts()?.observe(viewLifecycleOwner) {
                binding?.progressCircular?.visibility = View.GONE

            }
        }
        viewModel?.error?.observe(viewLifecycleOwner) {
            binding?.progressCircular?.visibility = View.GONE
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
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
}