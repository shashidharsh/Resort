package com.ssh.resort

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class TacAgentTransactions : AppCompatActivity() {

    private val TAG = "TacAgentTransactions"

    var tvCurrentDate: TextView? = null

    var agentID : String? = ""
    var agentName : String? = ""
    var agentMobile : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tac_agent_transactions)

        agentID = intent.getStringExtra("AgentID")
        agentName = intent.getStringExtra("AgentName")
        agentMobile = intent.getStringExtra("AgentMobile")

        //Set From Date and End Date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        tvCurrentDate = findViewById(R.id.tacAgentTransactionDate) as TextView

        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val currentDate: String = simpleDateFormat.format(Date())
        tvCurrentDate!!.text = currentDate

        //Button Start Date
        tvCurrentDate!!.setOnClickListener {
            val datePickerDialog =
                DatePickerDialog(this@TacAgentTransactions, DatePickerDialog.OnDateSetListener
                { view, year, monthOfYear, dayOfMonth ->
                    tvCurrentDate!!.setText("" + String.format("%02d", dayOfMonth) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year)
                }, year, month, day)
            datePickerDialog.show()
        }
    }
}