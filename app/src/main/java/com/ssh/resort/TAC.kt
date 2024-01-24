package com.ssh.resort

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.ssh.appdataremotedb.HTTPDownload
import com.ssh.resort.adapter.TacAgentsAdapter
import com.ssh.resort.adapter.TransactionListAdapter
import com.ssh.resort.data.ExistingAgentListData
import com.ssh.resort.data.TacAgentsListData
import com.ssh.resort.data.TransactionListData
import java.lang.Exception
import java.util.Locale
import java.util.Objects

class TAC : AppCompatActivity() {

    private val TAG = "Clothing"

    var agentsGV: GridView? = null

    var agentsList: ArrayList<TacAgentsListData> = ArrayList()
    var adapter: TacAgentsAdapter? = null

    private val SPEECH_REQUEST_CODE = 1
    var searchAgentsEditText : EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tac)

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                getAgentDetailsFromServer()
            } else {
                Snackbar.make(getWindow().getDecorView().getRootView(), "No Internet Connection", Snackbar.LENGTH_LONG)
                    .setTextColor(Color.WHITE)
                    .setBackgroundTint(ContextCompat.getColor(this@TAC, R.color.colorAccent))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .show()
            }
        }

        agentsGV = findViewById(R.id.tacAgentsGridView)

        adapter = TacAgentsAdapter(this, agentsList)
        agentsGV!!.setAdapter(adapter)

        // Adding on item click listener for our grid view.
        agentsGV!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            /*val intent = Intent(this, ProductInfo::class.java)
            intent.putExtra("ProductID", clothingList[position].productID)
            intent.putExtra("Image", clothingList[position].image)
            intent.putExtra("ProductName", clothingList[position].productName)
            intent.putExtra("Category", clothingList[position].category)
            intent.putExtra("Price", clothingList[position].price)
            intent.putExtra("MRP", clothingList[position].mrp)
            intent.putExtra("Description1", clothingList[position].description1)
            intent.putExtra("Description2", clothingList[position].description2)
            intent.putExtra("Description3", clothingList[position].description3)
            intent.putExtra("Email", loginEmail)
            intent.putExtra("ProductStatus", clothingList[position].productStatus)
            intent.putExtra("ProductShopName", clothingList[position].productShopName)
            startActivity(intent)*/
        }

        //Search Products Using Edit Text
        searchAgentsEditText = findViewById(R.id.tacSearchViewAgents) as EditText
        searchAgentsEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                //SEARCH FILTER
                adapter!!.getFilter()!!.filter(charSequence)
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        //Search Products Using Voice Search
        var searchClothingVoice = findViewById(R.id.tacVoiceSearchAgents) as ImageView
        searchClothingVoice.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak To Text")
                try {
                    startActivityForResult(intent, SPEECH_REQUEST_CODE)
                }
                catch (e: Exception) {
                    Toast.makeText(this@TAC, " " + e.message, Toast.LENGTH_SHORT).show()
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
                agentsList.clear()

                for (i in 0 until agentResult.length()) {
                    val json_data = agentResult.getJSONObject(i)
                    val agentListData = TacAgentsListData(
                        json_data.getString("id"),
                        json_data.getString("name"),
                        json_data.getString("mobile"),
                        json_data.getString("phonePe"),
                        json_data.getString("email"))
                    agentsList.add(agentListData)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                searchAgentsEditText!!.setText(Objects.requireNonNull(result)?.get(0))
            }
        }
    }
}