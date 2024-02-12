package com.ssh.resort

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.ssh.appdataremotedb.HTTPDownload
import com.ssh.resort.adapter.ReportsListAdapter
import com.ssh.resort.adapter.TransactionListAdapter
import com.ssh.resort.data.ReportsListData
import com.ssh.resort.data.TransactionListData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class Reports : AppCompatActivity() {

    private val TAG = "Reports"

    var reportsListRecyclerView: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var adapter: ReportsListAdapter? = null

    val reportsList: ArrayList<ReportsListData> = ArrayList()

    var tvFromDate: TextView? = null
    var tvToDate: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                getTransactionDetailsFromServer()
            } else {
                Snackbar.make(getWindow().getDecorView().getRootView(), "No Internet Connection", Snackbar.LENGTH_LONG)
                    .setTextColor(Color.WHITE)
                    .setBackgroundTint(ContextCompat.getColor(this@Reports, R.color.colorAccent))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .show()
            }
        }

        //Set From Date and End Date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        tvFromDate = findViewById(R.id.reportsFromDate) as TextView
        tvToDate = findViewById(R.id.reportsToDate) as TextView

        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val currentDate: String = simpleDateFormat.format(Date())
        tvFromDate!!.text = currentDate
        tvToDate!!.text = currentDate

        //Button From Date
        tvFromDate!!.setOnClickListener {
            val datePickerDialog =
                DatePickerDialog(this@Reports, DatePickerDialog.OnDateSetListener
                { view, year, monthOfYear, dayOfMonth ->
                    tvFromDate!!.setText("" + String.format("%02d", dayOfMonth) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year)
                }, year, month, day)
            datePickerDialog.show()
        }

        //Button To Date
        tvToDate!!.setOnClickListener {
            val datePickerDialog =
                DatePickerDialog(this@Reports, DatePickerDialog.OnDateSetListener
                { view, year, monthOfYear, dayOfMonth ->
                    tvToDate!!.setText("" + String.format("%02d", dayOfMonth) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year)
                }, year, month, day)
            datePickerDialog.show()
        }

        //RecyclerView
        reportsListRecyclerView = findViewById(R.id.rvReportsList)
        reportsListRecyclerView!!.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        reportsListRecyclerView!!.setLayoutManager(layoutManager)

        adapter = ReportsListAdapter(applicationContext, reportsList)
        reportsListRecyclerView!!.adapter = adapter
    }

    //Download Transaction Details from Server
    fun getTransactionDetailsFromServer() {
        Log.d(TAG, "getTransactionDetailsFromServer")

        var pd = Dialog(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.progress, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.setCancelable(false)

        class TransactionDetails : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {

                val transactionDetailsUrl = "https://hillstoneresort.com/Resorts/GetCheckinData.php"

                var httpDownload = HTTPDownload()
                val httpStatus = httpDownload.downloadUrl(transactionDetailsUrl)

                if(httpStatus.status != HTTPDownload.STATUS_SUCCESS) {
                    Log.d(TAG, "getTransactionDetailsFromServer jsonojb null" + httpStatus.status)
                    return false
                }

                val transactionResult = httpStatus.value!!.optJSONArray("results")
                if (transactionResult == null){
                    return false
                }
                reportsList.clear()

                for (i in 0 until transactionResult.length()) {
                    val json_data = transactionResult.getJSONObject(i)
                    val transactionListData = ReportsListData(
                        json_data.getString("id"),
                        json_data.getString("B2B"),
                        json_data.getString("TAC"))
                    reportsList.add(transactionListData)
                    Log.d(TAG, "getTransactionDetailsFromServer transactionData: " + transactionListData)
                }
                return true
            }

            override fun onPreExecute() {
                super.onPreExecute()
                pd.show()
            }

            override fun onPostExecute(result: Boolean) {
                super.onPostExecute(result)
                Log.d(TAG, "getTransactionDetailsFromServer onPostExecute result:" + result)
                pd.cancel()
                if (result == false) {
                    Toast.makeText(applicationContext, "$result", Toast.LENGTH_SHORT).show()
                }
                else {
                    //Total TAC
                    var totalTAC = findViewById<TextView>(R.id.reportsTotalTAC)
                    var totTAC = adapter!!.totalTAC()
                    totalTAC!!.setText(totTAC)
                    //Total B2B
                    var totalB2B = findViewById<TextView>(R.id.reportsTotalB2B)
                    var totB2B = adapter!!.totalB2B()
                    totalB2B!!.setText(totB2B)
                    adapter!!.notifyDataSetChanged()
                }
            }
        }
        var result = TransactionDetails().execute()
    }
}