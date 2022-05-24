package net.tipam2022.projet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import net.tipam2022.projet.databinding.ActivityDetailsBinding
import net.tipam2022.projet.R
import net.tipam2022.projet.adapters.MenuAdapter
import net.tipam2022.projet.entities.Menu
import org.jetbrains.annotations.Nullable

class DetailsActivity : AppCompatActivity() {

    lateinit var menu: Menu
    lateinit var binding: ActivityDetailsBinding
    //private var progressBar: ProgressBar? = null

    private var menuRecyclerView: RecyclerView? = binding.subMenuList
    private var menus: ArrayList<Menu>? = null
    private var menuAdapter: MenuAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        var bundle: Bundle = intent.extras!!
        menu = bundle.getSerializable("selectedMenu") as Menu
        displayDetails(menu)
    }

    private fun menuClickLister(position: Int): Unit{
        var menu = menus?.get(position)
        var bundle = Bundle()
        bundle.putSerializable("selectedMenu", menu)

        var intent = Intent(this, DetailsActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun displayDetails(menu: Menu) {
        Glide.with(binding.selectedSubMenu).load(menu?.menuImageUrl).into(binding.selectedSubMenu)
        binding.submenuName.text = menu.menuName
        binding.submenuPrice.text = menu.menuPrice.toString()
        binding.detailsParagraph.text = menu.menuDescription

        menus = ArrayList()
        menuAdapter = MenuAdapter(this, menus!!){ it ->
            menuClickLister(it)
        }
        menuRecyclerView?.adapter = menuAdapter
        getMenus()
    }

    private fun getMenus() {
        databaseReference = FirebaseDatabase.getInstance().getReference(HomeFragment.Constants.DATABASE_PATH_MENU)

        menus?.clear()
        databaseReference!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(
                snapshot: DataSnapshot,
                @Nullable previousChildName: String?
            ) {
                snapshot.getValue(Menu::class.java)?.let { menus?.add(it) }
                menuAdapter?.notifyDataSetChanged()
            }

            override fun onChildChanged(
                snapshot: DataSnapshot,
                @Nullable previousChildName: String?
            ) {
                menuAdapter?.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                menuAdapter?.notifyDataSetChanged()
            }

            override fun onChildMoved(
                snapshot: DataSnapshot,
                @Nullable previousChildName: String?
            ) {
                menuAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println("-----------------> ${error.message}")
            }
        })
    }
}