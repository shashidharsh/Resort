package com.ssh.resort

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.ssh.appdataremotedb.Utils

class AdminHomePage : AppCompatActivity() {

    val TAG = "AdminHomePage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_home_page)

        var cvCheckin = findViewById<CardView>(R.id.cvCheckin)
        cvCheckin.setOnClickListener{
            if (Utils.checkInternetConnectivity(this) == false){
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            }
            else {
                val intent = Intent(this, Checkin::class.java)
                startActivity(intent)
            }
        }

        var cvAgentRegistration = findViewById<CardView>(R.id.cvAgentRegistration)
        cvAgentRegistration.setOnClickListener{
            if (Utils.checkInternetConnectivity(this) == false){
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            }
            else {
                val intent = Intent(this, AgentRegistration::class.java)
                startActivity(intent)
            }
        }

        var cvTransaction = findViewById<CardView>(R.id.cvTransaction)
        cvTransaction.setOnClickListener{
            if (Utils.checkInternetConnectivity(this) == false){
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            }
            else {
                val intent = Intent(this, Transactions::class.java)
                startActivity(intent)
            }
        }

        var cvTAC = findViewById<CardView>(R.id.cvTAC)
        cvTAC.setOnClickListener{
            if (Utils.checkInternetConnectivity(this) == false){
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            }
            else {
                val intent = Intent(this, TAC::class.java)
                startActivity(intent)
            }
        }
    }
}