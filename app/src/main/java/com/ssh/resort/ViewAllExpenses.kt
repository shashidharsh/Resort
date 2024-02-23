package com.ssh.resort

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.ssh.appdataremotedb.HTTPDownload
import com.ssh.resort.adapter.ExpenseListAdapter
import com.ssh.resort.data.ExpenseListData
import java.io.File

class ViewAllExpenses : AppCompatActivity() {

    private val TAG = "ViewAllExpenses"

    var allExpenseListRecyclerView: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var adapter: ExpenseListAdapter? = null

    val allExpenseList: ArrayList<ExpenseListData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_all_expenses)

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                getAllExpenseDetails()
            } else {
                Snackbar.make(getWindow().getDecorView().getRootView(), "No Internet Connection", Snackbar.LENGTH_LONG)
                    .setTextColor(Color.WHITE)
                    .setBackgroundTint(ContextCompat.getColor(this@ViewAllExpenses, R.color.colorAccent))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .show()
            }
        }

        //RecyclerView
        allExpenseListRecyclerView = findViewById(R.id.rvAllExpenseList)
        allExpenseListRecyclerView!!.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        allExpenseListRecyclerView!!.setLayoutManager(layoutManager)

        adapter = ExpenseListAdapter(this, allExpenseList)
        allExpenseListRecyclerView!!.adapter = adapter

        var generatePDF = findViewById<Button>(R.id.allExpensesGeneratePDF)
        generatePDF.setOnClickListener{
            createPdf()
        }
    }

    //Download All Expense Details from Server
    fun getAllExpenseDetails() {
        Log.d(TAG, "getAllExpenseDetails")

        var pd = Dialog(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.progress, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.setCancelable(false)

        class ExpenseDetails : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {

                val transactionDetailsUrl = "https://hillstoneresort.com/Resorts/GetAllExpenses.php"

                var httpDownload = HTTPDownload()
                val httpStatus = httpDownload.downloadUrl(transactionDetailsUrl)

                if(httpStatus.status != HTTPDownload.STATUS_SUCCESS) {
                    Log.d(TAG, "getAllExpenseDetails jsonojb null" + httpStatus.status)
                    return false
                }

                val expenseResult = httpStatus.value!!.optJSONArray("results")
                if (expenseResult == null){
                    return false
                }
                allExpenseList.clear()

                for (i in 0 until expenseResult.length()) {
                    val json_data = expenseResult.getJSONObject(i)
                    val expenseListData = ExpenseListData(
                        json_data.getString("id"),
                        json_data.getString("expenseAmount"),
                        json_data.getString("expenseReason"),
                        json_data.getString("expenseBy"),
                        json_data.getString("date"))
                    allExpenseList.add(expenseListData)
                    Log.d(TAG, "getAllExpenseDetails expenseListData: " + expenseListData)
                }
                return true
            }

            override fun onPreExecute() {
                super.onPreExecute()
                pd.show()
            }

            override fun onPostExecute(result: Boolean) {
                super.onPostExecute(result)
                Log.d(TAG, "getAllExpenseDetails onPostExecute result:" + result)
                pd.cancel()
                if (result == false) {
                    Toast.makeText(applicationContext, "$result", Toast.LENGTH_SHORT).show()
                }
                else {
                    if (allExpenseList.size == 0){
                        Snackbar.make(getWindow().getDecorView().getRootView(), "No Expenses", Snackbar.LENGTH_LONG)
                            .setTextColor(Color.WHITE)
                            .setBackgroundTint(ContextCompat.getColor(this@ViewAllExpenses, R.color.colorAccent))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .show()
                    } else{
                        adapter!!.notifyDataSetChanged()
                    }
                }
            }
        }
        var result = ExpenseDetails().execute()
    }

    private fun createPdf() {
        val onError: (Exception) -> Unit = { toastErrorMessage(it.message.toString()) }
        val onFinish: (File) -> Unit = { openFile(it) }
        val paragraphList = listOf(getString(R.string.paragraph1), getString(R.string.paragraph2))
        val pdfService = PdfAllExpenses()
        pdfService.createUserTable(data = allExpenseList, paragraphList = paragraphList, onFinish = onFinish, onError = onError)
    }

    private fun openFile(file: File) {
        val path = FileHandler().getPathFromUri(this, file.toUri())
        val pdfFile = File(path)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(pdfFile.toUri(), "application/pdf")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        try {
            startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            toastErrorMessage("Can't read pdf file")
        }
    }

    private fun toastErrorMessage(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
}