package com.ssh.resort

import android.os.Environment
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
import com.ssh.resort.data.ReportsListData
import com.ssh.resort.data.TacAgentsTransactionListData
import com.ssh.resort.data.TransactionListData
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class PdfService {
    val TITLE_FONT = Font(Font.FontFamily.TIMES_ROMAN, 18f, Font.BOLD)
    val BODY_FONT = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
    private lateinit var pdf: PdfWriter

    private fun createFile(): File {
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy h:mm a")
        val currentDateTime: String = simpleDateFormat.format(Date())
        //Prepare file
        val title = "Reports-" + currentDateTime + ".pdf"
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(path, title)
        if (!file.exists()) file.createNewFile()
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
            val balanceB2BCell = createCell(it.totalB2B)
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
}