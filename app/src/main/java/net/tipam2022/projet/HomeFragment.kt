package net.tipam2022.projet

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import net.tipam2022.projet.adapters.CategoryAdapter
import net.tipam2022.projet.adapters.MenuAdapter
import net.tipam2022.projet.databinding.FragmentHomeBinding
import net.tipam2022.projet.entities.Category
import net.tipam2022.projet.entities.Menu
import net.tipam2022.projet.entities.User
import org.jetbrains.annotations.Nullable


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private var progressBar: ProgressBar? = null
    lateinit var binding: FragmentHomeBinding

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
        categories = arrayListOf()
        categoryAdapter = CategoryAdapter(requireContext(), categories){categoryClickLister(it)}
        categoryRecyclerView?.adapter = categoryAdapter
        getCategories()

        menuRecyclerView = binding.menuList
        menus = arrayListOf()
        menuAdapter = MenuAdapter(requireContext(), menus!!){menuClickLister(it)}
        menuRecyclerView?.adapter = menuAdapter
        getMenus()

        return binding.root
    }


    private fun categoryClickLister(position: Int): Unit{
        getMenus()
        Toast.makeText(requireContext(), "$position", Toast.LENGTH_LONG)
        menus = menus?.filter {it -> it.categoryId ==
                categories?.get(position)?.categoryId} as ArrayList<Menu>
    }

    private fun menuClickLister(position: Int){
        var menu = menus?.get(position)
        var bundle = Bundle()
        bundle.putSerializable("selectedMenu", menu)

        var intent = Intent(requireContext(), DetailsActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }



    private fun getCategories() {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_CATEGORY)

        categories?.clear()
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progress.visibility = View.GONE
                categories = ArrayList()
                for (postSnapshot in snapshot.children) {
                    val category: Category? = postSnapshot.getValue(Category::class.java)
                    categories?.add(category!!)
                }
                categoryAdapter?.notifyDataSetChanged()
                categoryRecyclerView?.adapter =  CategoryAdapter(requireContext(), categories){categoryClickLister(it)}
                Toast.makeText(requireContext(), "${categories?.size}", Toast.LENGTH_LONG).show()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                binding.progress.visibility = View.GONE
            }
        })
    }

    private fun getMenus() {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_MENU)

        menus?.clear()
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progress.visibility = View.GONE
                categories = ArrayList()
                for (postSnapshot in snapshot.children) {
                    val menu: Menu? = postSnapshot.getValue(Menu::class.java)
                    menus?.add(menu!!)
                }
                menuAdapter?.notifyDataSetChanged()
                menuRecyclerView?.adapter =  MenuAdapter(requireContext(), menus!!){menuClickLister(it)}
                Toast.makeText(requireContext(), "${menus?.size}", Toast.LENGTH_LONG).show()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                binding.progress.visibility = View.GONE
            }
        })
    }

    object Constants {
        const val DATABASE_PATH_MENU = "menus"
        const val DATABASE_PATH_CATEGORY = "categories"
    }

}