package com.ssh.resort

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class AdminAgentRegistration : AppCompatActivity() {

    val TAG = "AdminAgentRegistration"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_agent_registration)

        var cvNewAgent = findViewById<CardView>(R.id.cvNewAgent)
        cvNewAgent.setOnClickListener{
            val intent = Intent(this, NewAgentRegistration::class.java)
            startActivity(intent)
        }

        var cvExistingAgent = findViewById<CardView>(R.id.cvExistingAgent)
        cvExistingAgent.setOnClickListener{
            val intent = Intent(this, ExistingAgent::class.java)
            startActivity(intent)
        }
    }
}