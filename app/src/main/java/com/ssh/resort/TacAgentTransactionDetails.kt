package com.ssh.resort

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class TacAgentTransactionDetails : AppCompatActivity() {

    private val TAG = "TacAgentTransactionDetails"

    var id : String? = ""
    var guestName : String? = ""
    var noOfPersons : String? = ""
    var noOfChildrens : String? = ""
    var packageAdult : String? = ""
    var packageChild : String? = ""
    var selectedCo : String? = ""
    var selectedCoMobile : String? = ""
    var enterB2B : String? = ""
    var advance : String? = ""
    var activities : String? = ""
    var selectedActivity : String? = ""
    var noPersonActivity : String? = ""
    var roomNumber : String? = ""
    var driver : String? = ""
    var driverCost : String? = ""
    var type : String? = ""
    var partner : String? = ""
    var totB2B : String? = ""
    var totActivityPrice : String? = ""
    var tac : String? = ""
    var total : String? = ""
    var grandTotal : String? = ""
    var paymentType : String? = ""
    var cash : String? = ""
    var upi : String? = ""
    var cashStatus : String? = ""
    var upiStatus : String? = ""
    var dateTime : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tac_agent_transaction_details)

        id = intent.getStringExtra("id")
        guestName = intent.getStringExtra("guestName")
        noOfPersons = intent.getStringExtra("noOfPersons")
        noOfChildrens = intent.getStringExtra("noOfChildrens")
        packageAdult = intent.getStringExtra("packageAdult")
        packageChild = intent.getStringExtra("packageChild")
        selectedCo = intent.getStringExtra("selectedCo")
        selectedCoMobile = intent.getStringExtra("selectedCoMobile")
        enterB2B = intent.getStringExtra("enterB2B")
        advance = intent.getStringExtra("advance")
        activities = intent.getStringExtra("activities")
        selectedActivity = intent.getStringExtra("selectedActivity")
        noPersonActivity = intent.getStringExtra("noPersonActivity")
        roomNumber = intent.getStringExtra("roomNumber")
        driver = intent.getStringExtra("driver")
        driverCost = intent.getStringExtra("driverCost")
        type = intent.getStringExtra("type")
        partner = intent.getStringExtra("partner")
        totB2B = intent.getStringExtra("totB2B")
        totActivityPrice = intent.getStringExtra("totActivityPrice")
        tac = intent.getStringExtra("tac")
        total = intent.getStringExtra("total")
        grandTotal = intent.getStringExtra("grandTotal")
        paymentType = intent.getStringExtra("paymentType")
        cash = intent.getStringExtra("cash")
        upi = intent.getStringExtra("upi")
        cashStatus = intent.getStringExtra("cashStatus")
        upiStatus = intent.getStringExtra("upiStatus")
        dateTime = intent.getStringExtra("dateTime")

        var tvGuestName = findViewById<TextView>(R.id.tacAgentTransactionDetailsGuestName)
        tvGuestName.setText(guestName)
        var tvTxnDateTime = findViewById<TextView>(R.id.tacAgentTransactionDetailsDate)
        tvTxnDateTime.setText(dateTime)
        var tvNoOfPersons = findViewById<TextView>(R.id.tacAgentTransactionDetailsNoOfPersons)
        tvNoOfPersons.setText(noOfPersons)
        var tvNoOfChildrens = findViewById<TextView>(R.id.tacAgentTransactionDetailsNofChildrens)
        tvNoOfChildrens.setText(noOfChildrens)
        var tvPackageAdult = findViewById<TextView>(R.id.tacAgentTransactionDetailsPackageAdult)
        tvPackageAdult.setText(packageAdult)
        var tvPackageChild = findViewById<TextView>(R.id.tacAgentTransactionDetailsPackageChild)
        tvPackageChild.setText(packageChild)
        var tvCO = findViewById<TextView>(R.id.tacAgentTransactionDetailsCO)
        tvCO.setText(selectedCo)
        var tvCoMobile = findViewById<TextView>(R.id.tacAgentTransactionDetailsCoMobile)
        tvCoMobile.setText(selectedCoMobile)
        var tvB2B = findViewById<TextView>(R.id.tacAgentTransactionDetailsB2B)
        tvB2B.setText(enterB2B)
        var tvAdvance = findViewById<TextView>(R.id.tacAgentTransactionDetailsAdvance)
        tvAdvance.setText(advance)
        var tvActivities = findViewById<TextView>(R.id.tacAgentTransactionDetailsActivities)
        tvActivities.setText(activities)
        var tvSelectedAcitvities = findViewById<TextView>(R.id.tacAgentTransactionDetailsSelectedActivity)
        tvSelectedAcitvities.setText(selectedActivity)
        var tvNoPersonActivity = findViewById<TextView>(R.id.tacAgentTransactionDetailsNoPersonActivity)
        tvNoPersonActivity.setText(noPersonActivity)
        var tvRoomNo = findViewById<TextView>(R.id.tacAgentTransactionDetailsRoomNumber)
        tvRoomNo.setText(roomNumber)
        var tvDriver = findViewById<TextView>(R.id.tacAgentTransactionDetailsDriver)
        tvDriver.setText(driver)
        var tvDriverCost = findViewById<TextView>(R.id.tacAgentTransactionDetailsDriverCost)
        tvDriverCost.setText(driverCost)
        var tvType = findViewById<TextView>(R.id.tacAgentTransactionDetailsType)
        tvType.setText(type)
        var tvPartner = findViewById<TextView>(R.id.tacAgentTransactionDetailsPartner)
        tvPartner.setText(partner)
        var tvTotalB2B = findViewById<TextView>(R.id.tacAgentTransactionDetailsTotalB2B)
        tvTotalB2B.setText(totB2B)
        var tvTotalActivityPrice = findViewById<TextView>(R.id.tacAgentTransactionDetailsTotalActivityPrice)
        tvTotalActivityPrice.setText(totActivityPrice)
        var tvTAC = findViewById<TextView>(R.id.tacAgentTransactionDetailsTAC)
        tvTAC.setText(tac)
        var tvTotal = findViewById<TextView>(R.id.tacAgentTransactionDetailsTotal)
        tvTotal.setText(total)
        var tvGrandTotal = findViewById<TextView>(R.id.tacAgentTransactionDetailsGrandTotal)
        tvGrandTotal.setText(grandTotal)
        var tvPaymentType = findViewById<TextView>(R.id.tacAgentTransactionDetailsPaymentType)
        tvPaymentType.setText(paymentType)
        var tvCash = findViewById<TextView>(R.id.tacAgentTransactionDetailsCash)
        tvCash.setText(cash)
        var tvUPI = findViewById<TextView>(R.id.tacAgentTransactionDetailsUPI)
        tvUPI.setText(upi)
        var tvCashStatus = findViewById<TextView>(R.id.tacAgentTransactionDetailsCashStatus)
        tvCashStatus.setText(cashStatus)
        var tvUpiStatus = findViewById<TextView>(R.id.tacAgentTransactionDetailsUPIStatus)
        tvUpiStatus.setText(upiStatus)

        var payAmount = findViewById<Button>(R.id.tacAgentTransactionDetailsPayAmount)
        payAmount.setText(grandTotal)
    }
}