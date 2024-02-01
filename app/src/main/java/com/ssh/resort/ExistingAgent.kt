package com.ssh.resort

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.ssh.appdataremotedb.HTTPDownload
import com.ssh.resort.adapter.ExistingAgentListAdapter
import com.ssh.resort.data.ExistingAgentListData
import java.lang.Exception
import java.util.Locale

class ExistingAgent : AppCompatActivity() {

    private val TAG = "ExistingAgent"

    var agentListRecyclerView: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var adapter: ExistingAgentListAdapter? = null

    val agentList: ArrayList<ExistingAgentListData> = ArrayList()

    private val SPEECH_REQUEST_CODE = 1
    var searchCustomerEditText : EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.existing_agent)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                getAgentDetailsFromServer()
            } else {
                Snackbar.make(getWindow().getDecorView().getRootView(), "No Internet Connection", Snackbar.LENGTH_LONG)
                    .setTextColor(Color.WHITE)
                    .setBackgroundTint(ContextCompat.getColor(this@ExistingAgent, R.color.colorAccent))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .show()
            }
        }

        //RecyclerView
        agentListRecyclerView = findViewById(R.id.rvExistingAgent)
        agentListRecyclerView!!.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        agentListRecyclerView!!.setLayoutManager(layoutManager)

        adapter = ExistingAgentListAdapter(applicationContext, agentList)
        agentListRecyclerView!!.adapter = adapter

        //Search Agent
        searchCustomerEditText = findViewById(R.id.searchViewAgent) as EditText
        searchCustomerEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                //SEARCH FILTER
                adapter!!.getFilter()!!.filter(charSequence)
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        //Search Agent Using Voice Search
        var searchCustomerVoice = findViewById(R.id.ivVoiceSearchAgent) as ImageView
        searchCustomerVoice.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak To Text")
                try {
                    startActivityForResult(intent, SPEECH_REQUEST_CODE)
                }
                catch (e: Exception) {
                    Toast.makeText(this@ExistingAgent, " " + e.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
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

                val agentDetailsUrl = "https://hillstoneresort.com/Resorts/GetAgents.php"

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
                        json_data.getString("upi_id"),
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                searchCustomerEditText!!.setText(java.util.Objects.requireNonNull(result)?.get(0))
            }
        }
    }
}