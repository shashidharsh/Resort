package com.ssh.resort

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Chunk
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.ssh.resort.data.TacAgentsTransactionListData
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.text.SimpleDateFormat
import java.util.Date

class PdfReports(context: Context) {
    val TITLE_FONT = Font(Font.FontFamily.TIMES_ROMAN, 18f, Font.BOLD)
    val BODY_FONT = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
    private lateinit var pdf: PdfWriter
    var cntxt: Context = context

    private fun createFile(): File {
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy h:mm a")
        val currentDateTime: String = simpleDateFormat.format(Date())
        //Prepare file
        val fileName = "Reports-" + currentDateTime + ".pdf"
        var path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/Resort"
        Log.d("Path", "createFile Path Below 10: " + path)
        val dir = File(path)
        if (!dir.exists())
            dir.mkdirs()
        val file = File(dir, fileName)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (invEx: InvocationTargetException) {
                invEx.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(cntxt, "File already exist", Toast.LENGTH_SHORT).show()
        }
        return file
    }

    private fun createDocument(): Document {
        //Create Document
        val document = Document()
        document.setMargins(24f, 24f, 32f, 32f)
        document.pageSize = PageSize.A4
        return document
    }

    private fun setupPdfWriter(document: Document, file: File) {
        pdf = PdfWriter.getInstance(document, FileOutputStream(file))
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
        val paragraph = Paragraph(content, BODY_FONT)
        paragraph.firstLineIndent = 25f
        //paragraph.alignment = Element.ALIGN_JUSTIFIED
        paragraph.alignment = Element.ALIGN_CENTER
        return paragraph
    }

    fun createUserTable(data: List<TacAgentsTransactionListData>, paragraphList: List<String>, onFinish: (file: File) -> Unit, onError: (Exception) -> Unit, fromDate: String, toDate: String) {
        //Define the document
        val file = createFile()
        val document = createDocument()

        //Setup PDF Writer
        setupPdfWriter(document, file)

        //Add Title
        document.add(Paragraph("Reports From " + fromDate + "  To " + toDate, TITLE_FONT))
        //Add Empty Line as necessary
        addLineSpace(document, 1)

        //Add paragraph
        paragraphList.forEach {content->
            val paragraph = createParagraph(content)
            document.add(paragraph)
        }
        addLineSpace(document, 1)
        //Add Empty Line as necessary

        //Add table title
        //document.add(Paragraph("User Data", TITLE_FONT))
        //addLineSpace(document, 1)

        //Define Table
        val CustName = 2f
        val noOfPersons = 2f
        val agentName = 2f
        val Package = 2f
        val activity = 2f
        val b2b = 2f
        val advance = 2f
        val balanceB2B = 2f
        val columnWidth = floatArrayOf(CustName, noOfPersons, agentName, Package, activity, b2b, advance, balanceB2B)
        val table = createTable(8, columnWidth)
        //Table header (first row)
        val tableHeaderContent = listOf("Cust Name", "No Of Person", "Agent", "Package", "Activity", "B2B", "Advance", "Bal B2B")
        //write table header into table
        tableHeaderContent.forEach {
            //define a cell
            val cell = createHeaderCell(it)
            //add our cell into our table
            table.addCell(cell)
        }
        //write user data into table
        data.forEach {
            //Write Each custName
            val custNameCell = createCell(it.guestName)
            table.addCell(custNameCell)
            //Write Each noOfPerson
            val noOfPersonCell = createCell(it.noOfPersons)
            table.addCell(noOfPersonCell)
            //Write Each Agent
            val agentCell = createCell(it.selectedCo)
            table.addCell(agentCell)
            //Write Each Package
            val packageCell = createCell(it.packageAdult)
            table.addCell(packageCell)
            //Write Each Activity
            val activityCell = createCell(it.selectedActivity)
            table.addCell(activityCell)
            //Write Each B2B
            val b2bCell = createCell(it.enterB2B)
            table.addCell(b2bCell)
            //Write Each Advance
            val advanceCell = createCell(it.advance)
            table.addCell(advanceCell)
            //Write Each Balance B2B
            val balanceB2BCell = createCell(it.balanceB2B)
            table.addCell(balanceB2BCell)
        }
        document.add(table)
        document.close()

        try {
            pdf.close()
        } catch (ex: Exception) {
            onError(ex)
        } finally {
            onFinish(file)
        }
    }

    //Create PDF above android 10
    fun createPDF(){
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy h:mm a")
        val currentDateTime: String = simpleDateFormat.format(Date())
        val fileName = "Reports-" + currentDateTime + ".pdf"

        val document = Document()
        val dest: String = cntxt.getExternalFilesDir(null).toString() + "/Resort"
        Log.d("PdfReports", "createPDF: " + dest)

        val dir = File(dest)
        if (!dir.exists())
            dir.mkdirs()

        try {
            val file = File(dest, fileName)
            file.createNewFile()
            val fOut = FileOutputStream(file, false)
            PdfWriter.getInstance(document, fOut)
        } catch (e: DocumentException) {
            e.printStackTrace()
            Log.v("PdfError", e.toString())
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.v("PdfError", e.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: DocumentException) {
            e.printStackTrace()
        }

        // Open to write
        document.open()
        document.add(Paragraph(""))
        document.add(Chunk("Hello"))
        document.close()

        val pdfFile = File("$dest/$fileName")
        if (!pdfFile.exists()) {
            pdfFile.mkdir()
        }

        if (pdfFile != null && pdfFile.exists()) //Checking for the file is exist or not
        {
            val intent = Intent(Intent.ACTION_VIEW)
            val mURI = FileProvider.getUriForFile(cntxt, cntxt.getApplicationContext().getPackageName() + ".provider", pdfFile)
            intent.setDataAndType(mURI, "application/pdf")
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                cntxt.startActivity(intent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(cntxt, "The file not exists! ", Toast.LENGTH_SHORT).show()
        }
    }
}