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

class NewAgentRegistration : AppCompatActivity() {

    val TAG = "AgentRegistration"

    var agentName: EditText? = null
    var agentMobile: EditText? = null
    var agentUpiId: EditText? = null
    var agentEmail: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_agent_registration)

        agentName = findViewById(R.id.etAgentRegAgentName)
        agentMobile = findViewById(R.id.etAgentRegAgentMobile)
        agentUpiId = findViewById(R.id.etAgentRegUpiId)
        agentEmail = findViewById(R.id.etAgentRegAgentEmail)

        var register = findViewById<Button>(R.id.btnRegister)
        register.setOnClickListener{
            if (agentName!!.text.toString().equals("") || agentMobile!!.text.toString().equals("")
                || agentUpiId!!.text.toString().equals("") || agentEmail!!.text.toString().equals("")) {
                Toast.makeText(this, "Please Enter all the fields", Toast.LENGTH_SHORT).show()
            }
            else if (agentMobile!!.text.toString().length != 10){
                agentMobile!!.setError("Enter Valid Mobile Number")
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(agentEmail!!.getText().toString()).matches()){
                agentEmail!!.setError("Enter Valid Email")
            }
            else if (Utils.checkInternetConnectivity(this) == false){
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            }
            else {
                registerAgent()
            }
        }
    }

    //Save Registration Data in Server
    @SuppressLint("LongLogTag")
    fun registerAgent() {
        Log.d(TAG, "registerAgent: ")

        var pd = Dialog(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.progress, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.setCancelable(false)

        class RegisterData : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String {
                var response: String = getRegisterAgent()
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
                Toast.makeText(this@NewAgentRegistration, result, Toast.LENGTH_LONG).show()
                agentName!!.setText("")
                agentMobile!!.setText("")
                agentUpiId!!.setText("")
                agentEmail!!.setText("")
            }
        }
        var saveData = RegisterData().execute()
    }

    //Export Registration Data Using HTTP POST Method
    @SuppressLint("LongLogTag")
    fun getRegisterAgent(): String {

        var response: String = ""

        val url = URL("https://hillstoneresort.com/Resorts/RegisterAgent.php")
        Log.d(TAG, "getRegisterAgent URL: " + url)
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

    @SuppressLint("LongLogTag")
    fun getDataToJson(): String {
        var dataJson: JSONObject = JSONObject()
        dataJson.put("name", agentName!!.text.toString())
        dataJson.put("mobile", agentMobile!!.text.toString())
        dataJson.put("upi_id", agentUpiId!!.text.toString())
        dataJson.put("email", agentEmail!!.text.toString())

        Log.d(TAG, "getDataToJson: " + dataJson.toString())
        return dataJson.toString()
    }
}