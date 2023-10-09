package com.ssh.resort

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.ssh.appdataremotedb.HTTPDownload
import com.ssh.appdataremotedb.Utils
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

class UpdateExistingAgent : AppCompatActivity() {

    val TAG = "AgentRegistration"

    var agentName: EditText? = null
    var agentMobile: EditText? = null
    var agentPhonePeNo: EditText? = null
    var agentEmail: EditText? = null

    var id: String? = ""
    var name: String? = ""
    var mobile: String? = ""
    var phonePe: String? = ""
    var email: String? = ""

    var agName: String? = ""
    var agMobile: String? = ""
    var agPhonePe: String? = ""
    var agEmail: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_existing_agent)

        id = intent.getStringExtra("id")
        name = intent.getStringExtra("name")
        mobile = intent.getStringExtra("mobile")
        phonePe = intent.getStringExtra("phonePe")
        email = intent.getStringExtra("email")

        downloadAgentDetails()

        agentName = findViewById(R.id.etUpdateAgentName)
        //agentName!!.setText(name)
        agentMobile = findViewById(R.id.etUpdateAgentMobile)
        //agentMobile!!.setText(mobile)
        agentPhonePeNo = findViewById(R.id.etUpdateAgentPhonePeNum)
        //agentPhonePeNo!!.setText(phonePe)
        agentEmail = findViewById(R.id.etUpdateAgentEmail)
        //agentEmail!!.setText(email)

        var update = findViewById<Button>(R.id.btnUpdate)
        update.setOnClickListener {
            if (agentName!!.text.toString().equals("") || agentMobile!!.text.toString().equals("")
                || agentPhonePeNo!!.text.toString().equals("") || agentEmail!!.text.toString().equals("")) {
                Toast.makeText(this, "Please Enter all the fields", Toast.LENGTH_SHORT).show()
            } else if (agentMobile!!.text.toString().length != 10) {
                agentMobile!!.setError("Enter Valid Mobile Number")
            } else if (!Patterns.EMAIL_ADDRESS.matcher(agentEmail!!.getText().toString()).matches()) {
                agentEmail!!.setError("Enter Valid Email")
            } else if (Utils.checkInternetConnectivity(this) == false) {
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            } else {
                updateAgent()
            }
        }
    }

    //Update Profile Data in Server
    @SuppressLint("LongLogTag")
    fun updateAgent() {
        Log.d(TAG, "updateProfile: ")

        var pd = Dialog(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.progress, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.setCancelable(false)

        class UpdateData : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String {
                var response: String = updateData()
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

                if (result.contains("Updated Successfully")) {
                    downloadAgentDetails()
                    Toast.makeText(this@UpdateExistingAgent, result, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@UpdateExistingAgent, result, Toast.LENGTH_LONG).show()
                }
            }
        }

        var updateData = UpdateData().execute()
    }

    //Update Using HTTP POST Method
    @SuppressLint("LongLogTag")
    fun updateData(): String {

        var response: String = ""

        val url = URL("https://sshsoftwares.in/Resort/UpdateAgent.php")
        Log.d(TAG, "updateData URL: " + url)
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
        dataJson.put("name", agentName!!.text.toString())
        dataJson.put("mobile", agentMobile!!.text.toString())
        dataJson.put("phonePeNum", agentPhonePeNo!!.text.toString())
        dataJson.put("email", agentEmail!!.text.toString())

        Log.d(TAG, "getDataToJson: " + dataJson.toString())
        return dataJson.toString()
    }

    fun downloadAgentDetails() {
        Log.d(TAG, "downloadAgentDetails")

        var pd = Dialog(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.progress, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.setCancelable(false)

        class DownloadAgentDetails : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {
                val profileDetailsUrl = "https://sshsoftwares.in/Resort/GetPerticularAgent.php?mobile=" + mobile

                var httpDownload = HTTPDownload()
                val httpStatus = httpDownload.downloadUrl(profileDetailsUrl)

                if(httpStatus.status != HTTPDownload.STATUS_SUCCESS) {
                    Log.d(TAG, "downloadAgentDetails jsonojb null" + httpStatus.status)
                    return false
                }

                val profileDetails = httpStatus.value!!.getJSONObject("results")
                if (profileDetails == null) {
                    Log.d(TAG, "downloadAgentDetails null")
                    return false
                }

                agName = profileDetails.getString("name")
                agMobile = profileDetails.getString("mobile")
                agPhonePe = profileDetails.getString("phonePe")
                agEmail = profileDetails.getString("email")

                return true
            }

            override fun onPreExecute() {
                super.onPreExecute()
                pd.show()
            }

            @SuppressLint("SetTextI18n")
            override fun onPostExecute(result: Boolean) {
                super.onPostExecute(result)
                Log.d(TAG, "downloadAgentDetails onPostExecute result:" + result)
                pd.cancel()
                if (result == false) {
                    Toast.makeText(this@UpdateExistingAgent, "Data Not Found", Toast.LENGTH_SHORT).show()
                }
                else {
                    agentName!!.setText(agName)
                    agentMobile!!.setText(agMobile)
                    agentPhonePeNo!!.setText(agPhonePe)
                    agentEmail!!.setText(agEmail)
                }
            }
        }
        var result = DownloadAgentDetails().execute()
    }
}