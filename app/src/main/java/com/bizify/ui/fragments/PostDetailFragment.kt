package com.bizify.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bizify.data.model.AodpList
import com.bizify.databinding.FragmentPostDetailBinding
import com.bizify.utils.formatDate


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentPostDetailBinding
    private lateinit var aodpList: AodpList
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
        binding = FragmentPostDetailBinding.inflate(layoutInflater)
        val safeArgs: PostDetailFragmentArgs by navArgs()
        aodpList = safeArgs.item
        binding.post = aodpList
        binding?.date?.text = formatDate(aodpList?.date!!)
        Glide.with(context!!)
            .load(aodpList?.url)
            .into(binding.ivPost)
//        if (aodpList?.url?.contains("gif")!!)
//            Glide.with(context!!).asGif().load(aodpList.url).into(binding.ivPost)
//        else
//            Glide.with(context!!).load(aodpList.url).into(binding.ivPost)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PostDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}