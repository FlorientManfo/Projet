package net.tipam2022.projet

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import net.tipam2022.projet.adapters.CategoryAdapter
import net.tipam2022.projet.adapters.MenuAdapter
import net.tipam2022.projet.adapters.MostPopularAdapter
import net.tipam2022.projet.databinding.FragmentHomeBinding
import net.tipam2022.projet.entities.Category
import net.tipam2022.projet.entities.Menu
import net.tipam2022.projet.entities.Opinion


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private var progressBar: ProgressBar? = null
    lateinit var binding: FragmentHomeBinding

    var currentCategory: Int = 0
    private var categoryRecyclerView: RecyclerView? = null
    private var categories: ArrayList<Category>? = null
    private var categoryAdapter: CategoryAdapter? = null

    private var menuRecyclerView: RecyclerView? = null
    private var menus: ArrayList<Menu>? = null
    private var menuAdapter: MenuAdapter? = null

    private var mostPopularMenuRecyclerView: RecyclerView? = null
    private var mostPopularMenus: ArrayList<Menu>? = null
    private var mostPopularAdapter: MostPopularAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FrameLayout {

        auth = FirebaseAuth.getInstance();

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        progressBar = binding.progress
        progressBar?.visibility = View.VISIBLE

        categoryRecyclerView = binding.categoriesList
        categories = arrayListOf()
        categoryAdapter = CategoryAdapter(requireContext(), categories){categoryClickLister(it)}
        categoryRecyclerView?.adapter = categoryAdapter
        getCategories()

        menuRecyclerView = binding.menuList
        menus = arrayListOf()
        menuAdapter = MenuAdapter(requireContext(), menus!!){menuClickLister(it)}
        menuRecyclerView?.adapter = menuAdapter

        mostPopularMenuRecyclerView = binding.mostPopularList
        mostPopularMenus = arrayListOf()
        mostPopularAdapter = MostPopularAdapter(requireContext(), mostPopularMenus!!){menuClickLister(it)}
        mostPopularMenuRecyclerView?.adapter = mostPopularAdapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return false
            }

        })


        return binding.root
    }

    private fun filter(text: String?) {
        val filteredList: ArrayList<Menu> = ArrayList()
        getMenus(currentCategory)
        for (item in menus!!) {
            if (item.menuDescription?.toLowerCase()?.contains(text?.toLowerCase()?:"") == true) {
                filteredList.add(item)
            }
        }
        if(filteredList?.size != 0){
            menus = filteredList?.clone() as  ArrayList<Menu>
            menuAdapter?.notifyDataSetChanged()
        }
    }

    private fun categoryClickLister(categoryId: Int?){
        currentCategory = categoryId!!
        getMenus(categoryId)
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

        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categories?.clear()
                binding.progress.visibility = View.GONE
                for (postSnapshot in snapshot.children) {
                    val category: Category? = postSnapshot.getValue(Category::class.java)
                    categories?.add(category!!)
                }
                categoryAdapter?.notifyDataSetChanged()
                if(categories?.size != 0){
                    getMenus(categories?.get(0)?.categoryId)
                    getMostPopularMenus(categories?.get(0)?.categoryId)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                binding.progress.visibility = View.GONE
            }
        })
    }

    private fun getMenus(categoryId: Int?) {
        binding.progress.visibility = View.VISIBLE
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_MENU)

        menus?.clear()
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progress.visibility = View.GONE
                for (postSnapshot in snapshot.children) {
                    val menu: Menu? = postSnapshot.getValue(Menu::class.java)
                    if(menu?.categoryId == categoryId)
                        menus?.add(menu!!)
                }
                menuAdapter?.notifyDataSetChanged()
                getMostPopularMenus(categoryId)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                binding.progress.visibility = View.GONE
            }
        })
    }

    private fun getMostPopularMenus(categoryId: Int?) {
        binding.progress.visibility = View.VISIBLE
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_MENU)

        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mostPopularMenus?.clear()
                binding.progress.visibility = View.GONE
                for (postSnapshot in snapshot.children) {
                    val menu: Menu? = postSnapshot.getValue(Menu::class.java)
                    if(menu?.categoryId == categoryId)
                        getRate(menu)
                }
                mostPopularAdapter?.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                binding.progress.visibility = View.GONE
            }
        })
    }

    private fun getRate(menu: Menu?){
        databaseReference = FirebaseDatabase.getInstance().getReference(DetailsActivity.Constants.DATABASE_PATH_OPINION)
        var finalNote = 0f

        binding.progress.visibility = View.VISIBLE
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var note = 0
                for(postSnapshot in snapshot.children){
                    var opinion = postSnapshot.getValue(Opinion::class.java)
                    if(opinion?.status == true &&
                        opinion?.menuId == menu?.menuId &&
                        opinion?.categoryId == menu?.categoryId){
                        note++
                    }
                }
                finalNote = note.toFloat()
                if(note != 0){
                    finalNote = note%5f
                    if(finalNote == 0f)
                        finalNote = 5f
                }

                if(finalNote>=3f){
                    println("most popular----------------------->$finalNote")
                    mostPopularMenus?.add(menu!!)
                    mostPopularAdapter?.notifyDataSetChanged()
                    println("most popular ***********************> ${mostPopularMenus?.size}")
                }
                binding.progress.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progress.visibility = View.VISIBLE
                TODO("Not yet implemented")
            }
        })
    }

    object Constants {
        const val DATABASE_PATH_MENU = "menus"
        const val DATABASE_PATH_CATEGORY = "categories"
    }

}