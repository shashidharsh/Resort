package com.ssh.resort

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.ssh.appdataremotedb.Utils

class AdminHomePage : AppCompatActivity() {

    val TAG = "AdminHomePage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_home_page)

        // assigning ID of the toolbar to a variable
        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        // using toolbar as ActionBar
        setSupportActionBar(toolbar)

        var addRooms = findViewById<ImageView>(R.id.ivAddRooms)
        addRooms.setOnClickListener{
            val intent = Intent(this, AddRooms::class.java)
            startActivity(intent)
        }

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

        var cvReports = findViewById<CardView>(R.id.cvReports)
        cvReports.setOnClickListener{
            if (Utils.checkInternetConnectivity(this) == false){
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            }
            else {
                val intent = Intent(this, Reports::class.java)
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

        var cvExpense = findViewById<CardView>(R.id.cvExpense)
        cvExpense.setOnClickListener{
            if (Utils.checkInternetConnectivity(this) == false){
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            }
            else {
                val intent = Intent(this, Expense::class.java)
                startActivity(intent)
            }
        }
    }
}