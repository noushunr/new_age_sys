package com.bizify.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bizify.databinding.FragmentConnectionTestBinding
import com.bizify.ui.activity.MainActivity
import com.bizify.ui.viewmodel.MainViewModel
import com.bizify.ui.viewmodel.ViewModelFactory
import com.bizify.utils.CustomProgressDialog
import com.bizify.utils.SessionUtils
import kotlinx.coroutines.delay
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
 * Use the [ConnectionTestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConnectionTestFragment : Fragment() , KodeinAware {
    override val kodein by kodein()
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentConnectionTestBinding
    private lateinit var viewModel: MainViewModel
    private val factory: ViewModelFactory by instance()
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
        binding = FragmentConnectionTestBinding.inflate(layoutInflater)
        loading = CustomProgressDialog(requireContext())
        navController = findNavController()
        val sessionUtils = SessionUtils(requireContext())
        binding.btnTest.setOnClickListener {
            if (binding.url.text.toString().isEmpty() || binding.url.text.toString().isBlank()){
                binding.url.error = "Field cannot empty"
            }else{
                loading.show("changing url..")
                sessionUtils.saveURL("http://${binding.url.text.toString()}/")
                lifecycleScope.launch {
                    delay(2000)
                    if(loading.isShowing)
                        loading.cancel()
                    Toast.makeText(requireContext(),"URL changed",Toast.LENGTH_LONG).show()
//                    navController.popBackStack()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    requireContext().startActivity(intent)
                    Runtime.getRuntime().exit(0)
                }

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
         * @return A new instance of fragment ConnectionTestFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConnectionTestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}