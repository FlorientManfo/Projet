package net.tipam2022.projet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.tipam2022.projet.databinding.ActivitySplashBinding

class SplashActivity: AppCompatActivity(){
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}
