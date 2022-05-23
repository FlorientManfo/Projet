package net.tipam2022.projet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import net.tipam2022.projet.databinding.ActivitySplashBinding

class SplashActivity: AppCompatActivity(){
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth!!.currentUser


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            getShearedPreferences()
            if(currentUser != null){
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            } else{
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }
        }, 3000)

        setContentView(binding.root)
    }

    private fun getShearedPreferences(){
        var sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE)
        UserName = sharedPreferences.getString("userName", null)
        PhoneNumber = sharedPreferences.getString("phoneNumber", null)
    }

}
