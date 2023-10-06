package com.ssh.resort

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
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

class Login : AppCompatActivity() {

    val TAG = "Login"

    var userName: EditText? = null
    var password: EditText? = null

    var loginType: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginType = intent.getStringExtra("Login")

        userName = findViewById(R.id.userName)
        password = findViewById(R.id.password)

        var login = findViewById<Button>(R.id.btnLogin)
        login.setOnClickListener{
            if (userName!!.text.toString().equals("") || password!!.text.toString().equals("")) {
                Toast.makeText(this, "Please Enter all the fields", Toast.LENGTH_SHORT).show()
            }
            else if (Utils.checkInternetConnectivity(this) == false){
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            }
            else {
                login()
            }
        }
    }

    //Login Data in Server
    @SuppressLint("LongLogTag")
    fun login() {
        Log.d(TAG, "login: ")

        var pd = Dialog(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.progress, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.setCancelable(false)

        class LoginData : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String {
                var response: String = loginData()
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

                if (result.contains("Login Successful")){
                    if (loginType!!.contains("Admin")) {
                        val intent = Intent(this@Login, AdminHomePage::class.java)
                        intent.putExtra("UserName", userName!!.text.toString())
                        startActivity(intent)
                        finish()
                    }
                    else{
                        val intent = Intent(this@Login, ManagerHomePage::class.java)
                        intent.putExtra("UserName", userName!!.text.toString())
                        startActivity(intent)
                        finish()
                    }
                }else{
                    Toast.makeText(this@Login, result, Toast.LENGTH_LONG).show()
                }
            }
        }
        var loginData = LoginData().execute()
    }

    //Login Using HTTP POST Method
    @SuppressLint("LongLogTag")
    fun loginData(): String {

        var response: String = ""

        val url = URL("https://sshsoftwares.in/Resort/AdminLogin.php")
        Log.d(TAG, "loginData URL: " + url)
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
        dataJson.put("userName", userName!!.text.toString())
        dataJson.put("password", password!!.text.toString())

        Log.d(TAG, "getDataToJson: " + dataJson.toString())
        return dataJson.toString()
    }
}