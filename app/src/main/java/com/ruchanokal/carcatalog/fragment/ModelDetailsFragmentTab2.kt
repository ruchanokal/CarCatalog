package com.ruchanokal.carcatalog.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ruchanokal.carcatalog.R
import com.ruchanokal.carcatalog.adapter.CarPhotosAdapter
import com.ruchanokal.carcatalog.databinding.FragmentModelDetailsTab2Binding


class ModelDetailsFragmentTab2 : Fragment() {


    private lateinit var db : FirebaseFirestore
    private var binding : FragmentModelDetailsTab2Binding? = null
    var modelName = ""
    var urlList = arrayListOf<String>()
    private lateinit var carPhotosAdapter: CarPhotosAdapter


    fun newInstance(modelName : String): ModelDetailsFragmentTab2 {
        val modelDetailsFragmentTab2 = ModelDetailsFragmentTab2()
        val args = Bundle()
        args.putString("modelName",modelName)
        modelDetailsFragmentTab2.setArguments(args)
        return modelDetailsFragmentTab2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentModelDetailsTab2Binding.inflate(inflater,container,false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {

            modelName = it.getString("modelName")!!

        }

        db = Firebase.firestore

        db.collection(modelName).addSnapshotListener { value, error ->

            if (value != null) {

                if (!value.isEmpty){

                    val documents = value.documents

                    for (document in documents){

                        val url = document.getString("url")

                        if (url != null) {
                            urlList.add(url)
                        }
                        carPhotosAdapter.notifyDataSetChanged()

                    }
                }
            }


        }

        binding!!.carPhotosRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        carPhotosAdapter = CarPhotosAdapter(urlList)
        binding!!.carPhotosRecyclerView.adapter = carPhotosAdapter

    }


}