package net.tipam2022.projet.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import net.tipam2022.projet.*
import net.tipam2022.projet.entities.Menu
import net.tipam2022.projet.entities.Opinion
import java.util.*
import kotlin.collections.ArrayList


class MenuAdapter(
    var context: Context,
    private var menus: ArrayList<Menu>, var callback: (Int)->Unit) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_card, parent, false)
        return ViewHolder (view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val menu = menus?.get(position)
        Glide.with(context).load(menu?.menuImage.toBitmap()).into(holder.imageView)
        holder.price.text = menu.menuPrice.toString()
        holder.name.text = menu.menuName.toString()
        var opinion: Opinion? = getOpinion(menu, holder.like)

        holder.buyButton.setOnClickListener {
            callback(position)
        }
        holder.like.setOnClickListener {
            setOpinion(menu, holder.like.isChecked, null)
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
       var like: CheckBox = itemView.findViewById(R.id.favorite)
       var buyButton: Button = itemView.findViewById(R.id.buyMenu)
   }

    private fun setOpinion(menu: Menu, like: Boolean, comment: String?){
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS)

        var lastOpinion: Opinion? = getOpinion(menu, null)

        var opinion: Opinion? = Opinion(
            PhoneNumber,
            menu.menuId,
            menu.categoryId,
            lastOpinion?.date?: Date().toString(),
            like,
            lastOpinion?.comment?:comment)
        databaseReference?.child("${opinion?.userPhoneNumber}${opinion?.menuId}${opinion?.categoryId}")?.setValue(opinion)

    }

    private fun getOpinion(menu: Menu, checkBox: CheckBox?): Opinion?{
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS)

        var opinion: Opinion? = null
        var snapshot = databaseReference?.child("${PhoneNumber}${menu?.menuId}${menu?.categoryId}")?.get()
        snapshot?.addOnSuccessListener {
            opinion = it.getValue(Opinion::class.java)
            if(checkBox!=null)
                checkBox.isChecked = opinion?.status?:false
        }
        return opinion
    }

    object Constants {
        const val DATABASE_PATH_UPLOADS = "opinions"
    }
}