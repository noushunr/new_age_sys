package com.bizify.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.bizify.R
import com.bizify.databinding.FragmentIntroBinding
import com.bizify.ui.adapter.ImageAdapter
import java.lang.Math.abs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IntroFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IntroFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentIntroBinding
    private lateinit var navController: NavController
//    private lateinit var handler : Handler
    private lateinit var imageList:ArrayList<Int>
    private lateinit var adapter: ImageAdapter
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
        binding = FragmentIntroBinding.inflate(layoutInflater)

        init()
        setUpTransformer()

//        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                handler.removeCallbacks(runnable)
//                handler.postDelayed(runnable , 2000)
//            }
//        })
        binding.btnStart.setOnClickListener {
            val action = IntroFragmentDirections.actionIntroFragmentToLoginFragment()
            navController.navigate(action)
        }
        return binding.root
    }

    override fun onPause() {
        super.onPause()

//        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()

//        handler.postDelayed(runnable , 2000)
    }

    private val runnable = Runnable {
//        binding.viewPager2.currentItem = binding.viewPager2.currentItem + 1
    }

    private fun setUpTransformer(){
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }

        binding.viewPager2.setPageTransformer(transformer)
    }

    private fun init(){
//        handler = Handler(Looper.myLooper()!!)
        navController = findNavController()
        imageList = ArrayList()

        imageList.add(R.drawable.one)
        imageList.add(R.drawable.two)
        imageList.add(R.drawable.three)

        adapter = ImageAdapter(imageList, binding.viewPager2)

        binding.viewPager2.adapter = adapter
        binding.viewPager2.offscreenPageLimit = 3
        binding.viewPager2.clipToPadding = false
        binding.viewPager2.clipChildren = false
        binding.viewPager2.currentItem = 1
        binding.viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_IF_CONTENT_SCROLLS

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IntroFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IntroFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}