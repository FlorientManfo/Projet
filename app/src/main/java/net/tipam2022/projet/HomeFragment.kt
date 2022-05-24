package net.tipam2022.projet

import android.content.Intent
import android.icu.text.Transliterator
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import net.tipam2022.projet.adapters.CategoryAdapter
import net.tipam2022.projet.adapters.MenuAdapter
import net.tipam2022.projet.databinding.FragmentHomeBinding
import net.tipam2022.projet.entities.Category
import net.tipam2022.projet.entities.Menu
import org.jetbrains.annotations.Nullable
import java.nio.file.attribute.AclEntry


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private var progressBar: ProgressBar? = null

    private var categoryRecyclerView: RecyclerView? = null
    private var categories: ArrayList<Category>? = null
    private var categoryAdapter: CategoryAdapter? = null

    private var menuRecyclerView: RecyclerView? = null
    private var menus: ArrayList<Menu>? = null
    private var menuAdapter: MenuAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance();

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        progressBar = binding.progress
        categoryRecyclerView = binding.categoriesList
        menuRecyclerView = binding.categoryMenu

        categories = ArrayList()
        categoryAdapter = CategoryAdapter(requireContext(), categories!!){ it ->
            categoryClickLister(it)
        }
        categoryRecyclerView?.adapter = categoryAdapter
        getCategories()

        menus = ArrayList()
        menuAdapter = MenuAdapter(requireContext(), menus!!){ it ->
            menuClickLister(it)
        }
        menuRecyclerView?.adapter = menuAdapter
        getMenus()

        return binding.root
    }


    private fun categoryClickLister(position: Int): Unit{
        menus = menus?.filter {
                it -> it.categoryId == categories?.get(position)?.categoryId
        } as ArrayList<Menu>
    }

    private fun menuClickLister(position: Int): Unit{
        var menu = menus?.get(position)
        var bundle = Bundle()
        bundle.putSerializable("selectedMenu", menu)

        var intent = Intent(requireContext(), DetailsActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }



    private fun getCategories() {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_CATEGORY)
        storageReference = FirebaseStorage.getInstance().getReference(Constants.STORAGE_PATH_CATEGORY)

        categories?.clear()
        databaseReference!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(
                snapshot: DataSnapshot,
                @Nullable previousChildName: String?
            ) {
                progressBar?.visibility = View.GONE
                snapshot.getValue(Category::class.java)?.let { categories?.add(it) }
                categoryAdapter?.notifyDataSetChanged()
            }

            override fun onChildChanged(
                snapshot: DataSnapshot,
                @Nullable previousChildName: String?
            ) {
                progressBar?.visibility = View.GONE
                categoryAdapter?.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                categoryAdapter?.notifyDataSetChanged()
                progressBar?.visibility = View.GONE
            }

            override fun onChildMoved(
                snapshot: DataSnapshot,
                @Nullable previousChildName: String?
            ) {
                categoryAdapter?.notifyDataSetChanged()
                progressBar?.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                println("----------------->${error.message}")
            }
        })
    }

    private fun getMenus() {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_MENU)
        storageReference = FirebaseStorage.getInstance().getReference(Constants.STORAGE_PATH_MENU)

        menus?.clear()
        databaseReference!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(
                snapshot: DataSnapshot,
                @Nullable previousChildName: String?
            ) {
                progressBar?.visibility = View.GONE
                snapshot.getValue(Menu::class.java)?.let { menus?.add(it) }
                menuAdapter?.notifyDataSetChanged()
            }

            override fun onChildChanged(
                snapshot: DataSnapshot,
                @Nullable previousChildName: String?
            ) {
                progressBar?.visibility = View.GONE
                menuAdapter?.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                menuAdapter?.notifyDataSetChanged()
                progressBar?.visibility = View.GONE
            }

            override fun onChildMoved(
                snapshot: DataSnapshot,
                @Nullable previousChildName: String?
            ) {
                menuAdapter?.notifyDataSetChanged()
                progressBar?.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                println("-----------------> ${error.message}")
            }
        })
    }



    object Constants {
        const val STORAGE_PATH_MENU = "menuImages/"
        const val DATABASE_PATH_MENU = "menus"
        const val STORAGE_PATH_CATEGORY = "categoryImages/"
        const val DATABASE_PATH_CATEGORY = "categories"
    }

}