package com.ruchanokal.carcatalog.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ruchanokal.carcatalog.R
import com.ruchanokal.carcatalog.adapter.CarAdapter
import com.ruchanokal.carcatalog.databinding.AuthorizedDialogBinding
import com.ruchanokal.carcatalog.databinding.FragmentCarsBinding
import com.ruchanokal.carcatalog.databinding.LayoutDialogBinding
import com.ruchanokal.carcatalog.model.Car
import java.io.ByteArrayOutputStream


class CarsFragment : Fragment() {

    private var binding : FragmentCarsBinding? = null
    private var binding2 : LayoutDialogBinding? = null
    private var binding3 : AuthorizedDialogBinding? = null
    var selectedBitmap : Bitmap? = null
    private lateinit var carList : ArrayList<Car>
    var uri : Uri? = null
    private lateinit var carAdapter : CarAdapter
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var db : FirebaseFirestore
    var password : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()


    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCarsBinding.inflate(inflater,container,false)
        val view = binding!!.root
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding!!.toolbar)
        requireActivity().title = requireActivity().resources.getString(R.string.car_catalog)

        return view
    }

    override fun onResume() {
        super.onResume()
        println("onResume")

        db = Firebase.firestore

        carList = ArrayList()
        carAdapter = CarAdapter(carList)
        binding!!.carRecylerView.layoutManager = LinearLayoutManager(activity)
        binding!!.carRecylerView.adapter = carAdapter



        try {

            val database = requireActivity().openOrCreateDatabase("Cars", Context.MODE_PRIVATE,null)

            val cursor = database.rawQuery("SELECT * FROM cars",null)
            val carBrandNameIx = cursor.getColumnIndex("name")
            val idIx = cursor.getColumnIndex("id")
            val imageIx = cursor.getColumnIndex("image")

            while (cursor.moveToNext()) {
                val name = cursor.getString(carBrandNameIx)
                val id = cursor.getInt(idIx)
                val byteArray = cursor.getBlob(imageIx)

                val car = Car(name,id,byteArray)

                carList.add(car)

            }

            carAdapter.notifyDataSetChanged()

            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
            println("warn: " + e.message)
            println("warn: " + e.printStackTrace())
            println("warn: " + e.stackTrace)

        }



        binding!!.fab1.setOnClickListener {


            openAuthorizedDialog()

        }
    }

    private fun openAuthorizedDialog() {

        val builder = AlertDialog.Builder(requireActivity())
        binding3 = AuthorizedDialogBinding.inflate(layoutInflater)



        builder.setView(binding3!!.root)
            .setNegativeButton(requireActivity().resources.getString(R.string.cancel), DialogInterface.OnClickListener { dialog, which ->

            }).setPositiveButton(requireActivity().resources.getString(R.string.enter), DialogInterface.OnClickListener { dialog, which ->

                val enteredPassword = binding3!!.passwordText.text.toString()

                db.collection("password").addSnapshotListener { value, error ->

                    if (value != null){

                        if (!value.isEmpty){

                            val documents = value.documents

                            for (document in documents){

                                password = document.getLong("password")!!

                            }


                            if (enteredPassword.toLong() == password){
                                openDialog()
                            } else{
                                Toast.makeText(requireContext(),requireActivity().resources.getString(R.string.wrong_password),Toast.LENGTH_SHORT).show()
                            }

                        }
                    }

                }


            })






        builder.create().show()

    }

    override fun onPause() {
        super.onPause()
        println("onPause")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("onViewCreated")


    }

    private fun openDialog() {

        val builder = AlertDialog.Builder(requireActivity())
        binding2 = LayoutDialogBinding.inflate(layoutInflater)

        builder.setView(binding2!!.root)
            .setTitle(requireActivity().resources.getString(R.string.select_logo_and_brand_name))
            .setNegativeButton(requireActivity().resources.getString(R.string.cancel), DialogInterface.OnClickListener { dialog, which ->

            }).setPositiveButton(requireActivity().resources.getString(R.string.save), DialogInterface.OnClickListener { dialog, which ->
                val carBrandName = binding2!!.carBrandEditText.text.toString()

                if (selectedBitmap != null) {

                    val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)

                    val outputStream = ByteArrayOutputStream()
                    smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
                    val byteArray = outputStream.toByteArray()

                    val database = requireActivity().openOrCreateDatabase("Cars", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS cars (id INTEGER PRIMARY KEY, name VARCHAR, image BLOB)")
                    val sqlString = "INSERT INTO cars (name, image) VALUES (?, ?)"
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1,carBrandName)
                    statement.bindBlob(2,byteArray)
                    statement.execute()

                    val cursor = database.rawQuery("SELECT * FROM cars",null)
                    val carBrandNameIx = cursor.getColumnIndex("name")
                    val idIx = cursor.getColumnIndex("id")
                    val imageIx = cursor.getColumnIndex("image")

                    carList.clear()

                    while (cursor.moveToNext()) {
                        val name = cursor.getString(carBrandNameIx)
                        val id = cursor.getInt(idIx)
                        val byteArray = cursor.getBlob(imageIx)

                        val car = Car(name,id,byteArray)

                        carList.add(car)

                    }


                    carAdapter.notifyDataSetChanged()

                    cursor.close()

                } else if(carBrandName.isNullOrEmpty()){
                    Toast.makeText(requireContext(),resources.getString(R.string.you_should_give_informations),Toast.LENGTH_SHORT).show()
                } else
                    Toast.makeText(requireContext(),resources.getString(R.string.you_must_choose_car_logo),Toast.LENGTH_SHORT).show()

            })



        binding2!!.selectLogoImageView.setOnClickListener {

            if (ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    Snackbar.make(it,requireActivity().resources.getString(R.string.permission_needed), Snackbar.LENGTH_INDEFINITE).setAction(requireActivity().resources.getString(R.string.give_permission),
                        View.OnClickListener {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()

                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }

        }


        builder.create().show()

    }

    private fun makeSmallerBitmap(image: Bitmap, maximumSize : Int) : Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1) {
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        } else {
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image,width,height,true)
    }


    private fun registerLauncher() {

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == Activity.RESULT_OK) {

                val intentFromResult = it.data

                intentFromResult?.let {

                    val imageData = intentFromResult.data

                    imageData?.let {

                        try {
                            if (Build.VERSION.SDK_INT >= 28) {

                                val source = ImageDecoder.createSource(requireContext().contentResolver,imageData)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding2!!.selectLogoImageView.setImageBitmap(selectedBitmap)

                            } else {

                                selectedBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver,imageData)
                                binding2!!.selectLogoImageView.setImageBitmap(selectedBitmap)
                            }

                        } catch (e : Exception){
                            e.printStackTrace()
                        }

                    }

                }

            }

        }


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){

            if (it){
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else{
                Toast.makeText(requireContext(),requireActivity().resources.getString(R.string.permission_needed), Toast.LENGTH_SHORT).show()
            }

        }


    }






}