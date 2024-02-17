package com.ssh.resort.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ssh.resort.R
import com.ssh.resort.data.ReportsListData
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

class ReportsListAdapter(context: Context, reportLists : ArrayList<ReportsListData>) : RecyclerView.Adapter<ReportsListAdapter.ItemViewHolder>() {
    private val TAG = "ReportsListAdapter"

    var reportList: ArrayList<ReportsListData>
    var context: Context? = null
    var ID: String = ""
    var amount: Float = 0.0f
    var currentPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.reports_list_item, parent, false)
        val viewHolder = ItemViewHolder(itemView)
        viewHolder.setOnClickListener(object : ItemViewHolder.ClickListener {
            override fun onItemClick(view: View?, position: Int) {
                var reportList = reportList.get(position)
                /*val intent = Intent(context, TransactionDetails::class.java)
                intent.putExtra("id", reportList.id)
                intent.putExtra("guestName", reportList.guestName)
                intent.putExtra("noOfPersons", reportList.noOfPersons)
                intent.putExtra("noOfChildrens", reportList.noOfChildrens)
                intent.putExtra("packageAdult", reportList.packageAdult)
                intent.putExtra("packageChild", reportList.packageChild)
                intent.putExtra("selectedCo", reportList.selectedCo)
                intent.putExtra("selectedCoMobile", reportList.selectedCoMobile)
                intent.putExtra("enterB2B", reportList.enterB2B)
                intent.putExtra("advance", reportList.advance)
                intent.putExtra("activities", reportList.activities)
                intent.putExtra("selectedActivity", reportList.selectedActivity)
                intent.putExtra("noPersonActivity", reportList.noPersonActivity)
                intent.putExtra("activityPaymentStatus", reportList.activityPaymentStatus)
                intent.putExtra("roomNumber", reportList.roomNumber)
                intent.putExtra("driver", reportList.driver)
                intent.putExtra("driverCost", reportList.driverCost)
                intent.putExtra("type", reportList.type)
                intent.putExtra("partner", reportList.partner)
                intent.putExtra("totB2B", reportList.totalB2B)
                intent.putExtra("totActivityPrice", reportList.totActivityPrice)
                intent.putExtra("tac", reportList.tac)
                intent.putExtra("total", reportList.total)
                intent.putExtra("grandTotal", reportList.grandTotal)
                intent.putExtra("paymentType", reportList.paymentType)
                intent.putExtra("cash", reportList.cash)
                intent.putExtra("upi", reportList.upi)
                intent.putExtra("cashStatus", reportList.cashStatus)
                intent.putExtra("upiStatus", reportList.upiStatus)
                intent.putExtra("dateTime", reportList.dateTime)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context!!.startActivity(intent)*/
            }
        })
        return viewHolder
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        Log.d("TAG", "onBindViewHolder:" + reportList[position])

        holder.b2b.setText(reportList[position].totalB2B)
        holder.tac.setText(reportList[position].totalTAC)

        holder.tvSattle.setOnClickListener {
            setData(reportList[position].id, reportList[position].totalB2B, reportList[position].totalTAC, position)
            updatePaymentSattlementStatus()
        }
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    fun setData(id: String, b2bAmount: String, tacAmt: String, position: Int){
        ID = id
        amount = b2bAmount.toFloat() + tacAmt.toFloat()
        currentPosition = position
        Log.d(TAG, "setdata ID: " + ID)
    }

    init {
        this.context = context
        this.reportList = reportLists
    }

    fun totalTAC(): String {
        var totalTAC: Float = 0.0f
        for ( i in 0 until  reportList.size){
            totalTAC += reportList[i].totalTAC.toFloat()
        }
        return totalTAC.toString()
    }

    fun totalB2B(): String {
        var totalB2B: Float = 0.0f
        for ( i in 0 until  reportList.size){
            totalB2B += reportList[i].totalB2B.toFloat()
        }
        return totalB2B.toString()
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val TAG = "TransactionListAdapter"

        var b2b: TextView
        var tac: TextView
        var tvSattle: TextView

        private var mClickListener: ClickListener? = null

        interface ClickListener {
            fun onItemClick(view: View?, position: Int)
        }

        fun setOnClickListener(clickListener: ClickListener?) {
            mClickListener = clickListener
        }

        init {
            b2b = itemView.findViewById(R.id.reportsListItemB2B)
            tac = itemView.findViewById(R.id.reportsListItemTAC)
            tvSattle = itemView.findViewById(R.id.reportsListItemSattle)

            itemView.setOnClickListener { view ->
                mClickListener!!.onItemClick(view, adapterPosition)
            }
        }
    }

    //Update Activity Payment Status in Server
    @SuppressLint("LongLogTag")
    fun updatePaymentSattlementStatus() {
        Log.d(TAG, "updatePaymentSattlementStatus: ")

        var pd = Dialog(context!!)
        val view: View = LayoutInflater.from(context).inflate(R.layout.progress, null)
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
                    Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                    (context as Activity).finish()
                } else {
                    Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                }
            }
        }

        var updatePaymentStatus = UpdatePaymentStatus().execute()
    }

    //Update Using HTTP POST Method
    @SuppressLint("LongLogTag")
    fun updateData(): String {

        var response: String = ""

        val url = URL("https://hillstoneresort.com/Resorts/UpdateTotalB2B.php")
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
        dataJson.put("id", ID)
        dataJson.put("amount", amount)
        dataJson.put("tacAmount", "0.0")

        Log.d(TAG, "getDataToJson: " + dataJson.toString())
        return dataJson.toString()
    }
}