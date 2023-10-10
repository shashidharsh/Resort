package com.ssh.resort

import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ssh.appdataremotedb.HTTPDownload
import com.ssh.resort.data.ExistingAgentListData

class Checkin : AppCompatActivity() {

    val TAG = "Checkin"

    var radioGroupActivities: RadioGroup? = null
    var radioGroupDriver: RadioGroup? = null
    var radioButtonActivities: RadioButton? = null
    var radioButtonDriver: RadioButton? = null

    val agentList: ArrayList<ExistingAgentListData> = ArrayList()
    val agentShowList: ArrayList<String> = ArrayList()
    var currentAgent : String = ""
    var activities :String? = null

    var guestName :EditText? = null
    var packagePerHeadChild :EditText? = null
    var roomNumber :EditText? = null
    var b2bPrice :EditText? = null
    var noOfPerson :EditText? = null
    var noOfChildren :EditText? = null
    var packagePerHeadAddult :EditText? = null
    var tvB2B :TextView? = null
    var tvTAC :TextView? = null
    var tvTotal :TextView? = null
    var b2b :String? = null
    var tac :String? = null
    var total :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin)

        var layoutActivities = findViewById<RelativeLayout>(R.id.layoutActivities)
        var layoutDriver = findViewById<LinearLayout>(R.id.layoutDriver)

        layoutActivities.visibility = View.GONE
        layoutDriver.visibility = View.GONE

        //Get Agent Details
        getAgentDetailsFromServer()

        //access the items of the list
        var activity = resources.getStringArray(R.array.Activities)

        //Set Agent Dropdown Spinner
        var arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, agentShowList)
        val agentDropDownSpinner = findViewById(R.id.coSpinner) as Spinner
        agentDropDownSpinner.adapter = arrayAdapter

        agentDropDownSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //view!!.setBackgroundColor(Color.rgb(30,144,255))
                parent?.getItemAtPosition(position) as String
                currentAgent = agentShowList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        //Set Activities Dropdown Spinner
        val spinner = findViewById<Spinner>(R.id.activitiesSpinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, activity)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    activities = activity[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

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
                layoutDriver.visibility = View.VISIBLE
            }
            else{
                layoutDriver.visibility = View.GONE
            }
        })

        //Initialization
        guestName = findViewById(R.id.etCheckinGuestName)
        packagePerHeadChild = findViewById(R.id.etPackagePerHeadForChild)
        roomNumber = findViewById(R.id.etCheckinRoomNumber)
        b2bPrice = findViewById(R.id.etB2BPrice)
        noOfPerson = findViewById(R.id.etCheckinNoOfPersons)
        noOfChildren = findViewById(R.id.etCheckinNoOfChildrens)
        packagePerHeadAddult = findViewById(R.id.etPackagePerHeadForAdult)
        packagePerHeadAddult = findViewById(R.id.etPackagePerHeadForAdult)
        packagePerHeadAddult = findViewById(R.id.etPackagePerHeadForAdult)
        packagePerHeadAddult = findViewById(R.id.etPackagePerHeadForAdult)
        tvB2B = findViewById(R.id.tvCheckinB2B)
        tvTAC = findViewById(R.id.tvCheckinTAC)
        tvTotal = findViewById(R.id.tvCheckinTotal)

        //Calculate Data
        var calculate = findViewById<Button>(R.id.btnCalculate)
        calculate.setOnClickListener{
            if (guestName!!.text.toString().equals("") || packagePerHeadAddult!!.text.toString().equals("")
                || packagePerHeadChild!!.text.toString().equals("") || noOfPerson!!.text.toString().equals("")
                || noOfChildren!!.text.toString().equals("") || b2bPrice!!.text.toString().equals("")
                || roomNumber!!.text.toString().equals("")) {
                Toast.makeText(this, "Please Enter all the fields", Toast.LENGTH_SHORT).show()
            }
            else {
                //Calculate B2B
                b2b = ((b2bPrice!!.text.toString().toFloat() * noOfPerson!!.text.toString().toFloat()) +
                        (noOfChildren!!.text.toString().toFloat() * packagePerHeadAddult!!.text.toString().toFloat())).toString()
                Log.d(TAG, "b2b: " + b2b)
                tvB2B!!.setText(b2b)

                //Calculate TAC
                tac = ((b2b.toString().toFloat() * noOfPerson!!.text.toString().toFloat()) - packagePerHeadAddult!!.text.toString().toFloat()).toString()
                Log.d(TAG, "tac: " + tac)
                tvTAC!!.setText(tac)

                //Calculate Total
                total = ((noOfPerson!!.text.toString().toFloat() * packagePerHeadAddult!!.text.toString().toFloat()) +
                        (noOfChildren!!.text.toString().toFloat() * packagePerHeadChild!!.text.toString().toFloat())).toString()
                Log.d(TAG, "total: " + total)
                tvTotal!!.setText(total)
            }
        }

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
            else if (guestName!!.text.toString().equals("") || packagePerHeadAddult!!.text.toString().equals("")
                || packagePerHeadChild!!.text.toString().equals("") || noOfPerson!!.text.toString().equals("")
                || noOfChildren!!.text.toString().equals("") || b2bPrice!!.text.toString().equals("")
                || roomNumber!!.text.toString().equals("")) {
                Toast.makeText(this, "Please Enter all the fields", Toast.LENGTH_SHORT).show()
            }
            else if (tvB2B!!.text.toString().equals("") || tvTAC!!.text.toString().equals("") || tvTotal!!.text.toString().equals("")) {
                Toast.makeText(this, "Please Calculate Total", Toast.LENGTH_SHORT).show()
            }
            else {
                radioButtonActivities = radioGroupActivities!!.findViewById(selectedActivitiesId)
                radioButtonDriver = radioGroupDriver!!.findViewById(selectedDriverId)
                Log.d(TAG, "RadioGroupActivity: " + radioButtonActivities!!.text)
                Log.d(TAG, "RadioGroupDriver: " + radioButtonDriver!!.text)
            }
        })
    }

    //Download Agent Details from Server
    fun getAgentDetailsFromServer() {
        Log.d(TAG, "getAgentDetailsFromServer")

        var pd = Dialog(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.progress, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.setCancelable(false)

        class AgentDetails : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {

                val agentDetailsUrl = "https://sshsoftwares.in/Resort/GetAgents.php"

                var httpDownload = HTTPDownload()
                val httpStatus = httpDownload.downloadUrl(agentDetailsUrl)

                if(httpStatus.status != HTTPDownload.STATUS_SUCCESS) {
                    Log.d(TAG, "getAgentDetailsFromServer jsonojb null" + httpStatus.status)
                    return false
                }

                val agentResult = httpStatus.value!!.optJSONArray("results")
                if (agentResult == null){
                    return false
                }
                agentList.clear()

                for (i in 0 until agentResult.length()) {
                    val json_data = agentResult.getJSONObject(i)
                    val agentListData = ExistingAgentListData(
                        json_data.getString("id"),
                        json_data.getString("name"),
                        json_data.getString("mobile"),
                        json_data.getString("phonePe"),
                        json_data.getString("email"))
                    agentList.add(agentListData)
                    Log.d(TAG, "getAgentDetailsFromServer agentData: " + agentListData)
                }
                return true
            }

            override fun onPreExecute() {
                super.onPreExecute()
                pd.show()
            }

            override fun onPostExecute(result: Boolean) {
                super.onPostExecute(result)
                Log.d(TAG, "getAgentDetailsFromServer onPostExecute result:" + result)
                pd.cancel()
                if (result == false) {
                    Toast.makeText(applicationContext, "$result", Toast.LENGTH_SHORT).show()
                }
                else {
                    agentShowList.clear()
                    for (i in 0 until agentList.size) {
                        agentShowList.add(agentList.get(i).name)
                    }

                    var arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, agentShowList)
                    val coDropDownSpinner = findViewById(R.id.coSpinner) as Spinner
                    coDropDownSpinner.adapter = arrayAdapter
                }
            }
        }
        var result = AgentDetails().execute()
    }
}