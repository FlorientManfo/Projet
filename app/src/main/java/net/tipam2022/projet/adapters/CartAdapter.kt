package net.tipam2022.projet.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import net.tipam2022.projet.*
import net.tipam2022.projet.adapters.CartAdapter.Constants.DATABASE_PATH_MENU
import net.tipam2022.projet.adapters.CartAdapter.Constants.DATABASE_PATH_ORDER
import net.tipam2022.projet.entities.Category
import net.tipam2022.projet.entities.Menu
import net.tipam2022.projet.entities.Order


class CartAdapter(var context: Context,
                   var orders: ArrayList<Order>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item_card, parent, false)
        return ViewHolder (view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders?.get(position)
        getMenu(order.menuId, holder.imageView, holder.menu)
        holder.quantity.setText(order.quantity.toString())
        holder.price.text = "${order.price.toString()} XAF"
        holder.editLayout.visibility = View.GONE
        val price = order.price/order.quantity
        holder.up?.setOnClickListener {
            if (order.quantity >= 0) {
                order.quantity +=1
                order.price = order.quantity * price
                holder.quantity?.setText(order.quantity.toString())
                holder.price?.setText(order.price.toString()+" XAF")
            }
        }
        holder.down?.setOnClickListener {
            if (order.quantity > 0) {
                order.quantity -= 1
                holder.quantity?.setText(order.quantity.toString())
                order.price = order.quantity * price
                holder.price?.setText(order.price.toString()+" XAF")
            }
        }
        holder.drop.setOnClickListener {
            if(holder.editLayout.visibility == View.GONE){
                holder.editLayout.visibility = View.VISIBLE
                holder.drop.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            }
            else{
                holder.editLayout.visibility = View.GONE
                holder.drop.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
            }
        }

        holder.remove.setOnClickListener {
            deleteOrder(order)
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView){
        var imageView: ImageView = itemView.findViewById(R.id.menuImage)
        var menu: TextView = itemView.findViewById(R.id.menuName)
        var price: TextView = itemView.findViewById(R.id.menuPrice)
        var quantity: EditText = itemView.findViewById(R.id.quantity)
        var up: ImageButton = itemView.findViewById(R.id.up)
        var down: ImageButton = itemView.findViewById(R.id.down)
        var drop: ImageButton = itemView.findViewById(R.id.more)
        var remove: ImageButton = itemView.findViewById(R.id.remove)
        var editLayout: RelativeLayout = itemView.findViewById(R.id.editLayout)
    }

    private fun deleteOrder(order: Order){
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_ORDER)

        showDialog(
            context,
            "Favorite",
            "Do you really want to remove this item from your cart?",
            "Remove",
            "Cancel",
            {_, _ -> var snapshot = databaseReference?.child("${order.orderId}")?.removeValue()
                orders.remove(order)
                this.notifyDataSetChanged()},
            {_, _ ->}
        )
    }

    private fun getMenu(menuId: Int, imageView: ImageView, textView: TextView){
        var databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_MENU)
        var menu: Menu? = null
        databaseReference.child(menuId.toString()).get().addOnSuccessListener {

            menu = it.getValue(Menu::class.java)
            Glide.with(context).load(menu?.menuImage.toBitmap()).into(imageView)
            textView.text = menu?.menuName
        }
    }

    object Constants {
        const val DATABASE_PATH_MENU = "menus"
        const val DATABASE_PATH_ORDER = "orders"
    }
}