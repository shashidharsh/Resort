package com.ssh.resort.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssh.resort.R
import com.ssh.resort.data.ReportsListData

class ReportsListAdapter(context: Context, reportLists : ArrayList<ReportsListData>) : RecyclerView.Adapter<ReportsListAdapter.ItemViewHolder>() {
    private val TAG = "ReportsListAdapter"

    var reportList: ArrayList<ReportsListData>
    var context: Context? = null

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

        holder.tac.setText(reportList[position].tac)
        holder.b2b.setText(reportList[position].totalB2B)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    init {
        this.context = context
        this.reportList = reportLists
    }

    fun totalTAC(): String {
        var totalTAC: Float = 0.0f
        for ( i in 0 until  reportList.size){
            totalTAC += reportList[i].tac.toFloat()
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

        var tac: TextView
        var b2b: TextView

        private var mClickListener: ClickListener? = null

        interface ClickListener {
            fun onItemClick(view: View?, position: Int)
        }

        fun setOnClickListener(clickListener: ClickListener?) {
            mClickListener = clickListener
        }

        init {
            tac = itemView.findViewById(R.id.reportsListItemTAC)
            b2b = itemView.findViewById(R.id.reportsListItemB2B)

            itemView.setOnClickListener { view ->
                mClickListener!!.onItemClick(view, adapterPosition)
            }
        }
    }
}