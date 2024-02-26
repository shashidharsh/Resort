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
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ssh.resort.R
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

class ReportsListAdapter(context: Context, reportLists : ArrayList<TacAgentsTransactionListData>) : RecyclerView.Adapter<ReportsListAdapter.ItemViewHolder>() {
    private val TAG = "ReportsListAdapter"

    var reportList: ArrayList<TacAgentsTransactionListData>
    var context: Context? = null
    var ID: String = ""
    var currentPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.reports_list_item, parent, false)
        val viewHolder = ItemViewHolder(itemView)
        viewHolder.setOnClickListener(object : ItemViewHolder.ClickListener {
            override fun onItemClick(view: View?, position: Int) {
                var reportList = reportList.get(position)

            }
        })
        return viewHolder
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        Log.d("TAG", "onBindViewHolder:" + reportList[position])

        holder.totalB2B.setText(reportList[position].totalB2B)
        holder.totalTAC.setText(reportList[position].totalTAC)
        holder.advance.setText(reportList[position].advance)
        holder.balanceTAC.setText(reportList[position].balanceTAC)
        holder.balanceB2B.setText(reportList[position].balanceB2B)

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

        var totalB2B: TextView
        var totalTAC: TextView
        var advance: TextView
        var balanceTAC: TextView
        var balanceB2B: TextView
        var tvSattle: TextView

        private var mClickListener: ClickListener? = null

        interface ClickListener {
            fun onItemClick(view: View?, position: Int)
        }

        fun setOnClickListener(clickListener: ClickListener?) {
            mClickListener = clickListener
        }

        init {
            totalB2B = itemView.findViewById(R.id.reportsListItemTotalB2B)
            totalTAC = itemView.findViewById(R.id.reportsListItemTotalTAC)
            advance = itemView.findViewById(R.id.reportsListItemAdvance)
            balanceTAC = itemView.findViewById(R.id.reportsListItemBalanceTAC)
            balanceB2B = itemView.findViewById(R.id.reportsListItemBalanceB2B)
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

        val url = URL("https://hillstoneresort.com/Resorts/UpdateBalanceB2B.php")
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
        dataJson.put("sattlementAmount", "0.0")

        Log.d(TAG, "getDataToJson: " + dataJson.toString())
        return dataJson.toString()
    }
}