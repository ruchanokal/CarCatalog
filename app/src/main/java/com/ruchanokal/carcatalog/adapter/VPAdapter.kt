package com.ruchanokal.carcatalog.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ruchanokal.carcatalog.fragment.ModelDetailsFragmentTab1
import com.ruchanokal.carcatalog.fragment.ModelDetailsFragmentTab2


class VPAdapter(fm: FragmentManager,
                lifecycle: Lifecycle,
                carIndex: Int,
                modelIndex : Int,
                modelName : String)
    : FragmentStateAdapter(fm,lifecycle) {

    private var carIndex : Int = carIndex
    private var modelIndex : Int = modelIndex
    private var modelName : String = modelName

    override fun getItemCount(): Int {
        return 2
    }


    override fun createFragment(position: Int): Fragment {

        when(position){

            0 -> {
                return ModelDetailsFragmentTab1().newInstance(carIndex,modelIndex)
            } 1 -> {
                return ModelDetailsFragmentTab2().newInstance(modelName)
            } else -> {
                return Fragment()
            }


        }
    }


}