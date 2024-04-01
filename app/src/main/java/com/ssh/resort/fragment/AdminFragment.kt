package com.ssh.resort.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.ssh.appdataremotedb.HTTPDownload
import com.ssh.appdataremotedb.Utils
import com.ssh.resort.AdminHomePage
import com.ssh.resort.ManagerHomePage
import com.ssh.resort.NetworkConnection
import com.ssh.resort.R
import com.ssh.resort.UpdateApp
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class AdminFragment : Fragment() {

    private val TAG = "AdminFragment"

    var userName: EditText? = null
    var password: EditText? = null

    var loginType: String? = "Admin"

    var id: String? = ""
    var versionName: String? = ""
    var appVersion: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: $view")
        initUI()
    }

    fun initUI() {

        val networkConnection = NetworkConnection(requireView().context)
        networkConnection.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                downloadAppVersion()
            } else {
                getActivity()?.let { Snackbar.make(it.findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setTextColor(Color.WHITE)
                    .setBackgroundTint(ContextCompat.getColor(requireActivity(), R.color.colorAccent))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .show() }
            }
        }

        appVersion = requireView().findViewById(R.id.tvLoginAppVersion)
        appVersion!!.setText("App Version: " + getApplicationVersion())

        userName = requireView().findViewById(R.id.adminUserName)
        password = requireView().findViewById(R.id.adminPassword)

        var login = requireView().findViewById<Button>(R.id.btnAdminLogin)
        login.setOnClickListener{
            if (userName!!.text.toString().equals("") || password!!.text.toString().equals("")) {
                Toast.makeText(requireView().context, "Please Enter all the fields", Toast.LENGTH_SHORT).show()
            }
            else if (Utils.checkInternetConnectivity(requireView().context) == false){
                Toast.makeText(requireView().context, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
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

        var pd = Dialog(requireView().context)
        val view: View = LayoutInflater.from(requireView().context).inflate(R.layout.progress, null)
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
                    val intent = Intent(requireView().context, AdminHomePage::class.java)
                    intent.putExtra("UserName", userName!!.text.toString())
                    startActivity(intent)
                    requireActivity().finish()
                    requireActivity().finishAffinity()
                }else{
                    Toast.makeText(requireView().context, result, Toast.LENGTH_LONG).show()
                }
            }
        }
        var loginData = LoginData().execute()
    }

    //Login Using HTTP POST Method
    @SuppressLint("LongLogTag")
    fun loginData(): String {

        var response: String = ""

        val url = URL("https://hillstoneresort.com/Resorts/AdminLogin.php")
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
            Log.d(TAG, "MalformedURLException: " + e)
        } catch (ce: ConnectException) {
            ce.printStackTrace()
            Log.d(TAG, "ConnectException: Connection Timeout" + ce)
            response = "Connection Timeout"
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
        dataJson.put("userType", loginType)

        Log.d(TAG, "getDataToJson: " + dataJson.toString())
        return dataJson.toString()
    }

    //Get Application Version
    fun getApplicationVersion() : String{
        val pm = requireView().context.packageManager
        val pkgName = requireView().context.packageName
        var pkgInfo: PackageInfo? = null
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0)
        }
        catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val ver = pkgInfo!!.versionName
        Log.d(TAG, "getApplicationVersion: " + ver)

        return ver
    }

    fun downloadAppVersion() {
        Log.d(TAG, "downloadAppVersion")

        var pd = Dialog(requireView().context)
        val view: View = LayoutInflater.from(requireView().context).inflate(R.layout.progress, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.setCancelable(false)

        class DownloadAppVersion : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {
                val profileDetailsUrl = "https://hillstoneresort.com/Resorts/CheckAppVersion.php"

                var httpDownload = HTTPDownload()
                val httpStatus = httpDownload.downloadUrl(profileDetailsUrl)

                if(httpStatus.status != HTTPDownload.STATUS_SUCCESS) {
                    Log.d(TAG, "downloadAppVersion jsonojb null" + httpStatus.status)
                    return false
                }

                val contactDetails = httpStatus.value!!.getJSONObject("results")
                if (contactDetails == null) {
                    Log.d(TAG, "downloadAppVersion null")
                    return false
                }

                id = contactDetails.getString("id")
                versionName = contactDetails.getString("versionName")

                return true
            }

            override fun onPreExecute() {
                super.onPreExecute()
                pd.show()
            }

            @SuppressLint("SetTextI18n")
            override fun onPostExecute(result: Boolean) {
                super.onPostExecute(result)
                Log.d(TAG, "downloadAppVersion onPostExecute result:" + result)
                pd.cancel()
                if (result == false) {
                    Toast.makeText(requireView().context, "Data Not Found", Toast.LENGTH_SHORT).show()
                }
                else {
                    if (!versionName.equals(getApplicationVersion())){
                        val intent = Intent(requireView().context, UpdateApp::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        activity?.finishAffinity()
                    }
                    Log.d(TAG, "downloadAppVersion: Successful")
                }
            }
        }
        var result = DownloadAppVersion().execute()
    }
}