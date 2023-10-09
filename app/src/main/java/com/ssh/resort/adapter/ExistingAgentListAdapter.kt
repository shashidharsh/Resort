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
import com.ssh.resort.UpdateExistingAgent
import com.ssh.resort.data.ExistingAgentListData

class ExistingAgentListAdapter(context: Context, agentLists : ArrayList<ExistingAgentListData>) : RecyclerView.Adapter<ExistingAgentListAdapter.ItemViewHolder>() {
    private val TAG = "ExistingAgentListAdapter"

    var agentList: ArrayList<ExistingAgentListData>
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.existing_agent_list_item, parent, false)
        val viewHolder = ItemViewHolder(itemView)
        viewHolder.setOnClickListener(object : ItemViewHolder.ClickListener {
            override fun onItemClick(view: View?, position: Int) {
                var agentList = agentList.get(position)
                val intent = Intent(context, UpdateExistingAgent::class.java)
                intent.putExtra("id", agentList.id)
                intent.putExtra("name", agentList.name)
                intent.putExtra("mobile", agentList.mobile)
                intent.putExtra("phonePe", agentList.phonePe)
                intent.putExtra("email", agentList.email)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context!!.startActivity(intent)
            }
        })
        return viewHolder
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        Log.d("TAG", "onBindViewHolder:" + agentList[position])

        holder.name.setText(agentList[position].name)
        holder.mobile.setText(agentList[position].mobile)
        holder.email.setText(agentList[position].email)
    }

    override fun getItemCount(): Int {
        return agentList.size
    }

    init {
        this.context = context
        this.agentList = agentLists
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val TAG = "ExistingAgentListAdapter"

        var name: TextView
        var mobile: TextView
        var email: TextView

        private var mClickListener: ClickListener? = null

        interface ClickListener {
            fun onItemClick(view: View?, position: Int)
        }

        fun setOnClickListener(clickListener: ClickListener?) {
            mClickListener = clickListener
        }

        init {
            name = itemView.findViewById(R.id.existingAgentName)
            mobile = itemView.findViewById(R.id.existingAgentMobile)
            email = itemView.findViewById(R.id.existingAgentEmail)

            itemView.setOnClickListener { view ->
                mClickListener!!.onItemClick(view, adapterPosition)
            }
        }
    }
}