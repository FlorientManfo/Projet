package net.tipam2022.projet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import net.tipam2022.projet.adapters.CategoryAdapter
import net.tipam2022.projet.databinding.FragmentProfileBinding
import net.tipam2022.projet.entities.Category
import net.tipam2022.projet.entities.User
import org.jetbrains.annotations.Nullable


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    lateinit var currentUser: User

    private var orders: ArrayList<Category>? = null
    private var orderAdapter: CategoryAdapter? = null
    private var orderRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)



        binding.editButton.setOnClickListener { goToEdit() }
        binding.progress.visibility = View.VISIBLE
        getInformation(requireContext())


        orders = ArrayList()
        orderAdapter = CategoryAdapter(requireContext(), orders!!){it -> orderClickLister(it)}
        orderRecyclerView?.adapter = orderAdapter
        getOrders()

        binding.topBar.setOnMenuItemClickListener { menuItemListener(it) }

        return binding.root
    }

    private fun goToEdit(){
        var intent = Intent(requireContext(), EditProfileActivity::class.java)
        startActivity(intent)
        (activity as Activity?)!!.overridePendingTransition(0, 0)
    }

    private fun getInformation(context: Context){
        storageReference = FirebaseStorage.getInstance()
            .getReference(Constants.STORAGE_PATH_UPLOADS)
        databaseReference = FirebaseDatabase.getInstance()
            .getReference(Constants.DATABASE_PATH_UPLOADS)
        //adding an event listener to fetch values

        //adding an event listener to fetch values
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //dismissing the progress dialog
                binding.progress.visibility = View.GONE

                //iterating through all the values in database
                for (postSnapshot in snapshot.children) {
                    val user: User? = postSnapshot.getValue(User::class.java)
                    if(user?.userPhoneNumber == PhoneNumber)
                        currentUser = user!!
                }

                if(currentUser.profileUrl == null)
                    getDefaultProfileImage()
                else
                    Glide.with(binding.profileImage).load(currentUser.profileUrl).into(binding.profileImage)

                println(currentUser.profileUrl)
                binding.phoneNumber.text = currentUser.userPhoneNumber.toString()
                binding.userName.text = currentUser.userName
            }
            override fun onCancelled(databaseError: DatabaseError) {
                binding.progress.visibility = View.GONE
            }
        })

    }

    fun getDefaultProfileImage(){
        var generatedFilePath: String? = null
        storageReference!!.child("default_profile.png").downloadUrl.addOnSuccessListener(
            OnSuccessListener<Uri?> {
                // Got the download URL for 'profileImages/default_profile.png'
                val downloadUri: Uri = it
                generatedFilePath = downloadUri.toString() /// The string(file link) that you need
                Glide.with(binding.profileImage).load(generatedFilePath).into(binding.profileImage)
            }).addOnFailureListener(OnFailureListener {
        })
    }

    private fun getOrders() {
        databaseReference = FirebaseDatabase.getInstance().getReference("orders/$PhoneNumber")
        orders?.clear()
        databaseReference!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(
                snapshot: DataSnapshot,
                @Nullable previousChildName: String?
            ) {
                binding.progress?.visibility = View.GONE
                snapshot.getValue(Category::class.java)?.let { orders?.add(it) }
                orderAdapter?.notifyDataSetChanged()
            }

            override fun onChildChanged(
                snapshot: DataSnapshot,
                @Nullable previousChildName: String?
            ) {
                binding.progress?.visibility = View.GONE
                orderAdapter?.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                orderAdapter?.notifyDataSetChanged()
                binding.progress?.visibility = View.GONE
            }

            override fun onChildMoved(
                snapshot: DataSnapshot,
                @Nullable previousChildName: String?
            ) {
                orderAdapter?.notifyDataSetChanged()
                binding.progress?.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                println("----------------->${error.message}")
            }
        })
    }

    private fun orderClickLister(orderId: Int){

    }

    private fun menuItemListener(item: MenuItem): Boolean{
        return when (item.itemId) {
            R.id.signOut -> {
                auth = FirebaseAuth.getInstance()
                if (auth?.currentUser != null) {
                    auth?.signOut()
                    startActivity(Intent(activity, SignInActivity::class.java))
                }
                println("Logout!")
                true
            }
            R.id.notification -> {
                // save profile changes
                true
            }
            else -> false
        }
    }

    object Constants {
        const val STORAGE_PATH_UPLOADS = "profileImages/"
        const val DATABASE_PATH_UPLOADS = "users"
    }
}