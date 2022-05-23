package net.tipam2022.projet.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.tipam2022.projet.R
import net.tipam2022.projet.entities.Category
import net.tipam2022.projet.entities.Menu


class MenuAdapter(
    var context: Context,
    var menus: ArrayList<Menu>, var callback: (Int)->Unit) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_card, parent, false)
        return ViewHolder (view, callback)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val menu = menus?.get(position)
        Glide.with(holder.imageView).load(menu?.menuImageUrl).into(holder.imageView)
        holder.price.text = menu.menuPrice.toString()
        holder.name.text = menu.menuName.toString()
    }

    override fun getItemCount(): Int {
        return menus.size
    }

   class ViewHolder(itemView: View, var clickLister: (Int)->Unit) :
       RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var imageView: ImageView
        var price: TextView
        var name: TextView

        init {
            imageView = itemView.findViewById(R.id.menuImage)
            price = itemView.findViewById(R.id.menuPrice)
            name = itemView.findViewById(R.id.menuName)
        }
        override fun onClick(p0: View?) {
            val position = adapterPosition
            clickLister(position)
        }
    }
}