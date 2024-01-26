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
import com.ssh.resort.TransactionDetails
import com.ssh.resort.UpdateExistingAgent
import com.ssh.resort.data.ExistingAgentListData
import com.ssh.resort.data.TransactionListData

class TransactionListAdapter(context: Context, transactionLists : ArrayList<TransactionListData>) : RecyclerView.Adapter<TransactionListAdapter.ItemViewHolder>() {
    private val TAG = "TransactionListAdapter"

    var transactionList: ArrayList<TransactionListData>
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.transactions_list_item, parent, false)
        val viewHolder = ItemViewHolder(itemView)
        viewHolder.setOnClickListener(object : ItemViewHolder.ClickListener {
            override fun onItemClick(view: View?, position: Int) {
                var transactionList = transactionList.get(position)
                val intent = Intent(context, TransactionDetails::class.java)
                intent.putExtra("id", transactionList.id)
                intent.putExtra("guestName", transactionList.guestName)
                intent.putExtra("noOfPersons", transactionList.noOfPersons)
                intent.putExtra("noOfChildrens", transactionList.noOfChildrens)
                intent.putExtra("packageAdult", transactionList.packageAdult)
                intent.putExtra("packageChild", transactionList.packageChild)
                intent.putExtra("selectedCo", transactionList.selectedCo)
                intent.putExtra("enterB2B", transactionList.enterB2B)
                intent.putExtra("activities", transactionList.activities)
                intent.putExtra("selectedActivity", transactionList.selectedActivity)
                intent.putExtra("noPersonActivity", transactionList.noPersonActivity)
                intent.putExtra("roomNumber", transactionList.roomNumber)
                intent.putExtra("driver", transactionList.driver)
                intent.putExtra("driverCost", transactionList.driverCost)
                intent.putExtra("type", transactionList.type)
                intent.putExtra("partner", transactionList.partner)
                intent.putExtra("totB2B", transactionList.totalB2B)
                intent.putExtra("totActivityPrice", transactionList.totActivityPrice)
                intent.putExtra("tac", transactionList.tac)
                intent.putExtra("total", transactionList.total)
                intent.putExtra("paymentType", transactionList.paymentType)
                intent.putExtra("cash", transactionList.cash)
                intent.putExtra("upi", transactionList.upi)
                intent.putExtra("cashStatus", transactionList.cashStatus)
                intent.putExtra("upiStatus", transactionList.upiStatus)
                intent.putExtra("dateTime", transactionList.dateTime)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context!!.startActivity(intent)
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
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    init {
        this.context = context
        this.transactionList = transactionLists
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val TAG = "TransactionListAdapter"

        var guestName: TextView
        var txnDateTime: TextView
        var agentName: TextView
        var total: TextView

        private var mClickListener: ClickListener? = null

        interface ClickListener {
            fun onItemClick(view: View?, position: Int)
        }

        fun setOnClickListener(clickListener: ClickListener?) {
            mClickListener = clickListener
        }

        init {
            guestName = itemView.findViewById(R.id.transactionsGuestName)
            txnDateTime = itemView.findViewById(R.id.transactionsDateTime)
            agentName = itemView.findViewById(R.id.transactionsAgentName)
            total = itemView.findViewById(R.id.transactionsTotal)

            itemView.setOnClickListener { view ->
                mClickListener!!.onItemClick(view, adapterPosition)
            }
        }
    }
}