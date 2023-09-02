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
import com.bizify.MyApplication.Companion.instance
import com.bizify.R
import com.bizify.databinding.FragmentLoginBinding
import com.bizify.ui.viewmodel.MainViewModel
import com.bizify.ui.viewmodel.ViewModelFactory
import com.bizify.utils.*
import kotlinx.coroutines.launch
import okhttp3.internal.Internal.instance
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment(), KodeinAware {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: MainViewModel
    private val factory: ViewModelFactory by instance()
    lateinit var binding: FragmentLoginBinding
    lateinit var loading : CustomProgressDialog
    private lateinit var navController: NavController
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
        binding = FragmentLoginBinding.inflate(layoutInflater)
        navController = findNavController()
        loading = CustomProgressDialog(context)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
        binding.tvChangeUrl.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToConnectionTestFragment()
            navController.navigate(action)
        }
        binding.btnLogin.setOnClickListener{
            var userName = binding.email.text.toString()
            var password = binding.password.text.toString()
            if (userName.isNullOrEmpty()){
                binding.email.error = "Enter a username"
            }else if (password.isNullOrEmpty()){
                binding.password.error = "Enter a password"
            }
//            else if (!isConnected(requireContext())){
//               Toast.makeText(requireContext(),"No internet connection",Toast.LENGTH_LONG).show()
//            }
            else{
                loading.show(getString(R.string.text_loading))
                lifecycleScope.launch {
                    try {
                        viewModel.login(binding.email.text.toString(),binding.password.text.toString())
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
        viewModel.user.removeObservers(viewLifecycleOwner)
        viewModel.user.observe(viewLifecycleOwner){
            binding.progressCircular.visibility = View.GONE
            if (loading.isShowing)
                loading.cancel()
            if (it?.message==null){
                var sessions = SessionUtils(requireContext())
                sessions.saveToken(it.token)
                sessions.saveUserId(it.id.toString())
                Toast.makeText(requireContext(),"Login Success",Toast.LENGTH_LONG).show()
                var action = LoginFragmentDirections.actionLoginFragmentToPostFragment()
                navController.navigate(action)
            }else{
                Toast.makeText(requireContext(),it?.message,Toast.LENGTH_LONG).show()
            }
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
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override val kodein by kodein()
}