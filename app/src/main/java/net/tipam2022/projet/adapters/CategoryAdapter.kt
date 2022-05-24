package net.tipam2022.projet.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.tipam2022.projet.R
import net.tipam2022.projet.entities.Category


class CategoryAdapter(var context: Context,
    var categories: ArrayList<Category>, var callback: (Int)->Unit) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedItemPosition: Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_card, parent, false)
        return ViewHolder (view, callback)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val category = categories?.get(position)
        Glide.with(holder.imageView).load(category?.imageUrl).into(holder.imageView)

        holder.itemView.setOnClickListener {
            selectedItemPosition = position
            notifyDataSetChanged()
        }
        if(selectedItemPosition == position)
            holder.cardView.setBackgroundColor(R.color.input_border)
        else
            holder.cardView.setBackgroundColor(R.color.white)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

   class ViewHolder(itemView: View, var clickLister: (Int)->Unit) :
       RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var imageView: ImageView = itemView.findViewById(R.id.categoryImage)
       var cardView: CardView = itemView.findViewById(R.id.cardView)

       override fun onClick(p0: View?) {
            val position = adapterPosition
            clickLister(position)
        }
    }
}