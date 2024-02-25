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
    var selectedCoMobile : String? = ""
    var enterB2B : String? = ""
    var advance : String? = ""
    var activities : String? = ""
    var selectedActivity : String? = ""
    var noPersonActivity : String? = ""
    var activityPaymentStatus : String? = ""
    var roomNumber : String? = ""
    var driver : String? = ""
    var driverCost : String? = ""
    var type : String? = ""
    var partner : String? = ""
    var totB2B : String? = ""
    var balB2B : String? = ""
    var totActivityPrice : String? = ""
    var tac : String? = ""
    var balTAC : String? = ""
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
        setContentView(R.layout.transaction_details)

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
        activityPaymentStatus = intent.getStringExtra("activityPaymentStatus")
        roomNumber = intent.getStringExtra("roomNumber")
        driver = intent.getStringExtra("driver")
        driverCost = intent.getStringExtra("driverCost")
        type = intent.getStringExtra("type")
        partner = intent.getStringExtra("partner")
        totB2B = intent.getStringExtra("totB2B")
        balB2B = intent.getStringExtra("balanceB2B")
        totActivityPrice = intent.getStringExtra("totActivityPrice")
        tac = intent.getStringExtra("tac")
        balTAC = intent.getStringExtra("balanceTAC")
        total = intent.getStringExtra("total")
        grandTotal = intent.getStringExtra("grandTotal")
        paymentType = intent.getStringExtra("paymentType")
        cash = intent.getStringExtra("cash")
        upi = intent.getStringExtra("upi")
        cashStatus = intent.getStringExtra("cashStatus")
        upiStatus = intent.getStringExtra("upiStatus")
        dateTime = intent.getStringExtra("dateTime")

        var tvGuestName = findViewById<TextView>(R.id.transactionDetailsGuestName)
        tvGuestName.setText(guestName)
        var tvTxnDateTime = findViewById<TextView>(R.id.transactionDetailsDate)
        tvTxnDateTime.setText(dateTime)
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
        var tvCoMobile = findViewById<TextView>(R.id.transactionDetailsCoMobile)
        tvCoMobile.setText(selectedCoMobile)
        var tvB2B = findViewById<TextView>(R.id.transactionDetailsB2B)
        tvB2B.setText(enterB2B)
        var tvAdvance = findViewById<TextView>(R.id.transactionDetailsAdavnce)
        tvAdvance.setText(advance)
        var tvActivities = findViewById<TextView>(R.id.transactionDetailsActivities)
        tvActivities.setText(activities)
        var tvSelectedAcitvities = findViewById<TextView>(R.id.transactionDetailsSelectedActivity)
        tvSelectedAcitvities.setText(selectedActivity)
        var tvNoPersonActivity = findViewById<TextView>(R.id.transactionDetailsNoPersonActivity)
        tvNoPersonActivity.setText(noPersonActivity)
        var tvActivityPaymentStatus = findViewById<TextView>(R.id.transactionDetailsActivityPaymentStatus)
        tvActivityPaymentStatus.setText(activityPaymentStatus)
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
        var balanceB2B = findViewById<TextView>(R.id.transactionDetailsBalanceB2B)
        balanceB2B.setText(balB2B)
        var tvTotalActivityPrice = findViewById<TextView>(R.id.transactionDetailsTotalActivityPrice)
        tvTotalActivityPrice.setText(totActivityPrice)
        var tvTAC = findViewById<TextView>(R.id.transactionDetailsTAC)
        tvTAC.setText(tac)
        var balanceTAC = findViewById<TextView>(R.id.transactionDetailsBalanceTAC)
        balanceTAC.setText(balTAC)
        var tvTotal = findViewById<TextView>(R.id.transactionDetailsTotal)
        tvTotal.setText(total)
        var tvGrandTotal = findViewById<TextView>(R.id.transactionDetailsGrandTotal)
        tvGrandTotal.setText(grandTotal)
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