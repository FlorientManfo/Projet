package net.tipam2022.projet.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import net.tipam2022.projet.R
import net.tipam2022.projet.entities.Order
import net.tipam2022.projet.storageReference


class OrderAdapter(var context: Context,
                   var orders: ArrayList<Order>, var callback: (Int)->Unit) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return ViewHolder (view, callback)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders?.get(position)
        Glide.with(context).load(findMenuImage(order.menuId)).into(holder.imageView)
        holder.date.text = order.date
        holder.quantity.text = order.quantity.toString()
        holder.price.text = "${order.price.toString()} XAF"
        holder.menu.text = order.menuId
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class ViewHolder(itemView: View, var clickLister: (Int)->Unit) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var imageView: ImageView
        var date: TextView
        var menu: TextView
        var quantity: TextView
        var price: TextView
        init {
            date = itemView.findViewById(R.id.date)
            price = itemView.findViewById(R.id.price)
            menu = itemView.findViewById(R.id.menuName)
            quantity = itemView.findViewById(R.id.quantity)
            imageView = itemView.findViewById(R.id.menuImage)
        }
        override fun onClick(p0: View?) {
            val position = adapterPosition
            clickLister(position)
        }
    }

    private fun findMenuImage(menuId: String?): String?{
        storageReference = FirebaseStorage.getInstance().getReference("menus")
        var generatedFilePath: String? = null
        storageReference!!.child("$menuId.png").downloadUrl.addOnSuccessListener(
            OnSuccessListener<Uri?> {
                // Got the download URL for 'profileImages/default_profile.png'
                val downloadUri: Uri = it
                generatedFilePath = downloadUri.toString() /// The string(file link) that you need
            }).addOnFailureListener(OnFailureListener {
        })
        return generatedFilePath
    }
}