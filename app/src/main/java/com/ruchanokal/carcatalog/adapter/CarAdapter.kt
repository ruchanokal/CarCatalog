package com.ruchanokal.carcatalog.adapter

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.ruchanokal.carcatalog.R
import com.ruchanokal.carcatalog.databinding.CarRecyclerRowBinding
import com.ruchanokal.carcatalog.databinding.LayoutDialogBinding
import com.ruchanokal.carcatalog.fragment.CarsFragmentDirections
import com.ruchanokal.carcatalog.model.Car
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import java.io.ByteArrayOutputStream


class CarAdapter (val carList: ArrayList<Car>) : RecyclerView.Adapter<CarAdapter.CarHolder>() {



    class CarHolder (val binding: CarRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarHolder {
        val binding = CarRecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CarHolder(binding)
    }

    override fun onBindViewHolder(holder: CarHolder, position: Int) {


        holder.binding.carNameTextView.text = carList.get(position).name

        val byteArray = carList.get(position).imageByteArray
        val bitmap = byteArray?.let { BitmapFactory.decodeByteArray(byteArray,0, it.size) }
        holder.binding.carLogoImageView.setImageBitmap(bitmap)

        holder.itemView.setOnClickListener {

            val action = CarsFragmentDirections.actionCarsFragmentToCarModelsFragment(carList.get(position).id,carList.get(position).name)
            Navigation.findNavController(it).navigate(action)

        }

        holder.itemView.setOnLongClickListener {

            val balloon = Balloon.Builder(holder.itemView.context)
                .setLayout(R.layout.update_dialog)
                .setArrowSize(9)
                .setArrowColorMatchBalloon(true)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowPosition(0.5f)
                .setWidth(BalloonSizeSpec.WRAP)
                //.setWidthRatio(0.5F)
                .setHeight(BalloonSizeSpec.WRAP)
                .setCornerRadius(4f)
                .setBackgroundDrawableResource(R.drawable.mini_popup_bg)
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setLifecycleOwner(holder.itemView.context as LifecycleOwner)
                .build()

            val buttonDelete = balloon.getContentView().findViewById<CardView>(R.id.deleteCardView)
            buttonDelete.setOnClickListener {

                try {

                    val database = holder.itemView.context.openOrCreateDatabase("Cars", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS cars (id INTEGER PRIMARY KEY, name VARCHAR, image BLOB)")

                    database.execSQL("DELETE FROM cars WHERE id = " + carList.get(position).id)

                    carList.removeAt(position)
                    this.notifyItemRemoved(position)
                    this.notifyItemRangeChanged(position, carList.size)
                    this.notifyDataSetChanged()

                } catch (e:Exception){
                    e.printStackTrace()
                }

                balloon.dismiss()

            }

            balloon.show(it)




            true
        }


    }


    override fun getItemCount(): Int {
        return carList.size
    }
}