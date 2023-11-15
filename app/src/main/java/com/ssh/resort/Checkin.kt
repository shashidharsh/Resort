package com.ssh.resort

import android.annotation.SuppressLint
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
import android.widget.CheckBox
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
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

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
    var driverCost :EditText? = null
    var etNoOfPersonForActivity :EditText? = null
    var tvB2B :TextView? = null
    var tvActivityPrice :TextView? = null
    var tvTAC :TextView? = null
    var tvTotal :TextView? = null
    var b2b :String? = null
    var activityPrice :String? = null
    var tac :String? = null
    var total :String? = null

    private var checkBoxCash: CheckBox? = null
    private var checkBoxUPI: CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin)

        var layoutActivities = findViewById<RelativeLayout>(R.id.layoutActivities)
        var noOfPersonActivity = findViewById<LinearLayout>(R.id.noOfPersonsForActivity)
        var layoutDriver = findViewById<LinearLayout>(R.id.layoutDriver)
        var layoutPayment = findViewById<LinearLayout>(R.id.layoutPayment)

        layoutActivities.visibility = View.GONE
        layoutDriver.visibility = View.GONE
        noOfPersonActivity.visibility = View.GONE
        layoutPayment.visibility = View.GONE

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
                noOfPersonActivity.visibility = View.VISIBLE
            }
            else{
                layoutActivities.visibility = View.GONE
                noOfPersonActivity.visibility = View.GONE
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
        driverCost = findViewById(R.id.etDriverCost)
        tvB2B = findViewById(R.id.tvCheckinB2B)
        tvActivityPrice = findViewById(R.id.tvCheckinActivityPrice)
        tvTAC = findViewById(R.id.tvCheckinTAC)
        tvTotal = findViewById(R.id.tvCheckinTotal)
        etNoOfPersonForActivity = findViewById(R.id.etNoOfPersonsActivities)

        //Check Box Initialization
        checkBoxCash = findViewById(R.id.checkinCash) as CheckBox
        checkBoxUPI = findViewById(R.id.checkinUPI) as CheckBox

        if(checkBoxCash!!.isChecked() == true){
            layoutPayment.visibility = View.VISIBLE
        }

        //Calculate Data
        var calculate = findViewById<Button>(R.id.btnCalculate)
        calculate.setOnClickListener{
            val selectedDriverId: Int = radioGroupDriver!!.getCheckedRadioButtonId()
            radioButtonDriver = radioGroupDriver!!.findViewById(selectedDriverId)

            val selectedActivitiesId: Int = radioGroupActivities!!.getCheckedRadioButtonId()
            radioButtonActivities = radioGroupActivities!!.findViewById(selectedActivitiesId)

            if (guestName!!.text.toString().equals("") || packagePerHeadAddult!!.text.toString().equals("")
                || packagePerHeadChild!!.text.toString().equals("") || noOfPerson!!.text.toString().equals("")
                || noOfChildren!!.text.toString().equals("") || b2bPrice!!.text.toString().equals("")
                || roomNumber!!.text.toString().equals("")) {
                Toast.makeText(this, "Please Enter all the fields", Toast.LENGTH_SHORT).show()
            }
            else if (selectedActivitiesId == -1) {
                Toast.makeText(this@Checkin, "No Activities has been selected", Toast.LENGTH_SHORT).show()
            }
            else if (selectedDriverId == -1){
                Toast.makeText(this@Checkin, "No Driver has been selected", Toast.LENGTH_SHORT).show()
            }
            else {
                //Driver Radio Button
                if (radioButtonDriver!!.text.equals("Yes")) {
                    if (driverCost!!.text.toString().equals("")) {
                        Toast.makeText(this, "Please Enter Driver Cost", Toast.LENGTH_SHORT).show()
                    } else {
                        //Calculate B2B
                        b2b = (((b2bPrice!!.text.toString().toFloat() * noOfPerson!!.text.toString().toFloat()) +
                                (noOfChildren!!.text.toString().toFloat() * packagePerHeadChild!!.text.toString().toFloat())) + (driverCost!!.text.toString().toFloat())).toString()
                        Log.d(TAG, "b2b: " + b2b)
                        tvB2B!!.setText(b2b)
                    }
                } else {
                    //Calculate B2B
                    b2b = ((b2bPrice!!.text.toString().toFloat() * noOfPerson!!.text.toString().toFloat()) +
                            (noOfChildren!!.text.toString().toFloat() * packagePerHeadChild!!.text.toString().toFloat())).toString()
                    Log.d(TAG, "b2b: " + b2b)
                    tvB2B!!.setText(b2b)
                }

                //Activities Radio Button
                if (radioButtonActivities!!.text.contains("Yes")) {

                    //Get Only Numbers
                    val str = activities
                    val amountOnly = str!!.replace("[^0-9]".toRegex(), "")

                    //Calculate Activity Price
                    if (amountOnly.toInt() == 150) {
                        activityPrice = ((etNoOfPersonForActivity!!.text.toString().toFloat() * 150)).toString()
                        Log.d(TAG, "activityPrice: " + activityPrice)
                        tvActivityPrice!!.setText(activityPrice)
                    } else if (amountOnly.toInt() == 250) {
                        activityPrice = ((etNoOfPersonForActivity!!.text.toString().toFloat() * 250)).toString()
                        Log.d(TAG, "activityPrice: " + activityPrice)
                        tvActivityPrice!!.setText(activityPrice)
                    } else if (amountOnly.toInt() == 400) {
                        activityPrice = ((etNoOfPersonForActivity!!.text.toString().toFloat() * 400)).toString()
                        Log.d(TAG, "activityPrice: " + activityPrice)
                        tvActivityPrice!!.setText(activityPrice)
                    } else if (amountOnly.toInt() == 500) {
                        activityPrice = ((etNoOfPersonForActivity!!.text.toString().toFloat() * 500)).toString()
                        Log.d(TAG, "activityPrice: " + activityPrice)
                        tvActivityPrice!!.setText(activityPrice)
                    } else if (amountOnly.toInt() == 550) {
                        activityPrice = ((etNoOfPersonForActivity!!.text.toString().toFloat() * 550)).toString()
                        Log.d(TAG, "activityPrice: " + activityPrice)
                        tvActivityPrice!!.setText(activityPrice)
                    } else {
                        activityPrice = ((etNoOfPersonForActivity!!.text.toString().toFloat() * 1050)).toString()
                        Log.d(TAG, "activityPrice: " + activityPrice)
                        tvActivityPrice!!.setText(activityPrice)
                    }
                } else {
                    tvActivityPrice!!.setText("0.0")
                }

                //Calculate TAC
                tac = ((packagePerHeadAddult!!.text.toString().toFloat() - b2bPrice!!.text.toString().toFloat()) * noOfPerson!!.text.toString().toFloat()).toString()
                Log.d(TAG, "tac: " + tac)
                tvTAC!!.setText(tac)

                //Calculate Total
                total = ((tvB2B!!.text.toString().toFloat() + tvActivityPrice!!.text.toString().toFloat()) +
                        (tvTAC!!.text.toString().toFloat())).toString()
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
                getDataToJson()
                //checkin()
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

    //Save Checkin Data in Server
    @SuppressLint("LongLogTag")
    fun checkin() {
        Log.d(TAG, "checkin: ")

        var pd = Dialog(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.progress, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.setCancelable(false)

        class CheckinData : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String {
                var response: String = getCheckinData()
                Log.d(TAG, "response: " + response)
                return response
            }

            override fun onPreExecute() {
                super.onPreExecute()
                pd.show()
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                Log.d(TAG, "onPostExecute: result " + result)
                pd.cancel()
                Toast.makeText(this@Checkin, result, Toast.LENGTH_LONG).show()
                guestName!!.setText("")
                noOfPerson!!.setText("")
                noOfChildren!!.setText("")
                packagePerHeadAddult!!.setText("")
                packagePerHeadChild!!.setText("")
                b2bPrice!!.setText("")
                roomNumber!!.setText("")
                driverCost!!.setText("")
                tvB2B!!.setText("")
                tvTAC!!.setText("")
                tvTotal!!.setText("")

            }
        }
        var saveData = CheckinData().execute()
    }

    //Export Checkin Data Using HTTP POST Method
    @SuppressLint("LongLogTag")
    fun getCheckinData(): String {

        var response: String = ""

        val url = URL("https://sshsoftwares.in/Resort/.php")
        Log.d(TAG, "getCheckinData URL: " + url)
        var client: HttpURLConnection? = null
        try {
            client = url.openConnection() as HttpURLConnection
            client.setRequestMethod("POST")
            client.setRequestProperty("Content-Type", "application/json")
            client.setDoInput(true)
            client.setDoOutput(true)
            val os: OutputStream = client.getOutputStream()
            val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
            writer.write(getDataToJson())
            writer.flush()
            writer.close()
            os.close()
            val responseCode: Int = client.getResponseCode()
            Log.d(TAG, "responseCode: " + responseCode)

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                val br = BufferedReader(InputStreamReader(client.getInputStream()))
                var line: String? = br.readLine()
                while (line != null) {
                    response += line
                    line = br.readLine()
                    Log.d(TAG, "if true response: " + response)
                }
            } else {
                response = ""
                Log.d(TAG, "response empty: " + response)
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } finally {
            if (client != null) // Make sure the connection is not null.
                client.disconnect()
        }
        return response
    }

    fun getDataToJson(): String {
        var dataJson: JSONObject = JSONObject()
        dataJson.put("GuestName", guestName!!.text.toString())
        dataJson.put("NoOfPersons", noOfPerson!!.text.toString())
        dataJson.put("NoOfChildrens", noOfChildren!!.text.toString())
        dataJson.put("PackageForAdult", packagePerHeadAddult!!.text.toString())
        dataJson.put("PackageForChild", packagePerHeadChild!!.text.toString())
        dataJson.put("SelectedCo", currentAgent)
        dataJson.put("B2BPrice", b2bPrice!!.text.toString())
        dataJson.put("Activities", radioButtonActivities!!.text)

        if (radioButtonActivities!!.text.equals("Yes")) {
            dataJson.put("SelectedActivity", activities)
        }
        else{
            dataJson.put("SelectedActivity", "Not Selected")
        }

        dataJson.put("RoomNumber", roomNumber!!.text.toString())
        dataJson.put("Driver", radioButtonDriver!!.text)

        if (radioButtonDriver!!.text.equals("Yes")) {
            dataJson.put("DriverCost", driverCost!!.text.toString())
        }
        else{
            dataJson.put("DriverCost", "0")
        }

        dataJson.put("B2B", tvB2B!!.text.toString())
        dataJson.put("TAC", tvTAC!!.text.toString())
        dataJson.put("Total", tvTotal!!.text.toString())

        Log.d(TAG, "getDataToJson: " + dataJson.toString())
        return dataJson.toString()
    }
}