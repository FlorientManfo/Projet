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
import net.tipam2022.projet.SplashActivity
import net.tipam2022.projet.databinding.ActivityMainBinding
import net.tipam2022.projet.databinding.FragmentCartBinding

/**
 * A simple [Fragment] subclass.
 * Use the [CartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CartFragment : Fragment() {
    lateinit var binding: FragmentCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)


        return binding.root
    }
}