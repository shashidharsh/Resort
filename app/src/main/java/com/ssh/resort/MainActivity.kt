package com.ssh.resort

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var cvAdmin = findViewById<CardView>(R.id.cvAdmin)
        cvAdmin.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            intent.putExtra("Login", "Admin")
            startActivity(intent)
            finish()
        }

        var cvManager = findViewById<CardView>(R.id.cvManager)
        cvManager.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            intent.putExtra("Login", "Manager")
            startActivity(intent)
            finish()
        }
    }
}