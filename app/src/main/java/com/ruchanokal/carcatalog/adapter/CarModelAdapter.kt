package com.ruchanokal.carcatalog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.ruchanokal.carcatalog.R
import com.ruchanokal.carcatalog.databinding.CarModelRecyclerRowBinding
import com.ruchanokal.carcatalog.fragment.CarModelsFragmentDirections
import com.ruchanokal.carcatalog.fragment.CarsFragmentDirections
import com.ruchanokal.carcatalog.model.Car
import com.ruchanokal.carcatalog.model.CarModel
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec

class CarModelAdapter(val carList: ArrayList<CarModel>,val carIndex : Int) : RecyclerView.Adapter<CarModelAdapter.CarModelHolder>() {

    class CarModelHolder(val binding: CarModelRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarModelHolder {
        val binding = CarModelRecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CarModelHolder(binding)

    }

    override fun onBindViewHolder(holder: CarModelHolder, position: Int) {

        holder.binding.carModelNameTextView.text = carList.get(position).name

        holder.itemView.setOnClickListener {

            val action = CarModelsFragmentDirections.actionCarModelsFragmentToModelDetailsFragment(carList.get(position).id,carIndex,carList.get(position).name)
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
                .setHeight(BalloonSizeSpec.WRAP)
                .setCornerRadius(4f)
                .setBackgroundDrawableResource(R.drawable.mini_popup_bg)
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setLifecycleOwner(holder.itemView.context as LifecycleOwner)
                .build()

            val databaseName = "carmodels${carIndex}"

            val buttonDelete = balloon.getContentView().findViewById<CardView>(R.id.deleteCardView)
            buttonDelete.setOnClickListener {

                try {

                    val database = holder.itemView.context.openOrCreateDatabase("CarModels", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS "+ databaseName + "(id INTEGER PRIMARY KEY, name VARCHAR)")
                    database.execSQL("DELETE FROM " + databaseName + " WHERE id = " + carList.get(position).id)

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


