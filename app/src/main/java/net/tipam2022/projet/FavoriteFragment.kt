package net.tipam2022.projet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import net.tipam2022.projet.FavoriteFragment.Constants.DATABASE_PATH_MENU
import net.tipam2022.projet.adapters.FavoriteAdapter
import net.tipam2022.projet.adapters.MenuAdapter
import net.tipam2022.projet.adapters.MostPopularAdapter
import net.tipam2022.projet.databinding.FragmentFavoriteBinding
import net.tipam2022.projet.entities.Menu
import net.tipam2022.projet.entities.Opinion

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteFragment : Fragment() {
    lateinit var binding: FragmentFavoriteBinding

    private var menus: ArrayList<Menu>? = null
    private var favoriteRecyclerView: RecyclerView? = null
    private var favoriteMenus: ArrayList<Menu>? = null
    private var favoriteAdapter: FavoriteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        binding.progress.visibility = View.VISIBLE
        favoriteRecyclerView = binding.favoriteList
        menus = arrayListOf()
        favoriteMenus = arrayListOf()
        favoriteAdapter = FavoriteAdapter(requireContext(), favoriteMenus!!){favoriteClickLister(it)}
        favoriteRecyclerView?.adapter = favoriteAdapter
        getMenus()

        return binding.root
    }

    private fun favoriteClickLister(position: Int){
        var menu = menus?.get(position)
        var bundle = Bundle()
        bundle.putSerializable("selectedMenu", menu)

        var intent = Intent(requireContext(), DetailsActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun getFavorites(menu: Menu){
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_OPINION)

        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children){
                    var opinion = postSnapshot.getValue(Opinion::class.java)
                    if(opinion?.status == true &&
                        opinion?.userPhoneNumber == PhoneNumber &&
                        menu.menuId == opinion?.menuId){
                        favoriteMenus?.add(menu)
                    }
                }
                favoriteAdapter?.notifyDataSetChanged()
                binding.progress.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progress.visibility = View.GONE
                TODO("Not yet implemented")
            }
        })
    }

    private fun getMenus() {
        binding.progress.visibility = View.VISIBLE
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_MENU)

        menus?.clear()
        favoriteMenus?.clear()
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progress.visibility = View.GONE
                for (postSnapshot in snapshot.children) {
                    val menu: Menu? = postSnapshot.getValue(Menu::class.java)
                    menus?.add(menu!!)
                }
                for(menu in menus!!){
                    getFavorites(menu)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                binding.progress.visibility = View.GONE
            }
        })
    }
    object Constants {
        const val DATABASE_PATH_OPINION= "opinions"
        const val DATABASE_PATH_MENU= "menus"
    }
}