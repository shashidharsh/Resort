package com.ssh.resort

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Disable Dark Mode or Night Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        var cvAdmin = findViewById<CardView>(R.id.cvAdmin)
        cvAdmin.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            intent.putExtra("Login", "Admin")
            startActivity(intent)
        }

        var cvManager = findViewById<CardView>(R.id.cvManager)
        cvManager.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            intent.putExtra("Login", "Manager")
            startActivity(intent)
        }
    }
}