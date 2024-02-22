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
import com.ssh.resort.data.ExpenseListData
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

class ExpenseListAdapter(context: Context, expenseLists : ArrayList<ExpenseListData>) : RecyclerView.Adapter<ExpenseListAdapter.ItemViewHolder>() {
    private val TAG = "ExpenseListAdapter"

    var expenseList: ArrayList<ExpenseListData>
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.expense_list_item, parent, false)
        val viewHolder = ItemViewHolder(itemView)
        viewHolder.setOnClickListener(object : ItemViewHolder.ClickListener {
            override fun onItemClick(view: View?, position: Int) {
                var reportList = expenseList.get(position)
                /*val intent = Intent(context, TransactionDetails::class.java)
                intent.putExtra("id", reportList.id)
                intent.putExtra("guestName", reportList.guestName)
                intent.putExtra("noOfPersons", reportList.noOfPersons)
                intent.putExtra("noOfChildrens", reportList.noOfChildrens)
                intent.putExtra("packageAdult", reportList.packageAdult)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context!!.startActivity(intent)*/
            }
        })
        return viewHolder
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        Log.d("TAG", "onBindViewHolder:" + expenseList[position])

        holder.reason.setText(expenseList[position].expenseReason)
        holder.date.setText(expenseList[position].date)
        holder.expenseBy.setText(expenseList[position].expenseBy)
        holder.amount.setText(expenseList[position].expenseAmount)
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

    init {
        this.context = context
        this.expenseList = expenseLists
    }

    fun totalExpense(): String {
        var totalExpense: Float = 0.0f
        for ( i in 0 until  expenseList.size){
            totalExpense += expenseList[i].expenseAmount.toFloat()
        }
        return totalExpense.toString()
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val TAG = "TransactionListAdapter"

        var reason: TextView
        var date: TextView
        var expenseBy: TextView
        var amount: TextView

        private var mClickListener: ClickListener? = null

        interface ClickListener {
            fun onItemClick(view: View?, position: Int)
        }

        fun setOnClickListener(clickListener: ClickListener?) {
            mClickListener = clickListener
        }

        init {
            reason = itemView.findViewById(R.id.expenseListItemReason)
            date = itemView.findViewById(R.id.expenseListItemDate)
            expenseBy = itemView.findViewById(R.id.expenseListItemExpenseBy)
            amount = itemView.findViewById(R.id.expenseListItemAmount)

            itemView.setOnClickListener { view ->
                mClickListener!!.onItemClick(view, adapterPosition)
            }
        }
    }
}