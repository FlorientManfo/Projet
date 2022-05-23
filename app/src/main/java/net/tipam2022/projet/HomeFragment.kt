package net.tipam2022.projet

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
import net.tipam2022.projet.adapters.CategoryAdapter
import net.tipam2022.projet.databinding.FragmentHomeBinding
import net.tipam2022.projet.entities.Category
import org.jetbrains.annotations.Nullable


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding

    private var categoryRecyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    private var categories: ArrayList<Category>? = null
    private var categoryAdapter: CategoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase?.getReference("Projet");

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        progressBar = binding.progress
        categoryRecyclerView = binding.categoriesList

        categories = ArrayList()
        categoryAdapter = CategoryAdapter(requireContext(), categories!!){it -> categoryClickLister(it)}
        categoryRecyclerView?.adapter = categoryAdapter
        getCategories()

        return binding.root
    }


    private fun categoryClickLister(position: Int): Unit{

    }

    private fun getCategories() {
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

    object Constants {
        const val STORAGE_PATH_UPLOADS = "profileImages/"
        const val DATABASE_PATH_UPLOADS = "cateroy"
    }
}