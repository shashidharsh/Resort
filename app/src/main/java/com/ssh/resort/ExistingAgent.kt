package com.ssh.resort

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssh.appdataremotedb.HTTPDownload
import com.ssh.resort.adapter.ExistingAgentListAdapter
import com.ssh.resort.data.ExistingAgentListData

class ExistingAgent : AppCompatActivity() {

    private val TAG = "ExistingAgent"

    var agentListRecyclerView: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var adapter: ExistingAgentListAdapter? = null

    val agentList: ArrayList<ExistingAgentListData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.existing_agent)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        getAgentDetailsFromServer()

        //RecyclerView
        agentListRecyclerView = findViewById(R.id.rvExistingAgent)
        agentListRecyclerView!!.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        agentListRecyclerView!!.setLayoutManager(layoutManager)

        adapter = ExistingAgentListAdapter(applicationContext, agentList)
        agentListRecyclerView!!.adapter = adapter
    }

    //Download Agent Details from Server
    fun getAgentDetailsFromServer() {
        Log.d(TAG, "getAgentDetailsFromServer")

        var pd = Dialog(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.progress, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.setCancelable(false)

        class AgentDetails : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {

                val agentDetailsUrl = "https://sshsoftwares.in/Resort/GetAgents.php"

                var httpDownload = HTTPDownload()
                val httpStatus = httpDownload.downloadUrl(agentDetailsUrl)

                if(httpStatus.status != HTTPDownload.STATUS_SUCCESS) {
                    Log.d(TAG, "getAgentDetailsFromServer jsonojb null" + httpStatus.status)
                    return false
                }

                val agentResult = httpStatus.value!!.optJSONArray("results")
                if (agentResult == null){
                    return false
                }
                agentList.clear()

                for (i in 0 until agentResult.length()) {
                    val json_data = agentResult.getJSONObject(i)
                    val agentListData = ExistingAgentListData(
                        json_data.getString("id"),
                        json_data.getString("name"),
                        json_data.getString("mobile"),
                        json_data.getString("phonePe"),
                        json_data.getString("email"))
                    agentList.add(agentListData)
                    Log.d(TAG, "getAgentDetailsFromServer agentData: " + agentListData)
                }
                return true
            }

            override fun onPreExecute() {
                super.onPreExecute()
                pd.show()
            }

            override fun onPostExecute(result: Boolean) {
                super.onPostExecute(result)
                Log.d(TAG, "getAgentDetailsFromServer onPostExecute result:" + result)
                pd.cancel()
                if (result == false) {
                    Toast.makeText(applicationContext, "$result", Toast.LENGTH_SHORT).show()
                }
                else {
                    adapter!!.notifyDataSetChanged()
                }
            }
        }
        var result = AgentDetails().execute()
    }

    override fun onResume() {
        super.onResume()
        getAgentDetailsFromServer()
    }
}