package com.ssh.resort

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.Date
import javax.net.ssl.HttpsURLConnection

class Expense : AppCompatActivity() {

    val TAG = "Expense"

    var expenseAmount : EditText? = null
    var expenseReason : EditText? = null
    var expenseBy : EditText? = null

    var currentDate : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        //Current Date
        val smplDateFormat = SimpleDateFormat("yyyy-MM-dd")
        currentDate = smplDateFormat.format(Date())

        //Initialization
        expenseAmount = findViewById(R.id.etExpenseAmount)
        expenseReason = findViewById(R.id.etExpenseReason)
        expenseBy = findViewById(R.id.etExpenseBy)

        var btnSave = findViewById<Button>(R.id.btnSaveExpense)
        btnSave.setOnClickListener{
            if (Utils.checkInternetConnectivity(this) == false){
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            }
            else if (expenseAmount!!.text.toString().equals("") || expenseReason!!.text.toString().equals("") || expenseBy!!.text.toString().equals("")) {
                Toast.makeText(this, "Please Enter all the fields", Toast.LENGTH_SHORT).show()
            }
            else{
                insertExpense()
            }
        }

        var viewExpenseDatewise = findViewById<Button>(R.id.btnViewExpenseDatewise)
        viewExpenseDatewise.setOnClickListener{
            val intent = Intent(this, ViewExpenseDatewise::class.java)
            startActivity(intent)
        }

        var viewAllExpense = findViewById<Button>(R.id.btnViewAllExpenses)
        viewAllExpense.setOnClickListener{
            val intent = Intent(this, ViewAllExpenses::class.java)
            startActivity(intent)
        }
    }

    //Save Expense Data in Server
    fun insertExpense() {
        Log.d(TAG, "insertExpense: ")

        var pd = Dialog(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.progress, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.setCancelable(false)

        class ExpenseData : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String {
                var response: String = getExpenseData()
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
                Toast.makeText(this@Expense, result, Toast.LENGTH_LONG).show()
                expenseAmount!!.setText("")
                expenseReason!!.setText("")
                expenseBy!!.setText("")
            }
        }
        var saveData = ExpenseData().execute()
    }

    //Export Expense Data Using HTTP POST Method
    @SuppressLint("LongLogTag")
    fun getExpenseData(): String {

        var response: String = ""

        val url = URL("https://hillstoneresort.com/Resorts/InsertExpense.php")
        Log.d(TAG, "getExpenseData URL: " + url)
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
        dataJson.put("ExpenseAmount", expenseAmount!!.text.toString())
        dataJson.put("ExpenseReason", expenseReason!!.text.toString())
        dataJson.put("ExpenseBy", expenseBy!!.text.toString())
        dataJson.put("Date", currentDate)

        Log.d(TAG, "getDataToJson: " + dataJson.toString())
        return dataJson.toString()
    }
}