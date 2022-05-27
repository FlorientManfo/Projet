package net.tipam2022.projet

import android.os.Bundle
import net.tipam2022.projet.CartFragment
import net.tipam2022.projet.R
import net.tipam2022.projet.FavoriteFragment
import net.tipam2022.projet.HomeFragment
import net.tipam2022.projet.ProfileFragment
import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.os.Build
import android.view.*
import android.view.View.OnTouchListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import net.tipam2022.projet.CartFragment.Constants.DATABASE_PATH_CART
import net.tipam2022.projet.CartFragment.Constants.DATABASE_PATH_ORDER
import net.tipam2022.projet.SplashActivity
import net.tipam2022.projet.adapters.Cart
import net.tipam2022.projet.adapters.CartAdapter
import net.tipam2022.projet.adapters.FavoriteAdapter
import net.tipam2022.projet.databinding.ActivityMainBinding
import net.tipam2022.projet.databinding.FragmentCartBinding
import net.tipam2022.projet.entities.Menu
import net.tipam2022.projet.entities.Order
import net.tipam2022.projet.entities.OrderStatus
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [CartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CartFragment : Fragment() {
    lateinit var binding: FragmentCartBinding

    private var orders: ArrayList<Order>? = null
    private var cartItemRecyclerView: RecyclerView? = null
    private var cartAdapter: CartAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        binding.progress.visibility = View.VISIBLE

        cartItemRecyclerView = binding.cartItemList
        orders = arrayListOf()
        cartAdapter = CartAdapter(requireContext(), orders!!)
        cartItemRecyclerView?.adapter = cartAdapter
        getCurrentOrders()

        binding.validatedCart.setOnClickListener { submitCart() }

        return binding.root
    }

    private fun getCurrentOrders(){
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_ORDER)

        databaseReference?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                orders?.clear()
                for(dataSnapshot in snapshot.children){
                    var order = dataSnapshot.getValue(Order::class.java)
                    if(order?.userPhoneNumber == PhoneNumber && order?.statute == OrderStatus.NotValidated){
                        orders?.add(order)
                    }
                }
                cartAdapter?.notifyDataSetChanged()
                binding.progress.visibility = View.GONE
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        )
    }

    private fun submitCart(){
        if(orders != null && orders?.size!=0){
            databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_ORDER)
            var ok = true
            for(order in orders!!){
                if(order.quantity == 0){
                    ok = false
                }
            }
            if(!ok){
                showDialog(
                    requireContext(),
                    "Cart statute",
                    "Some order haven't good quantity !",
                    "KO",
                    null,
                    {_,_->},
                    {_,_->}
                )
            }
            else{
                var totalPrice = 0f
                binding.progress.visibility = View.VISIBLE
                for(order in orders!!){
                    order.statute = OrderStatus.Validated
                    totalPrice += order.price
                    databaseReference?.child(order.orderId.toString())?.setValue(order)
                }
                databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_CART)
                var jsonStringer = Gson().toJson(orders)
                var cartId = databaseReference?.push()?.key
                var cart = Cart(
                    cartId!!,
                    orders,
                    Date().toString(),
                    totalPrice)

                databaseReference?.child(cartId)?.setValue(cart)?.addOnCompleteListener {
                    it.addOnSuccessListener {
                        binding.progress.visibility = View.GONE
                        showDialog(
                            requireContext(),
                            "Cart statute",
                            "Orders validated !",
                            "KO",
                            null,
                            {_,_->},
                            {_,_->}
                        )
                    }
                    it.addOnFailureListener {
                        binding.progress.visibility = View.GONE
                        showDialog(
                            requireContext(),
                            "Cart statute",
                            "Something went wrong please try again later!",
                            "KO",
                            null,
                            {_,_ -> },
                            {_,_->}
                        )
                    }
                }
            }
        }
    }

    object Constants {
        const val DATABASE_PATH_ORDER = "orders"
        const val DATABASE_PATH_CART = "carts"
    }
}