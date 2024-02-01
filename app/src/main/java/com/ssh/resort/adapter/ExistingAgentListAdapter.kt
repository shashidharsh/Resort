package com.ssh.resort.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssh.resort.R
import com.ssh.resort.UpdateExistingAgent
import com.ssh.resort.data.ExistingAgentListData

class ExistingAgentListAdapter(context: Context, agentLists : ArrayList<ExistingAgentListData>) : RecyclerView.Adapter<ExistingAgentListAdapter.ItemViewHolder>(),
    Filterable {
    private val TAG = "ExistingAgentListAdapter"

    var agentList: ArrayList<ExistingAgentListData>
    private var displayedaAgentList = ArrayList<ExistingAgentListData>()
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
                intent.putExtra("upi_id", agentList.upi_id)
                intent.putExtra("email", agentList.email)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context!!.startActivity(intent)
            }
        })
        return viewHolder
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        Log.d("TAG", "onBindViewHolder:" + displayedaAgentList[position])

        val firstCharacter = displayedaAgentList[position].name
        val firstChar = firstCharacter[0]
        holder.firstChar.setText(firstChar.toString())

        holder.name.setText(displayedaAgentList[position].name)
        holder.mobile.setText(displayedaAgentList[position].mobile)
        holder.email.setText(displayedaAgentList[position].email)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getCount() returned: " + displayedaAgentList.size)
        return displayedaAgentList.size
    }

    override fun getItemId(position: Int): Long {
        Log.d(TAG, "PositionItem" + position)
        return position.toLong()
    }

    init {
        this.context = context
        this.agentList = agentLists
        displayedaAgentList = agentList
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val TAG = "ExistingAgentListAdapter"

        var firstChar: Button
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
            firstChar = itemView.findViewById(R.id.agentListItemFirstCharacter)
            name = itemView.findViewById(R.id.existingAgentName)
            mobile = itemView.findViewById(R.id.existingAgentMobile)
            email = itemView.findViewById(R.id.existingAgentEmail)

            itemView.setOnClickListener { view ->
                mClickListener!!.onItemClick(view, adapterPosition)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) displayedaAgentList = agentList
                else {
                    val filteredList = ArrayList<ExistingAgentListData>()
                    agentList
                        .filter {
                            (it.name.toLowerCase().contains(constraint!!)) or (it.mobile.contains(constraint)) or (it.name.contains(constraint))
                        }
                        .forEach { filteredList.add(it) }
                    displayedaAgentList = filteredList

                }
                return FilterResults().apply { values = displayedaAgentList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                displayedaAgentList = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<ExistingAgentListData>
                notifyDataSetChanged()
            }
        }
    }
}