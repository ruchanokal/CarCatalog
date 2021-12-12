package com.ruchanokal.carcatalog.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.ruchanokal.carcatalog.R
import com.ruchanokal.carcatalog.databinding.FragmentModelDetailsTab1Binding
import java.io.ByteArrayOutputStream


class ModelDetailsFragmentTab1 : Fragment() {

    private var binding : FragmentModelDetailsTab1Binding? = null
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var selectedBitmap : Bitmap? = null
    var carIndex = 0
    var modelIndex = 0
    var databaseName = ""
    private var byteArray = byteArrayOf()
    private var byteArray2 = byteArrayOf()
    var torque = ""
    var power = ""
    var production = ""
    var detail = ""
    private lateinit var database : SQLiteDatabase


    fun newInstance(carIndex: Int, modelIndex: Int): ModelDetailsFragmentTab1 {
        val modelDetailsFragmentTab1 = ModelDetailsFragmentTab1()
        val args = Bundle()
        args.putInt("carIndex", carIndex)
        args.putInt("modelIndex",modelIndex)
        modelDetailsFragmentTab1.setArguments(args)
        return modelDetailsFragmentTab1
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentModelDetailsTab1Binding.inflate(inflater,container,false)
        val view = binding!!.root


        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        registerLauncher()

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
                                binding!!.carModelImageView.setImageBitmap(selectedBitmap)

                            } else {

                                selectedBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver,imageData)
                                binding!!.carModelImageView.setImageBitmap(selectedBitmap)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = requireActivity().openOrCreateDatabase("CarModelDetails", Context.MODE_PRIVATE,null)


        arguments?.let {

            carIndex = it.getInt("carIndex")
            modelIndex = it.getInt("modelIndex")

        }

        databaseName = "carModelDetails"+carIndex+modelIndex


        try {

            val cursor = database.rawQuery("SELECT * FROM " + databaseName,null)
            val idIx = cursor.getColumnIndex("id")
            val torqueIx = cursor.getColumnIndex("torque")
            val powerIx = cursor.getColumnIndex("power")
            val productionIx = cursor.getColumnIndex("production")
            val detailIx = cursor.getColumnIndex("detail")
            val imageIx = cursor.getColumnIndex("image")

            while (cursor.moveToNext()) {
                torque = cursor.getString(torqueIx)
                power = cursor.getString(powerIx)
                production = cursor.getString(productionIx)
                detail = cursor.getString(detailIx)
                val id = cursor.getInt(idIx)
                byteArray2 = cursor.getBlob(imageIx)
            }

            cursor.close()

        } catch ( e : Exception) {
            e.printStackTrace()
        }


        if (torque.isNotEmpty() &&
            power.isNotEmpty() &&
            production.isNotEmpty() &&
            detail.isNotEmpty() &&
            byteArray2 != null && byteArray2.size > 0) {


            binding!!.saveButton.visibility = View.GONE

            binding!!.powerTextInputLayout.visibility = View.GONE
            binding!!.torqueTextInputLayout.visibility = View.GONE
            binding!!.productionTextInputLayout.visibility = View.GONE
            binding!!.detailsTextInputLayout.visibility = View.GONE

            binding!!.detailsTextView.visibility = View.VISIBLE
            binding!!.powerTextView.visibility = View.VISIBLE
            binding!!.torqueTextView.visibility = View.VISIBLE
            binding!!.productionTextView.visibility = View.VISIBLE

            binding!!.detailsTextView.text = detail
            binding!!.powerTextView.text = power
            binding!!.torqueTextView.text = torque
            binding!!.productionTextView.text = production

            val bitmap = BitmapFactory.decodeByteArray(byteArray2,0,byteArray2.size)
            binding!!.carModelImageView.setImageBitmap(bitmap)
            binding!!.carModelImageView.isClickable = false

        } else  {

            invalidateOptionsMenu(requireActivity())
        }



        binding!!.saveButton.setOnClickListener {


            val torqueText = binding!!.torqueEditText.text.toString()
            val powerText = binding!!.powerEditText.text.toString()
            val productionText = binding!!.productionEditText.text.toString()
            val detailText = binding!!.detailsTextInputEditText.text.toString()

            if (selectedBitmap != null) {

                val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 300)

                val outputStream = ByteArrayOutputStream()
                smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
                byteArray = outputStream.toByteArray()

            } else if (selectedBitmap == null && byteArray2.size > 0){

                byteArray = byteArray2
            }


            if (torqueText.isNullOrEmpty() || powerText.isNullOrEmpty()
                || productionText.isNullOrEmpty() || detailText.isNullOrEmpty())  {

                Toast.makeText(requireContext(),requireActivity().resources.getString(R.string.please_enter_the_required_informations), Toast.LENGTH_SHORT).show()

            } else if(byteArray == null || byteArray.size == 0) {

                Toast.makeText(requireContext(),requireActivity().resources.getString(R.string.please_choose_car_picture), Toast.LENGTH_SHORT).show()

            } else {


                try {

                    database.execSQL("CREATE TABLE IF NOT EXISTS " + databaseName +"(id INTEGER PRIMARY KEY, torque VARCHAR, power VARCHAR, production VARCHAR, detail VARCHAR, image BLOB)")
                    val sqlString = "INSERT INTO " + databaseName + "(torque, power, production, detail, image) VALUES (?, ?, ?, ?, ?)"
                    val statement = database.compileStatement(sqlString)

                    statement.bindString(1,torqueText)
                    statement.bindString(2,powerText)
                    statement.bindString(3,productionText)
                    statement.bindString(4,detailText)
                    statement.bindBlob(5,byteArray)
                    statement.execute()

                    binding!!.saveButton.visibility = View.GONE

                    binding!!.powerTextInputLayout.visibility = View.GONE
                    binding!!.torqueTextInputLayout.visibility = View.GONE
                    binding!!.productionTextInputLayout.visibility = View.GONE
                    binding!!.detailsTextInputLayout.visibility = View.GONE

                    binding!!.detailsTextView.visibility = View.VISIBLE
                    binding!!.powerTextView.visibility = View.VISIBLE
                    binding!!.torqueTextView.visibility = View.VISIBLE
                    binding!!.productionTextView.visibility = View.VISIBLE

                    binding!!.detailsTextView.text = detailText
                    binding!!.powerTextView.text = powerText
                    binding!!.torqueTextView.text = torqueText
                    binding!!.productionTextView.text = productionText

                    detail = detailText
                    power = powerText
                    torque = torqueText
                    production = productionText

                } catch (e : Exception) {
                    e.printStackTrace()
                }



            }
        }


        binding!!.carModelImageView.setOnClickListener {

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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.car_model_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)

        if (torque.isNullOrEmpty() && power.isNullOrEmpty() && production.isNullOrEmpty()){
            val editItem = menu.findItem(R.id.edit)
            editItem.setVisible(false)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item?.itemId == R.id.edit) {

            binding!!.saveButton.visibility = View.VISIBLE

            binding!!.powerTextInputLayout.visibility = View.VISIBLE
            binding!!.torqueTextInputLayout.visibility = View.VISIBLE
            binding!!.productionTextInputLayout.visibility = View.VISIBLE
            binding!!.detailsTextInputLayout.visibility = View.VISIBLE

            binding!!.detailsTextView.visibility = View.GONE
            binding!!.powerTextView.visibility = View.GONE
            binding!!.torqueTextView.visibility = View.GONE
            binding!!.productionTextView.visibility = View.GONE

            binding!!.detailsTextInputEditText.setText(detail)
            binding!!.powerEditText.setText(power)
            binding!!.torqueEditText.setText(torque)
            binding!!.productionEditText.setText(production)

        }

        return super.onOptionsItemSelected(item)
    }

}