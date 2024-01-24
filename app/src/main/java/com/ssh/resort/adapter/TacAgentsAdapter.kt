package com.ssh.resort.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.ssh.resort.R
import com.ssh.resort.data.TacAgentsListData

class TacAgentsAdapter(context: Context, agentList: ArrayList<TacAgentsListData>) : ArrayAdapter<TacAgentsListData>(context, 0, agentList),
    Filterable {

    private val TAG = "TacAgentsAdapter"

    private var agentLists = ArrayList<TacAgentsListData>()
    private var displayedAgentsLists = ArrayList<TacAgentsListData>()
    var cntx: Context? = null

    init {
        this.cntx = context
        agentLists = agentList
        displayedAgentsLists = agentLists
    }

    override fun getCount(): Int {
        Log.d(TAG, "getCount() returned: " + agentLists.size)
        return displayedAgentsLists.size
    }

    override fun getItem(position: Int): TacAgentsListData? {
        Log.d(TAG, "getItem: Position" + position)
        return displayedAgentsLists.get(position)
    }

    override fun getItemId(position: Int): Long {
        Log.d(TAG, "PositionItem" + position)
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listitemView = convertView
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(context).inflate(R.layout.tac_agents_list_items, parent, false)
        }
        val clothingData: TacAgentsListData? = getItem(position)
        val productName = listitemView!!.findViewById<TextView>(R.id.tacAgentsListName)
        productName.setText(clothingData!!.name)
        return listitemView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                displayedAgentsLists = results.values as ArrayList<TacAgentsListData> // has the filtered values
                notifyDataSetChanged() // notifies the data with new filtered values
            }

            override fun performFiltering(userText: CharSequence?): FilterResults? {
                var userText = userText
                val results = FilterResults()
                val filteredArrList: ArrayList<TacAgentsListData> = ArrayList<TacAgentsListData>()

                if (userText == null || userText.length == 0) {
                    // set the Original result to return
                    results.count = agentLists.size
                    results.values = agentLists
                } else {
                    userText = userText.toString().toLowerCase()
                    for (i in 0 until agentLists.size) {
                        val data: TacAgentsListData = agentLists.get(i)

                        if (data.name.toLowerCase().contains(userText)) {
                            filteredArrList.add(data)
                        }
                    }
                    // set the Filtered result to return
                    results.count = filteredArrList.size
                    results.values = filteredArrList
                }
                return results
            }
        }
    }
}