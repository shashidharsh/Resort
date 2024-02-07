package com.ssh.resort.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssh.resort.R
import com.ssh.resort.TacAgentTransactionDetails
import com.ssh.resort.data.TacAgentsTransactionListData
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

class TacAgentTransactionListAdapter(context: Context, transactionLists : ArrayList<TacAgentsTransactionListData>, upiID: String) : RecyclerView.Adapter<TacAgentTransactionListAdapter.ItemViewHolder>() {
    private val TAG = "TacAgentTransactionListAdapter"

    var transactionList: ArrayList<TacAgentsTransactionListData>
    var cntxt: Context? = null
    var upi_id: String? = null
    var transactionID: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.tac_agent_transactions_list_item, parent, false)
        val viewHolder = ItemViewHolder(itemView)
        viewHolder.setOnClickListener(object : ItemViewHolder.ClickListener {
            override fun onItemClick(view: View?, position: Int) {
                var transactionList = transactionList.get(position)
                val intent = Intent(cntxt, TacAgentTransactionDetails::class.java)
                intent.putExtra("id", transactionList.id)
                intent.putExtra("guestName", transactionList.guestName)
                intent.putExtra("noOfPersons", transactionList.noOfPersons)
                intent.putExtra("noOfChildrens", transactionList.noOfChildrens)
                intent.putExtra("packageAdult", transactionList.packageAdult)
                intent.putExtra("packageChild", transactionList.packageChild)
                intent.putExtra("selectedCo", transactionList.selectedCo)
                intent.putExtra("selectedCoMobile", transactionList.selectedCoMobile)
                intent.putExtra("enterB2B", transactionList.enterB2B)
                intent.putExtra("advance", transactionList.advance)
                intent.putExtra("activities", transactionList.activities)
                intent.putExtra("selectedActivity", transactionList.selectedActivity)
                intent.putExtra("noPersonActivity", transactionList.noPersonActivity)
                intent.putExtra("ActivityPaymentStatus", transactionList.activityPaymentStatus)
                intent.putExtra("roomNumber", transactionList.roomNumber)
                intent.putExtra("driver", transactionList.driver)
                intent.putExtra("driverCost", transactionList.driverCost)
                intent.putExtra("type", transactionList.type)
                intent.putExtra("partner", transactionList.partner)
                intent.putExtra("totB2B", transactionList.totalB2B)
                intent.putExtra("totActivityPrice", transactionList.totActivityPrice)
                intent.putExtra("tac", transactionList.tac)
                intent.putExtra("total", transactionList.total)
                intent.putExtra("grandTotal", transactionList.grandTotal)
                intent.putExtra("paymentType", transactionList.paymentType)
                intent.putExtra("cash", transactionList.cash)
                intent.putExtra("upi", transactionList.upi)
                intent.putExtra("cashStatus", transactionList.cashStatus)
                intent.putExtra("upiStatus", transactionList.upiStatus)
                intent.putExtra("dateTime", transactionList.dateTime)
                intent.putExtra("upiID", upi_id)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                cntxt!!.startActivity(intent)
            }
        })
        return viewHolder
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        Log.d("TAG", "onBindViewHolder:" + transactionList[position])

        holder.guestName.setText(transactionList[position].guestName)
        holder.txnDateTime.setText(transactionList[position].dateTime)
        holder.agentName.setText(transactionList[position].selectedCo)
        holder.total.setText(transactionList[position].total)
        holder.activityPrice.setText(transactionList[position].totActivityPrice)

        if (transactionList[position].activityPaymentStatus.equals("Pending")) {
            holder.activityPaymentStatus.setText(transactionList[position].activityPaymentStatus)
            holder.activityPaymentStatus.setTextColor(ContextCompat.getColor(cntxt!!, R.color.red))
            holder.btnPaymentReceived.setEnabled(true)
        } else if (transactionList[position].activityPaymentStatus.equals("Received")) {
            holder.activityPaymentStatus.setText(transactionList[position].activityPaymentStatus)
            holder.activityPaymentStatus.setTextColor(ContextCompat.getColor(cntxt!!, R.color.green))
        } else {
            holder.activityPaymentStatus.setText(transactionList[position].activityPaymentStatus)
            holder.activityPaymentStatus.setTextColor(ContextCompat.getColor(cntxt!!, R.color.orange))
            holder.btnPaymentReceived.setEnabled(false)
        }

        if ((transactionList[position].totActivityPrice).toFloat() == 0.0f){
            holder.btnPaymentReceived.setEnabled(false)
        } else if (transactionList[position].activityPaymentStatus.equals("Received")){
            holder.btnPaymentReceived.setEnabled(false)
        }
        else{
            holder.btnPaymentReceived.setEnabled(true)
        }

        holder.btnPaymentReceived.setOnClickListener {
            setdata(transactionList[position].id)
            updateActivityPaymentStatus()
        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    init {
        this.cntxt = context
        this.transactionList = transactionLists
        upi_id = upiID
    }

    fun setdata(id: String){
        transactionID = id
        Log.d(TAG, "setdata transactionID: " + transactionID)
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val TAG = "TransactionListAdapter"

        var guestName: TextView
        var txnDateTime: TextView
        var agentName: TextView
        var total: TextView
        var activityPrice: TextView
        var activityPaymentStatus: TextView
        var btnPaymentReceived: Button

        private var mClickListener: ClickListener? = null

        interface ClickListener {
            fun onItemClick(view: View?, position: Int)
        }

        fun setOnClickListener(clickListener: ClickListener?) {
            mClickListener = clickListener
        }

        init {
            guestName = itemView.findViewById(R.id.tacAgentTransactionsGuestName)
            txnDateTime = itemView.findViewById(R.id.tacAgentTransactionsDateTime)
            agentName = itemView.findViewById(R.id.tacAgentTransactionsAgentName)
            total = itemView.findViewById(R.id.tacAgentTransactionsTotal)
            activityPrice = itemView.findViewById(R.id.tacAgentTransactionsActivityPrice)
            activityPaymentStatus = itemView.findViewById(R.id.tacAgentTransactionsActivityPaymentStatus)
            btnPaymentReceived = itemView.findViewById(R.id.tacAgentTransactionsActivityPaymentReceived)

            itemView.setOnClickListener { view ->
                mClickListener!!.onItemClick(view, adapterPosition)
            }
        }
    }

    //Update Activity Payment Status in Server
    @SuppressLint("LongLogTag")
    fun updateActivityPaymentStatus() {
        Log.d(TAG, "updateActivityPaymentStatus: ")

        var pd = Dialog(cntxt!!)
        val view: View = LayoutInflater.from(cntxt).inflate(R.layout.progress, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.setCancelable(false)

        class UpdatePaymentStatus : AsyncTask<Void, Void, String>() {
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
                    Toast.makeText(cntxt, result, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(cntxt, result, Toast.LENGTH_LONG).show()
                }
            }
        }

        var updatePaymentStatus = UpdatePaymentStatus().execute()
    }

    //Update Using HTTP POST Method
    @SuppressLint("LongLogTag")
    fun updateData(): String {

        var response: String = ""

        val url = URL("https://hillstoneresort.com/Resorts/UpdatePaymentStatus.php")
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
        dataJson.put("id", transactionID)
        dataJson.put("paymentStatus", "Received")

        Log.d(TAG, "getDataToJson: " + dataJson.toString())
        return dataJson.toString()
    }
}