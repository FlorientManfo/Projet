package net.tipam2022.projet

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import net.tipam2022.projet.databinding.ActivitySignInBinding
import net.tipam2022.projet.entities.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit


class SignInActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding
    lateinit var next: Button
    lateinit var back: ImageButton

    // variable for FirebaseAuth class
    private lateinit var resendToken: ForceResendingToken
    private lateinit var callbacks: OnVerificationStateChangedCallbacks
    private var checkIng: Boolean = false
    private var storedVerificationId: String? = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        next = binding.next
        back = binding.back

        storageReference = FirebaseStorage.getInstance().getReference(Constants.STORAGE_PATH_UPLOADS)
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS)

        binding.phoneNumber.doOnTextChanged { text, start, before, count ->
            var currentText = binding.phoneNumber.text.toString().trim().toLong()
            if(!currentText?.toString().matches(pattern)!!){
                binding.phoneNumber.setTextColor(getColor(R.color.input_error))
            }else{
                binding.phoneNumber.setTextColor(getColor(R.color.white))
                PhoneNumber = currentText.toLong()
            }
            if(isRegistered(currentText)){
                binding.userName.visibility = View.GONE
            }else{
                binding.userName.visibility = View.VISIBLE
            }
        }

        binding.userName.doOnTextChanged { text, start, before, count ->
            UserName = text.toString()
        }

        next.setOnClickListener {
            if(checkIng)
                authenticate()
            else{
                getOtp()
            }
        }

        back.setOnClickListener {
            back.visibility = View.GONE
            binding.verification.visibility = View.GONE
            binding.information.visibility = View.VISIBLE
            next.text = "Next"
            checkIng = false
        }

        callbacks = object : OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                binding.progress.visibility = View.GONE
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.progress.visibility = View.GONE
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: ForceResendingToken
            ) {
                binding.progress.visibility = View.GONE
                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                back.visibility = View.VISIBLE
                binding.information.visibility = View.GONE
                binding.verification.visibility = View.VISIBLE
                binding.otp.text.clear()
                next.text = "Finish"
                checkIng = true
            }

        }

        auth = FirebaseAuth.getInstance()

        setContentView(binding.root)
    }

    private fun getOtp(){

        binding.progress.visibility = View.VISIBLE
        var phone = binding?.phoneNumber.text.toString()
        var userName = binding?.userName.text.toString()
        if(!phone.isNullOrEmpty() && phone.matches(pattern)){
            if(UserName == null)
                Toast.makeText(this, "You should provide a user name", Toast.LENGTH_SHORT).show()
            else{
                PhoneNumber = phone.toLong()
                phone = "+237$phone"
                sendVerificationCode(phone)
            }
        }
        else{Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show()}
    }

    private fun authenticate(){

        binding.progress.visibility = View.VISIBLE
        val otp = binding.otp.text.toString().trim()
        if(otp.isNotEmpty()){
            val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                storedVerificationId.toString(), otp)
            signInWithPhoneAuthCredential(credential)
        }
        else{
            Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationCode(phoneNumber: String){
        val options = PhoneAuthOptions.newBuilder(auth!!)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth?.signInWithCredential(credential)
        ?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                savePhoneNumber()
                startActivity(Intent(applicationContext, WelcomeActivity::class.java))
                addDataToFirebase()
                finish()
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addDataToFirebase() {

        databaseReference!!.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                var user = User(
                    PhoneNumber!!,
                    UserName!!,
                    null,
                    null,
                    LocalDateTime.now().format(DateTimeFormatter.ISO_DATE),
                    null
                )
                databaseReference!!.child(PhoneNumber?.toString()).setValue(user)
                println("--------------->Save done")
            }

            override fun onCancelled(error: DatabaseError) {
                println("--------------->$error")
            }
        })
    }

    private fun isRegistered(currentPhone: Long): Boolean{
        var sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE)
        PhoneNumber = sharedPreferences.getLong("phoneNumber", 0)
        UserName = sharedPreferences.getString("userName", null)
        println("---------------------->$UserName")
        println("---------------->$PhoneNumber")
        println("------------->$currentPhone")
        return PhoneNumber == currentPhone
    }

    private fun savePhoneNumber(){
        val sharedPref = getSharedPreferences("profile", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putLong("phoneNumber", PhoneNumber)
        editor.putString("userName", UserName)
        editor.commit()
    }


    object Constants {
        const val STORAGE_PATH_UPLOADS = "profileImages/"
        const val DATABASE_PATH_UPLOADS = "users"
    }
}