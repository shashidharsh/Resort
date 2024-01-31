package com.ssh.resort

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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.ssh.appdataremotedb.HTTPDownload
import com.ssh.resort.adapter.TransactionListAdapter
import com.ssh.resort.data.TransactionListData

class Transactions : AppCompatActivity() {

    private val TAG = "Transactions"

    var transactionListRecyclerView: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var adapter: TransactionListAdapter? = null

    val transactionList: ArrayList<TransactionListData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                getTransactionDetailsFromServer()
            } else {
                Snackbar.make(getWindow().getDecorView().getRootView(), "No Internet Connection", Snackbar.LENGTH_LONG)
                    .setTextColor(Color.WHITE)
                    .setBackgroundTint(ContextCompat.getColor(this@Transactions, R.color.colorAccent))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .show()
            }
        }

        //RecyclerView
        transactionListRecyclerView = findViewById(R.id.rvTransactionList)
        transactionListRecyclerView!!.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        transactionListRecyclerView!!.setLayoutManager(layoutManager)

        adapter = TransactionListAdapter(applicationContext, transactionList)
        transactionListRecyclerView!!.adapter = adapter
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
                transactionList.clear()

                for (i in 0 until transactionResult.length()) {
                    val json_data = transactionResult.getJSONObject(i)
                    val transactionListData = TransactionListData(
                        json_data.getString("id"),
                        json_data.getString("GuestName"),
                        json_data.getString("NoOfPersons"),
                        json_data.getString("NoOfChildrens"),
                        json_data.getString("PackageAdult"),
                        json_data.getString("PackageChild"),
                        json_data.getString("SelectedCo"),
                        json_data.getString("SelectedCoMobile"),
                        json_data.getString("EnterB2BPrice"),
                        json_data.getString("Advance"),
                        json_data.getString("Activities"),
                        json_data.getString("SelectedActivity"),
                        json_data.getString("NoPersonActivity"),
                        json_data.getString("RoomNumber"),
                        json_data.getString("Driver"),
                        json_data.getString("DriverCost"),
                        json_data.getString("Type"),
                        json_data.getString("Partner"),
                        json_data.getString("B2B"),
                        json_data.getString("TotActivityPrice"),
                        json_data.getString("TAC"),
                        json_data.getString("Total"),
                        json_data.getString("GrandTotal"),
                        json_data.getString("PaymentType"),
                        json_data.getString("Cash"),
                        json_data.getString("UPI"),
                        json_data.getString("CashPayStatus"),
                        json_data.getString("UPIPayStatus"),
                        json_data.getString("DateTime"),
                        json_data.getString("Date"))
                    transactionList.add(transactionListData)
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
                    adapter!!.notifyDataSetChanged()
                }
            }
        }
        var result = TransactionDetails().execute()
    }
}