package com.ssh.resort

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.ssh.appdataremotedb.HTTPDownload
import com.ssh.appdataremotedb.Utils
import com.ssh.resort.adapter.TacAgentTransactionListAdapter
import com.ssh.resort.data.TacAgentsTransactionListData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class TacAgentTransactions : AppCompatActivity() {

    private val TAG = "TacAgentTransactions"

    var tvCurrentDate: TextView? = null

    var agentID : String? = ""
    var agentName : String? = ""
    var agentMobile : String? = ""
    var agentUPI : String? = ""

    var tacAgentTransactionListRecyclerView: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var adapter: TacAgentTransactionListAdapter? = null

    val transactionList: ArrayList<TacAgentsTransactionListData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tac_agent_transactions)

        agentID = intent.getStringExtra("AgentID")
        agentName = intent.getStringExtra("AgentName")
        agentMobile = intent.getStringExtra("AgentMobile")
        agentUPI = intent.getStringExtra("AgentUPI")

        //Set From Date and End Date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        tvCurrentDate = findViewById(R.id.tacAgentTransactionDate) as TextView

        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val currentDate: String = simpleDateFormat.format(Date())
        tvCurrentDate!!.text = currentDate

        //Button Start Date
        tvCurrentDate!!.setOnClickListener {
            val datePickerDialog =
                DatePickerDialog(this@TacAgentTransactions, DatePickerDialog.OnDateSetListener
                { view, year, monthOfYear, dayOfMonth ->
                    tvCurrentDate!!.setText("" + String.format("%02d", dayOfMonth) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year)
                }, year, month, day)
            datePickerDialog.show()
        }

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        //RecyclerView
        tacAgentTransactionListRecyclerView = findViewById(R.id.rvTacAgentTransactions)
        tacAgentTransactionListRecyclerView!!.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        tacAgentTransactionListRecyclerView!!.setLayoutManager(layoutManager)

        adapter = TacAgentTransactionListAdapter(this, transactionList, agentUPI!!)
        tacAgentTransactionListRecyclerView!!.adapter = adapter
        adapter!!.notifyDataSetChanged()

        var showData = findViewById<Button>(R.id.tacAgentTransactionShow)
        showData.setOnClickListener{
            if (Utils.checkInternetConnectivity(this) == false){
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            }
            else {
                getTransactionDetailsFromServer()
            }
        }
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

                var currentDateFormat = SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("dd-MM-yyyy").parse(tvCurrentDate!!.text.toString()))
                Log.d(TAG, "currentDateFormat: " + currentDateFormat)

                val transactionDetailsUrl = Constants.BASE_URL + "GetAgentTransactions.php?SelectedCoMobile=" + agentMobile + "&Date=" + currentDateFormat
                Log.d(TAG, "transactionDetailsUrl: " + transactionDetailsUrl)

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
                    val transactionListData = TacAgentsTransactionListData(
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
                        json_data.getString("ActivityPaymentStatus"),
                        json_data.getString("RoomNumber"),
                        json_data.getString("Driver"),
                        json_data.getString("DriverCost"),
                        json_data.getString("Type"),
                        json_data.getString("Partner"),
                        json_data.getString("B2B"),
                        json_data.getString("BalanceB2B"),
                        json_data.getString("TotActivityPrice"),
                        json_data.getString("TAC"),
                        json_data.getString("BalanceTAC"),
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
                    if (transactionList.size == 0){
                        Snackbar.make(getWindow().getDecorView().getRootView(), "No Transactions On this Date, Please Choose Other Date.", Snackbar.LENGTH_LONG)
                            .setTextColor(Color.WHITE)
                            .setBackgroundTint(ContextCompat.getColor(this@TacAgentTransactions, R.color.colorAccent))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .show()
                        transactionList.clear()
                        adapter!!.notifyDataSetChanged()
                    }
                    else {
                        adapter!!.notifyDataSetChanged()
                    }
                }
            }
        }
        var result = TransactionDetails().execute()
    }
}