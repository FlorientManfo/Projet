package net.tipam2022.projet

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import net.tipam2022.projet.DetailsActivity.Constants.DATABASE_PATH_MENU
import net.tipam2022.projet.DetailsActivity.Constants.DATABASE_PATH_OPINION
import net.tipam2022.projet.databinding.ActivityDetailsBinding
import net.tipam2022.projet.adapters.OtherMenuAdapter
import net.tipam2022.projet.entities.Menu
import net.tipam2022.projet.entities.Opinion
import net.tipam2022.projet.entities.Order
import net.tipam2022.projet.entities.OrderStatus
import java.util.*
import kotlin.collections.ArrayList

class DetailsActivity : AppCompatActivity() {

    var menu: Menu? = null
    lateinit var binding: ActivityDetailsBinding
    lateinit var context: Context

    private var menuRecyclerView: RecyclerView? = null
    private var menus: ArrayList<Menu>? = null
    private var menuAdapter: OtherMenuAdapter? = null
    private var quantity: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        binding.addToCartButton.setOnClickListener {
            specifyQuantity()
        }
        binding.topBar.setNavigationOnClickListener {
            finish()
        }

        setContentView(binding.root)
        context = this

        var bundle: Bundle = intent.extras!!
        menu = bundle.getSerializable("selectedMenu") as Menu
        displayDetails(menu)

        menuRecyclerView = binding.otherMenuList
        menus = arrayListOf()
        menuAdapter = OtherMenuAdapter(context, menus!!) { menuClickLister(it) }
        menuRecyclerView?.adapter = menuAdapter
        getMenus(menu?.categoryId)
    }

    private fun menuClickLister(position: Int) {
        menu = menus?.get(position)
        displayDetails(menu)
    }

    private fun displayDetails(menu: Menu?) {
        binding.progress.visibility = View.VISIBLE
        Glide.with(context).load(menu?.menuImage.toBitmap()).into(binding.selectedSubMenuImage)
        binding.menuName.text = menu?.menuName
        binding.price.text = menu?.menuPrice.toString() + " XAF"
        binding.menuDescription.text = menu?.menuDescription
        getOpinion(menu, binding.topBar.menu.getItem(0))
        getRate(menu)
        binding.progress.visibility = View.GONE
    }

    private fun getMenus(categoryId: Int?) {
        binding.progress.visibility = View.VISIBLE
        databaseReference =
            FirebaseDatabase.getInstance().getReference(HomeFragment.Constants.DATABASE_PATH_MENU)

        menus?.clear()
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progress.visibility = View.GONE
                for (postSnapshot in snapshot.children) {
                    val menu: Menu? = postSnapshot.getValue(Menu::class.java)
                    if (menu?.categoryId == categoryId)
                        menus?.add(menu!!)
                }
                menuAdapter?.notifyDataSetChanged()
                menuRecyclerView?.adapter =
                    OtherMenuAdapter(context, menus!!) { menuClickLister(it) }
                println("--------------> ${menus?.size}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                binding.progress.visibility = View.GONE
            }
        })
    }

    private fun specifyQuantity() {

        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.quantity_layout, null)

        val popupWindow = PopupWindow(
            view,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 10.0F
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn

            val slideOut = Slide()
            slideOut.slideEdge = Gravity.RIGHT
            popupWindow.exitTransition = slideOut

        }

        // Get the widgets reference from custom view
        var inputQuantity: EditText? = view.findViewById(R.id.quantity)
        inputQuantity?.setText("0")
        quantity = inputQuantity?.text.toString().toInt()
        var upButton: ImageButton? = view.findViewById(R.id.up)
        var downButton: ImageButton? = view.findViewById(R.id.down)

        var cancel: Button? = view.findViewById(R.id.cancel)
        var save: Button? = view.findViewById(R.id.save)

        upButton?.setOnClickListener {
            if (inputQuantity?.text.toString().toInt() >= 0) {
                quantity += 1
                inputQuantity?.setText(quantity.toString())
            }
        }
        downButton?.setOnClickListener {
            if (inputQuantity?.text.toString().toInt() > 0) {
                quantity -= 1
                inputQuantity?.setText(quantity.toString())
            }
        }
        cancel?.setOnClickListener { popupWindow.dismiss() }
        save?.setOnClickListener {
            addToCart()
        }

        popupWindow.setOnDismissListener {
        }


        TransitionManager.beginDelayedTransition(binding.rootLayout)
        popupWindow.showAtLocation(
            binding.rootLayout,
            Gravity.CENTER,
            0,
            0
        )
    }


    private fun addToCart(){
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_MENU)

        if(quantity > 0){
            var orderId = databaseReference?.push()?.key
            var order = Order(
                orderId = orderId!!,
                userPhoneNumber = PhoneNumber,
                menuId = menu?.menuId!!,
                categoryId = menu?.categoryId!!,
                quantity = quantity,
                price = menu?.menuPrice!! * quantity,
                date = Date().toString(),
                statute = OrderStatus.NotValidated
            )
            databaseReference?.child(orderId)?.setValue(order)?.addOnCompleteListener {
                binding.progress.visibility = View.VISIBLE

                it.addOnSuccessListener {
                    binding.progress.visibility = View.GONE
                    showDialog(this, "Order statute",
                        "Your order has been added in your cart!",
                        "OK",null,
                        {_,_ ->
                            binding.progress.visibility = View.GONE
                            finish()},
                        {dinterface, it ->})
                }
                it.addOnFailureListener {
                    binding.progress.visibility = View.GONE
                    showDialog(this, "Order statute",
                        "Something went wrong try again late please!",
                        "OK",null,
                        {_,_ ->
                            binding.progress.visibility = View.GONE },
                        {dinterface, it ->})
                }
            }
        }else{
            showDialog(this, "Order statute",
                "Quantity most be more than 0!",
                "OK",null,
                {_,_ ->
                    binding.progress.visibility = View.GONE},
                {dinterface, it ->})
        }
    }

    private fun getOpinion(menu: Menu?, item: MenuItem?){
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_OPINION)

        var opinion: Opinion? = null
        var snapshot = databaseReference?.child("${PhoneNumber}${menu?.menuId}${menu?.categoryId}")?.get()
        snapshot?.addOnSuccessListener {
            opinion = it.getValue(Opinion::class.java)
            if(opinion?.status == true)
                item?.icon?.setTint((resources.getColor(R.color.input_border)))
            else
                item?.icon?.setTint((resources.getColor(R.color.input_background)))
        }
    }

    private fun getRate(menu: Menu?){
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_OPINION)

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
                var finalNote: Float = 0f
                if(note != 0){
                    finalNote = note%5f
                    if(finalNote == 0f)
                        finalNote = 5f
                }
                binding.rat.text = finalNote.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    object Constants {
        const val DATABASE_PATH_MENU = "orders"
        const val DATABASE_PATH_OPINION = "opinions"
    }
}