package net.tipam2022.projet.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import net.tipam2022.projet.R
import net.tipam2022.projet.entities.Menu
import net.tipam2022.projet.toBitmap
import org.w3c.dom.Text


class MostPopularAdapter(
    var context: Context,
    private var menus: ArrayList<Menu>, var callback: (Int)->Unit) :
    RecyclerView.Adapter<MostPopularAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.most_popular_card, parent, false)
        return ViewHolder (view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val menu = menus?.get(position)
        Glide.with(context).load(menu?.menuImage.toBitmap()).into(holder.imageView)
        holder.textView.text = menu.menuName
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
        var textView: TextView = itemView.findViewById(R.id.menuName)
    }
}