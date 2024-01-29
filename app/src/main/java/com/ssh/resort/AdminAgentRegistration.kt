package com.ssh.resort

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.ssh.appdataremotedb.Utils

class AdminAgentRegistration : AppCompatActivity() {

    val TAG = "AdminAgentRegistration"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_agent_registration)

        var newAgent = findViewById<Button>(R.id.newAgent)
        newAgent.setOnClickListener{
            if (Utils.checkInternetConnectivity(this) == false){
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            }
            else {
                val intent = Intent(this, NewAgentRegistration::class.java)
                startActivity(intent)
            }
        }

        var existingAgent = findViewById<Button>(R.id.existingAgent)
        existingAgent.setOnClickListener{
            if (Utils.checkInternetConnectivity(this) == false){
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            }
            else {
                val intent = Intent(this, ExistingAgent::class.java)
                startActivity(intent)
            }
        }
    }
}