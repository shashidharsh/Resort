package com.ssh.resort

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
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

    private lateinit var pdf: PdfWriter

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
            if (expenseListDate.size == 0){
                Snackbar.make(getWindow().getDecorView().getRootView(), "No Expenses Select Other Date", Snackbar.LENGTH_LONG)
                    .setTextColor(Color.WHITE)
                    .setBackgroundTint(ContextCompat.getColor(this@ViewExpenseDatewise, R.color.colorAccent))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .show()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val paragraphList = listOf(getString(R.string.paragraph1), getString(R.string.paragraph2))
                    createUserTable(data = expenseListDate, paragraphList = paragraphList, tvFromDate!!.text.toString(), tvToDate!!.text.toString())
                } else {
                    createPdf()
                    Toast.makeText(this@ViewExpenseDatewise, "PDF Generated Successfully", Toast.LENGTH_SHORT).show()
                }
            }
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

                val expenseDatewiseUrl = Constants.BASE_URL + "GetExpenseDatewise.php?from=" + fromDateFormat + "&to=" + toDateFormat

                var httpDownload = HTTPDownload()
                val httpStatus = httpDownload.downloadUrl(expenseDatewiseUrl)

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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun createUserTable(data: List<ExpenseListData>, paragraphList: List<String>, fromDate: String, toDate: String) {
        //Define the document
        val file = saveFileToExternalStorage()
        val document = createDocument()

        //Setup PDF Writer
        setupPdfWriter(document, file!!)

        //Add Title
        document.add(Paragraph("Expenses From " + fromDate + "  To " + toDate, Font(Font.FontFamily.TIMES_ROMAN, 18f, Font.BOLD)))
        //Add Empty Line as necessary
        addLineSpace(document, 1)

        //Add paragraph
        paragraphList.forEach {content->
            val paragraph = createParagraph(content)
            document.add(paragraph)
        }
        addLineSpace(document, 1)
        //Add Empty Line as necessary

        //Define Table
        val expenseReason = 2f
        val expenseAmount = 2f
        val expenseBy = 2f
        val date = 2f
        val columnWidth = floatArrayOf(expenseReason, expenseAmount, expenseBy, date)
        val table = createTable(4, columnWidth)
        //Table header (first row)
        val tableHeaderContent = listOf("Expense Reason", "Amount", "Expense By", "Date")
        //write table header into table
        tableHeaderContent.forEach {
            //define a cell
            val cell = createHeaderCell(it)
            //add our cell into our table
            table.addCell(cell)
        }
        //write user data into table
        data.forEach {
            //Write Each expenseReasonCell
            val expenseReasonCell = createCell(it.expenseReason)
            table.addCell(expenseReasonCell)
            //Write Each expenseAmountCell
            val expenseAmountCell = createCell(it.expenseAmount)
            table.addCell(expenseAmountCell)
            //Write Each expenseByCell
            val expenseByCell = createCell(it.expenseBy)
            table.addCell(expenseByCell)
            //Write Each expenseDateCell
            val expenseDateCell = createCell(it.date)
            table.addCell(expenseDateCell)
        }
        document.add(table)
        document.close()
        Toast.makeText(this, "PDF Generated Successfully", Toast.LENGTH_SHORT).show()
        openPdfFileAboveAndroid10(file)

        try {
            pdf.close()
        } catch (ex: Exception) {
            Log.d(TAG, "createUserTable Ex: " + ex)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileToExternalStorage() : Uri? {
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy h:mm a")
        val currentDateTime: String = simpleDateFormat.format(Date())
        //File Name
        val fileName = "Reports-" + currentDateTime

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/pdf")
        contentValues.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Resort")
        val fileUri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        return fileUri
    }

    private fun createDocument(): Document {
        //Create Document
        val document = Document()
        document.setMargins(24f, 24f, 32f, 32f)
        document.pageSize = PageSize.A4
        return document
    }

    private fun setupPdfWriter(document: Document, fileUri: Uri) {
        val outputStream = contentResolver.openOutputStream(fileUri)
        pdf = PdfWriter.getInstance(document, outputStream)
        pdf.setFullCompression()
        //Open the document
        document.open()
    }

    private fun createTable(column: Int, columnWidth: FloatArray): PdfPTable {
        val table = PdfPTable(column)
        table.widthPercentage = 100f
        table.setWidths(columnWidth)
        table.headerRows = 1
        table.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
        return table
    }

    private fun createCell(content: String): PdfPCell {
        val cell = PdfPCell(Phrase(content))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.verticalAlignment = Element.ALIGN_MIDDLE
        //setup padding
        cell.setPadding(8f)
        cell.isUseAscender = true
        cell.paddingLeft = 4f
        cell.paddingRight = 4f
        cell.paddingTop = 8f
        cell.paddingBottom = 8f
        return cell
    }

    private fun createHeaderCell(content: String): PdfPCell {
        val cell = PdfPCell(Phrase(content))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.verticalAlignment = Element.ALIGN_MIDDLE
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY)
        //setup padding
        cell.setPadding(8f)
        cell.isUseAscender = true
        cell.paddingLeft = 4f
        cell.paddingRight = 4f
        cell.paddingTop = 8f
        cell.paddingBottom = 8f
        return cell
    }

    private fun addLineSpace(document: Document, number: Int) {
        for (i in 0 until number) {
            document.add(Paragraph(" "))
        }
    }

    private fun createParagraph(content: String): Paragraph {
        val paragraph = Paragraph(content, Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL))
        paragraph.firstLineIndent = 25f
        paragraph.alignment = Element.ALIGN_CENTER
        return paragraph
    }

    private fun openPdfFileAboveAndroid10(fileUri: Uri) {
        val path = FileHandler().getPathFromUri(this, fileUri)
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
}