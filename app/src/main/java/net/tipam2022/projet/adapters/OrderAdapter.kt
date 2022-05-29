package net.tipam2022.projet.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import net.tipam2022.projet.R
import net.tipam2022.projet.entities.Category
import net.tipam2022.projet.entities.Menu
import net.tipam2022.projet.entities.Order
import net.tipam2022.projet.storageReference
import net.tipam2022.projet.toBitmap
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class OrderAdapter(var context: Context,
                   var orders: ArrayList<Order>) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return ViewHolder (view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val order = orders?.get(position)
        println(order.orderId)
        getMenu(order.menuId, holder.imageView, holder.menu)
        holder.date.text = LocalDate.parse(order.date, DateTimeFormatter.ISO_DATE_TIME).toString()
        holder.quantity.text = order.quantity.toString()+ ": "
        holder.price.text = "${order.price.toString()} XAF"
        holder.statute.text = order.statute.name
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView){
        var imageView: ImageView = itemView.findViewById(R.id.menuImage)
        var date: TextView = itemView.findViewById(R.id.date)
        var menu: TextView = itemView.findViewById(R.id.menuName)
        var quantity: TextView = itemView.findViewById(R.id.quantity)
        var price: TextView = itemView.findViewById(R.id.price)
        var statute: TextView = itemView.findViewById(R.id.statute)

    }

    private fun getMenu(menuId: Int, imageView: ImageView, textView: TextView): Menu?{
        var databaseReference = FirebaseDatabase.getInstance().getReference("menus")
        var menu: Menu? = null
        println("----------------------------------------->${menuId}")
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val m: Menu? = postSnapshot.getValue(Menu::class.java)
                    if(m?.menuId == menuId){
                        Glide.with(context).load(m.menuImage.toBitmap()).into(imageView)
                        textView.text = m.menuName
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                TODO()
            }
        })
        return menu
    }
}