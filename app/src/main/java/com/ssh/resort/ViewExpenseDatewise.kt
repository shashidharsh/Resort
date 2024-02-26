package com.ssh.resort

import android.app.DatePickerDialog
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
import android.widget.TextView
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ViewExpenseDatewise : AppCompatActivity() {

    private val TAG = "ViewExpenseDatewise"

    var datewiseExpenseListRecyclerView: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var adapter: ExpenseListAdapter? = null

    val expenseListDate: ArrayList<ExpenseListData> = ArrayList()

    var tvFromDate: TextView? = null
    var tvToDate: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_expense_datewise)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        //Set From Date and End Date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        tvFromDate = findViewById(R.id.reportsFromDate) as TextView
        tvToDate = findViewById(R.id.reportsToDate) as TextView

        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val currentDate: String = simpleDateFormat.format(Date())
        tvFromDate!!.text = currentDate
        tvToDate!!.text = currentDate

        //Button From Date
        tvFromDate!!.setOnClickListener {
            val datePickerDialog =
                DatePickerDialog(this@ViewExpenseDatewise, DatePickerDialog.OnDateSetListener
                { view, year, monthOfYear, dayOfMonth ->
                    tvFromDate!!.setText("" + String.format("%02d", dayOfMonth) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year)
                }, year, month, day)
            datePickerDialog.show()
        }

        //Button To Date
        tvToDate!!.setOnClickListener {
            val datePickerDialog =
                DatePickerDialog(this@ViewExpenseDatewise, DatePickerDialog.OnDateSetListener
                { view, year, monthOfYear, dayOfMonth ->
                    tvToDate!!.setText("" + String.format("%02d", dayOfMonth) + "-" + String.format("%02d", monthOfYear + 1) + "-" + year)
                }, year, month, day)
            datePickerDialog.show()
        }

        //RecyclerView
        datewiseExpenseListRecyclerView = findViewById(R.id.rvDatewiseExpenseList)
        datewiseExpenseListRecyclerView!!.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        datewiseExpenseListRecyclerView!!.setLayoutManager(layoutManager)

        adapter = ExpenseListAdapter(this, expenseListDate)
        datewiseExpenseListRecyclerView!!.adapter = adapter

        var showExpense = findViewById<Button>(R.id.showExpences)
        showExpense.setOnClickListener{
            getExpenseDetailsDateRange()
        }

        var generatePDF = findViewById<Button>(R.id.expensesDatewiseGeneratePDF)
        generatePDF.setOnClickListener{
            createPdf()
        }
    }

    //Download Expense Details from Server with date range
    fun getExpenseDetailsDateRange() {
        Log.d(TAG, "getExpenseDetailsDateRange")

        var pd = Dialog(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.progress, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.setCancelable(false)

        class ExpenseDetails : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {

                var fromDateFormat = SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("dd-MM-yyyy").parse(tvFromDate!!.text.toString()))
                Log.d(TAG, "fromDateFormat: " + fromDateFormat)

                var toDateFormat = SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("dd-MM-yyyy").parse(tvToDate!!.text.toString()))
                Log.d(TAG, "toDateFormat: " + toDateFormat)

                val transactionDetailsUrl = "https://hillstoneresort.com/Resorts/GetExpenseDatewise.php?from=" + fromDateFormat + "&to=" + toDateFormat

                var httpDownload = HTTPDownload()
                val httpStatus = httpDownload.downloadUrl(transactionDetailsUrl)

                if(httpStatus.status != HTTPDownload.STATUS_SUCCESS) {
                    Log.d(TAG, "getExpenseDetailsDateRange jsonojb null" + httpStatus.status)
                    return false
                }

                val expenseResult = httpStatus.value!!.optJSONArray("results")
                if (expenseResult == null){
                    return false
                }
                expenseListDate.clear()

                for (i in 0 until expenseResult.length()) {
                    val json_data = expenseResult.getJSONObject(i)
                    val expenseListData = ExpenseListData(
                        json_data.getString("id"),
                        json_data.getString("expenseAmount"),
                        json_data.getString("expenseReason"),
                        json_data.getString("expenseBy"),
                        json_data.getString("date"))
                    expenseListDate.add(expenseListData)
                    Log.d(TAG, "getExpenseDetailsDateRange expenseListData: " + expenseListData)
                }
                return true
            }

            override fun onPreExecute() {
                super.onPreExecute()
                pd.show()
            }

            override fun onPostExecute(result: Boolean) {
                super.onPostExecute(result)
                Log.d(TAG, "getExpenseDetailsDateRange onPostExecute result:" + result)
                pd.cancel()
                if (result == false) {
                    Toast.makeText(applicationContext, "$result", Toast.LENGTH_SHORT).show()
                }
                else {
                    if (expenseListDate.size == 0){
                        Snackbar.make(getWindow().getDecorView().getRootView(), "No Expenses", Snackbar.LENGTH_LONG)
                            .setTextColor(Color.WHITE)
                            .setBackgroundTint(ContextCompat.getColor(this@ViewExpenseDatewise, R.color.colorAccent))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .show()
                    } else{
                        //Total Expense
                        var totalExpense = findViewById<TextView>(R.id.expensesDatewiseTotalExpense)
                        var totExpense = adapter!!.totalExpense()
                        totalExpense!!.setText(totExpense)

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
        val pdfService = PdfExpenseDatewise()
        pdfService.createUserTable(data = expenseListDate, paragraphList = paragraphList, onFinish = onFinish, onError = onError, tvFromDate!!.text.toString(), tvToDate!!.text.toString())
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