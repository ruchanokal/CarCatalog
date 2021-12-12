package com.ruchanokal.carcatalog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.ruchanokal.carcatalog.R
import com.ruchanokal.carcatalog.databinding.CarPhotosRowBinding

class CarPhotosAdapter(val urlList : ArrayList<String>) : RecyclerView.Adapter<CarPhotosAdapter.CarHolder>() {

    class CarHolder(val binding : CarPhotosRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarHolder {
        val binding = CarPhotosRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CarHolder(binding)
    }

    override fun onBindViewHolder(holder: CarHolder, position: Int) {

        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Glide.with(holder.itemView.context)
            .load(urlList.get(position))
            .placeholder(circularProgressDrawable)
            .into(holder.binding.carPhotoImageView);

    }

    override fun getItemCount(): Int {
        return urlList.size
    }

}