package com.ssh.resort

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class TransactionDetails : AppCompatActivity() {

    private val TAG = "TransactionDetails"

    var id : String? = ""
    var guestName : String? = ""
    var noOfPersons : String? = ""
    var noOfChildrens : String? = ""
    var packageAdult : String? = ""
    var packageChild : String? = ""
    var selectedCo : String? = ""
    var enterB2B : String? = ""
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
    var paymentType : String? = ""
    var cash : String? = ""
    var upi : String? = ""
    var cashStatus : String? = ""
    var upiStatus : String? = ""
    var date : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transaction_details)

        id = intent.getStringExtra("id")
        guestName = intent.getStringExtra("guestName")
        noOfPersons = intent.getStringExtra("noOfPersons")
        noOfChildrens = intent.getStringExtra("noOfChildrens")
        packageAdult = intent.getStringExtra("packageAdult")
        packageChild = intent.getStringExtra("packageChild")
        selectedCo = intent.getStringExtra("selectedCo")
        enterB2B = intent.getStringExtra("enterB2B")
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
        paymentType = intent.getStringExtra("paymentType")
        cash = intent.getStringExtra("cash")
        upi = intent.getStringExtra("upi")
        cashStatus = intent.getStringExtra("cashStatus")
        upiStatus = intent.getStringExtra("upiStatus")
        date = intent.getStringExtra("date")

        var tvGuestName = findViewById<TextView>(R.id.transactionDetailsGuestName)
        tvGuestName.setText(guestName)
        var tvTxnDate = findViewById<TextView>(R.id.transactionDetailsDate)
        tvTxnDate.setText(date)
        var tvNoOfPersons = findViewById<TextView>(R.id.transactionDetailsNoOfPersons)
        tvNoOfPersons.setText(noOfPersons)
        var tvNoOfChildrens = findViewById<TextView>(R.id.transactionDetailsNofChildrens)
        tvNoOfChildrens.setText(noOfChildrens)
        var tvPackageAdult = findViewById<TextView>(R.id.transactionDetailsPackageAdult)
        tvPackageAdult.setText(packageAdult)
        var tvPackageChild = findViewById<TextView>(R.id.transactionDetailsPackageChild)
        tvPackageChild.setText(packageChild)
        var tvCO = findViewById<TextView>(R.id.transactionDetailsCO)
        tvCO.setText(selectedCo)
        var tvB2B = findViewById<TextView>(R.id.transactionDetailsB2B)
        tvB2B.setText(enterB2B)
        var tvActivities = findViewById<TextView>(R.id.transactionDetailsActivities)
        tvActivities.setText(activities)
        var tvSelectedAcitvities = findViewById<TextView>(R.id.transactionDetailsSelectedActivity)
        tvSelectedAcitvities.setText(selectedActivity)
        var tvNoPersonActivity = findViewById<TextView>(R.id.transactionDetailsNoPersonActivity)
        tvNoPersonActivity.setText(noPersonActivity)
        var tvRoomNo = findViewById<TextView>(R.id.transactionDetailsRoomNumber)
        tvRoomNo.setText(roomNumber)
        var tvDriver = findViewById<TextView>(R.id.transactionDetailsDriver)
        tvDriver.setText(driver)
        var tvDriverCost = findViewById<TextView>(R.id.transactionDetailsDriverCost)
        tvDriverCost.setText(driverCost)
        var tvType = findViewById<TextView>(R.id.transactionDetailsType)
        tvType.setText(type)
        var tvPartner = findViewById<TextView>(R.id.transactionDetailsPartner)
        tvPartner.setText(partner)
        var tvTotalB2B = findViewById<TextView>(R.id.transactionDetailsTotalB2B)
        tvTotalB2B.setText(totB2B)
        var tvTotalActivityPrice = findViewById<TextView>(R.id.transactionDetailsTotalActivityPrice)
        tvTotalActivityPrice.setText(totActivityPrice)
        var tvTAC = findViewById<TextView>(R.id.transactionDetailsTAC)
        tvTAC.setText(tac)
        var tvTotal = findViewById<TextView>(R.id.transactionDetailsTotal)
        tvTotal.setText(total)
        var tvPaymentType = findViewById<TextView>(R.id.transactionDetailsPaymentType)
        tvPaymentType.setText(paymentType)
        var tvCash = findViewById<TextView>(R.id.transactionDetailsCash)
        tvCash.setText(cash)
        var tvUPI = findViewById<TextView>(R.id.transactionDetailsUPI)
        tvUPI.setText(upi)
        var tvCashStatus = findViewById<TextView>(R.id.transactionDetailsCashStatus)
        tvCashStatus.setText(cashStatus)
        var tvUpiStatus = findViewById<TextView>(R.id.transactionDetailsUPIStatus)
        tvUpiStatus.setText(upiStatus)
    }
}