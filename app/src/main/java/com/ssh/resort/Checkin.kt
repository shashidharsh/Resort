package com.ssh.resort

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Checkin : AppCompatActivity() {

    val TAG = "Checkin"

    var radioGroupActivities: RadioGroup? = null
    var radioGroupDriver: RadioGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin)

        var layoutActivities = findViewById<RelativeLayout>(R.id.layoutActivities)
        var driverCost = findViewById<EditText>(R.id.etDriverCost)

        layoutActivities.visibility = View.GONE
        driverCost.visibility = View.GONE

        //radio Group Activities
        radioGroupActivities = findViewById(R.id.radioGroupActivities)

        // Uncheck or reset the radio buttons initially
        radioGroupActivities!!.clearCheck()

        // Add the Listener to the RadioGroup Activities
        radioGroupActivities!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val radioButtonActivities = group.findViewById<View>(checkedId) as RadioButton
            if (radioButtonActivities.text.contains("Yes")){
                layoutActivities.visibility = View.VISIBLE
            }
            else{
                layoutActivities.visibility = View.GONE
            }
        })

        //radio Group Driver
        radioGroupDriver = findViewById(R.id.radioGroupDriver)

        // Uncheck or reset the radio buttons initially
        radioGroupDriver!!.clearCheck()

        // Add the Listener to the RadioGroup Activities
        radioGroupDriver!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val radioButtonDriver = group.findViewById<View>(checkedId) as RadioButton
            if (radioButtonDriver.text.contains("Yes")){
                driverCost.visibility = View.VISIBLE
            }
            else{
                driverCost.visibility = View.GONE
            }
        })

        // Add the Listener to the Submit Button
        var submit = findViewById<Button>(R.id.btnSubmit)
        submit.setOnClickListener(View.OnClickListener {
            val selectedActivitiesId: Int = radioGroupActivities!!.getCheckedRadioButtonId()
            val selectedDriverId: Int = radioGroupDriver!!.getCheckedRadioButtonId()
            if (selectedActivitiesId == -1) {
                Toast.makeText(this@Checkin, "No Activities has been selected", Toast.LENGTH_SHORT).show()
            }
            else if (selectedDriverId == -1){
                Toast.makeText(this@Checkin, "No Driver has been selected", Toast.LENGTH_SHORT).show()
            }
            else {
                val radioButtonActivities = radioGroupActivities!!.findViewById(selectedActivitiesId) as RadioButton
                val radioButtonDriver = radioGroupDriver!!.findViewById(selectedDriverId) as RadioButton
                Log.d(TAG, "RadioGroupActivity: " + radioButtonActivities.text)
                Log.d(TAG, "RadioGroupDriver: " + radioButtonDriver.text)
                //Toast.makeText(this@Checkin, radioButton.text, Toast.LENGTH_SHORT).show()
            }
        })
    }
}