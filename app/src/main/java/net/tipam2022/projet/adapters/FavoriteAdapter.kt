package net.tipam2022.projet.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import net.tipam2022.projet.*
import net.tipam2022.projet.entities.Menu
import java.util.*


class FavoriteAdapter(
    var context: Context,
    private var menus: ArrayList<Menu>, var callback: (Int)->Unit) :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_menu_card, parent, false)
        return ViewHolder (view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val menu = menus?.get(position)
        Glide.with(context).load(menu?.menuImage.toBitmap()).into(holder.imageView)
        holder.price.text = menu.menuPrice.toString() + " XAF"
        holder.name.text = menu.menuName.toString()

        holder.remove.setOnClickListener {
            deleteOpinion(menu)
        }
        holder.itemView.setOnClickListener {
            callback(position)
        }
    }

    override fun getItemCount(): Int {
        return menus.size
    }

    class ViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.menuImage)
        var price: TextView = itemView.findViewById(R.id.menuPrice)
        var name: TextView = itemView.findViewById(R.id.menuName)
        var remove: ImageButton = itemView.findViewById(R.id.remove)
    }

    private fun deleteOpinion(menu: Menu){
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS)

        showDialog(
            context,
            "Favorite",
            "Do you really want to remove this form your favorites?",
            "Remove",
            "Cancel",
            {_, _ -> var snapshot = databaseReference?.child("${PhoneNumber}${menu?.menuId}${menu?.categoryId}")?.removeValue()
                menus.remove(menu)
            this.notifyDataSetChanged()},
            {_, _ ->}
        )
    }


    object Constants {
        const val DATABASE_PATH_UPLOADS = "opinions"
    }
}