package net.tipam2022.projet.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import net.tipam2022.projet.R
import net.tipam2022.projet.entities.Category
import net.tipam2022.projet.storageReference
import net.tipam2022.projet.toBitmap


class CategoryAdapter(var context: Context,
    var categories: ArrayList<Category>?, var callback: (Int?)->Unit) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedItemPosition: Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_card, parent, false)
        return ViewHolder (view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val category = categories?.get(position)
        if(category?.image == null)
            getDefaultCategoryImage(holder.imageView)
        holder.imageView.setImageBitmap(category?.image?.toBitmap())

        holder.imageView.setOnClickListener {
            selectedItemPosition = position
            callback(categories?.get(position)?.categoryId)
            println("-------------------------->$position")
            notifyDataSetChanged()
        }
        if(selectedItemPosition == position){
            holder.imageView.setBackgroundColor(Color.parseColor("#B647DF"))
        }
        else{
            holder.imageView.setBackgroundColor(Color.parseColor("#FDB81A"))
        }
    }

    override fun getItemCount(): Int {
        return categories?.size?:0
    }

   class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
       var imageView: ImageView = itemView.findViewById(R.id.categoryImage)
       var categoryBackground: CardView = itemView.findViewById(R.id.categoryBackground)
   }

   private fun getDefaultCategoryImage(view: ImageView): String?{
        var generatedFilePath: String? = null
        storageReference = FirebaseStorage.getInstance().getReference("categoryImages/")
        storageReference!!.child("1.jpeg").downloadUrl.addOnSuccessListener(
            OnSuccessListener<Uri?> {
                val downloadUri: Uri = it
                generatedFilePath = downloadUri.toString()
                Glide.with(context).load(generatedFilePath).into(view)
                println("--------->Default image url $generatedFilePath")
            }).addOnFailureListener(OnFailureListener {
        })
        return  generatedFilePath
    }
}


