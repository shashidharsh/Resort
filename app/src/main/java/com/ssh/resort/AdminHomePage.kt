package com.ssh.resort

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class AdminHomePage : AppCompatActivity() {

    val TAG = "AdminHomePage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_home_page)

        var cvCheckin = findViewById<CardView>(R.id.cvCheckin)
        cvCheckin.setOnClickListener{
            val intent = Intent(this, Checkin::class.java)
            startActivity(intent)
        }

        var cvAgentRegistration = findViewById<CardView>(R.id.cvAgentRegistration)
        cvAgentRegistration.setOnClickListener{
            val intent = Intent(this, AdminAgentRegistration::class.java)
            startActivity(intent)
        }
    }
}