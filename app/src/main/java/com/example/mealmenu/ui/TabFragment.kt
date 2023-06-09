package com.example.mealmenu.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mealmenu.ui.adapter.ViewPagerAdapter
import com.example.mealmenu.databinding.FragmentTabBinding
import com.google.android.material.tabs.TabLayoutMediator


/**
 * A simple [Fragment] subclass.
 * Use the [TabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TabFragment : Fragment() {
    val LayoutArray = arrayOf(
       "Dishes",
        "Templates"
    )
     private var _binding: FragmentTabBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentTabBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = LayoutArray[position]
        }.attach()
    }

}