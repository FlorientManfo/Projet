package net.tipam2022.projet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.tipam2022.projet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var containre: FrameLayout
    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        containre = binding.mainContainer
        bottomNav = binding.bottomNavigation as BottomNavigationView
        frManager = supportFragmentManager

        setContentView(binding.root)

        loadFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener{ item ->
            when(item.itemId) {
                R.id.home-> {
                    loadFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.favorite -> {
                    loadFragment(FavoriteFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.cart -> {
                    loadFragment(CartFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                    loadFragment(ProfileFragment())
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }
    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainContainer,fragment, fragment::class.java.simpleName)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}