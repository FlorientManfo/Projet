package net.tipam2022.projet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import net.tipam2022.projet.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityWelcomeBinding
    private val messageList = arrayListOf(
        Message("Simplicity", "Order food have never been so easier !"),
        Message("Flexibility", "No time prepare your cart and order when you are available"),
        Message("Friendly", "Access to friendly food when and where you want !")
    )
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)

        val messageTextView = binding.message
        val titleTextView = binding.messageTitle

        messageTextView.setFactory {
            val textView = TextView(this@WelcomeActivity)
            textView.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            textView.textSize = 32f
            textView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            textView.setTextColor(Color.WHITE)
            textView
        }
        messageTextView.setText(messageList[0].message)

        titleTextView.setFactory {
            val textView = TextView(this@WelcomeActivity)
            textView.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            textView.textSize = 32f
            textView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            textView.setTextColor(resources.getColor(R.color.input_border))
            textView
        }
        titleTextView.setText(messageList[0].title)

        binding.previous.setOnClickListener {
            index = if(index -1>=0) index -1 else 0
            titleTextView.setText(messageList[index].title)
            messageTextView.setText(messageList[index].message)
        }

        binding.next.setOnClickListener {
            index = if(index + 1 < messageList.size) index + 1 else 0
            titleTextView.setText(messageList[index].title)
            messageTextView.setText(messageList[index].message)
        }

        binding.skip.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setContentView(binding.root)
    }

    inner class Message(var title: String, var message: String){
    }
}