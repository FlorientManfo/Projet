package net.tipam2022.projet

import android.os.Bundle
import net.tipam2022.projet.CartFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import net.tipam2022.projet.R
import net.tipam2022.projet.DetailsFragment
import net.tipam2022.projet.FavoriteFragment
import net.tipam2022.projet.HomeFragment
import net.tipam2022.projet.ProfileFragment
import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.os.Build
import android.view.WindowInsets
import android.view.View.OnTouchListener
import android.view.MotionEvent
import net.tipam2022.projet.SplashActivity
import net.tipam2022.projet.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}