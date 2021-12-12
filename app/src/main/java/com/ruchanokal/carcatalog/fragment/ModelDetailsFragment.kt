package com.ruchanokal.carcatalog.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ruchanokal.carcatalog.R
import com.ruchanokal.carcatalog.adapter.VPAdapter
import com.ruchanokal.carcatalog.databinding.FragmentModelDetailsBinding
import com.ruchanokal.carcatalog.model.Car
import java.io.ByteArrayOutputStream


class ModelDetailsFragment : Fragment() {

    private var binding : FragmentModelDetailsBinding? = null
    var carIndex = 0
    var modelIndex = 0
    var modelName = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentModelDetailsBinding.inflate(inflater, container, false)
        val view = binding!!.root
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding!!.toolbar)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        arguments?.let {

            carIndex = ModelDetailsFragmentArgs.fromBundle(it).carIndex
            modelIndex = ModelDetailsFragmentArgs.fromBundle(it).modelIndex
            modelName = ModelDetailsFragmentArgs.fromBundle(it).modelName

        }

        if (!modelName.equals("")) {
            requireActivity().title = modelName
        } else {
            requireActivity().title = requireActivity().resources.getString(R.string.car_details)
        }


        val vpAdapter = VPAdapter(childFragmentManager,lifecycle,carIndex,modelIndex,modelName)
        binding!!.viewPager.adapter = vpAdapter

        TabLayoutMediator(binding!!.tabLayout, binding!!.viewPager, object : TabLayoutMediator.TabConfigurationStrategy {
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                when(position) {
                    0 -> {
                        tab.text = requireActivity().resources.getString(R.string.features)
                        ModelDetailsFragmentTab1()
                    } 1 -> {
                        tab.text = requireActivity().resources.getString(R.string.photos)
                        ModelDetailsFragmentTab2()
                    } else -> {
                        Fragment()
                    }
                }
            }

        }).attach()

    }




}