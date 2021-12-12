package com.ruchanokal.carcatalog.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ruchanokal.carcatalog.R
import com.ruchanokal.carcatalog.adapter.CarAdapter
import com.ruchanokal.carcatalog.adapter.CarModelAdapter
import com.ruchanokal.carcatalog.databinding.FragmentCarModelsBinding
import com.ruchanokal.carcatalog.databinding.LayoutDialog2Binding
import com.ruchanokal.carcatalog.databinding.LayoutDialogBinding
import com.ruchanokal.carcatalog.model.Car
import com.ruchanokal.carcatalog.model.CarModel
import java.io.ByteArrayOutputStream


class CarModelsFragment : Fragment() {

    private var binding : FragmentCarModelsBinding? = null
    private var binding2 : LayoutDialog2Binding? = null

    private lateinit var carModelList : ArrayList<CarModel>
    private lateinit var carModelAdapter : CarModelAdapter

    var databaseName = ""
    var indexID = 0
    var carBrandName = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCarModelsBinding.inflate(inflater,container,false)
        val view = binding!!.root
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding!!.toolbar)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {

            indexID = CarModelsFragmentArgs.fromBundle(it).id
            carBrandName = CarModelsFragmentArgs.fromBundle(it).carBrandName

        }

        if (!carBrandName.equals("")) {
            requireActivity().title = carBrandName + requireActivity().resources.getString(R.string.models)
        } else {
            requireActivity().title = requireActivity().resources.getString(R.string.car_models)
        }

    }

    override fun onResume() {
        super.onResume()


        carModelList = ArrayList()
        carModelAdapter = CarModelAdapter(carModelList,indexID)
        binding!!.carModelRecylerView.layoutManager = LinearLayoutManager(activity)
        binding!!.carModelRecylerView.adapter = carModelAdapter

        try {

            databaseName = "carmodels${indexID}"

            val database = requireActivity().openOrCreateDatabase("CarModels", Context.MODE_PRIVATE,null)

            val cursor = database.rawQuery("SELECT * FROM " + databaseName ,null)
            val carBrandNameIx = cursor.getColumnIndex("name")
            val idIx = cursor.getColumnIndex("id")

            while (cursor.moveToNext()) {
                val name = cursor.getString(carBrandNameIx)
                val id = cursor.getInt(idIx)

                val carModel = CarModel(name,id)
                carModelList.add(carModel)
            }

            carModelAdapter.notifyDataSetChanged()

            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding!!.fab2.setOnClickListener {

            openDialog()

        }


    }

    private fun openDialog() {

        val builder = AlertDialog.Builder(requireActivity())
        binding2 = LayoutDialog2Binding.inflate(layoutInflater)

        builder.setView(binding2!!.root)
            .setTitle(requireActivity().resources.getString(R.string.write_the_car_model))
            .setNegativeButton(requireActivity().resources.getString(R.string.cancel), DialogInterface.OnClickListener { dialog, which ->

            }).setPositiveButton(requireActivity().resources.getString(R.string.save), DialogInterface.OnClickListener { dialog, which ->
                val carBrandName = binding2!!.carModelEditText.text.toString()

                try{

                    val database = requireActivity().openOrCreateDatabase("CarModels", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS "+ databaseName + "(id INTEGER PRIMARY KEY, name VARCHAR)")
                    val sqlString = "INSERT INTO " + databaseName + " (name) VALUES (?)"
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1,carBrandName)
                    statement.execute()

                    val cursor = database.rawQuery("SELECT * FROM "+databaseName,null)
                    val carBrandNameIx = cursor.getColumnIndex("name")
                    val idIx = cursor.getColumnIndex("id")

                    carModelList.clear()

                    while (cursor.moveToNext()) {
                        val name = cursor.getString(carBrandNameIx)
                        val id = cursor.getInt(idIx)

                        val carModel = CarModel(name,id)

                        carModelList.add(carModel)

                    }

                    carModelAdapter.notifyDataSetChanged()

                    cursor.close()

                } catch (e : Exception){

                    e.printStackTrace()
                }



            })

        builder.create().show()

    }


}