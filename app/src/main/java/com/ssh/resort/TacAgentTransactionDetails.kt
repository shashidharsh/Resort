package com.ssh.resort

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class TacAgentTransactionDetails : AppCompatActivity() {

    private val TAG = "TacAgentTransactionDetails"

    var UPI_PAYMENT = 0
    var paymentStatus: String? = ""

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
    var upiID : String? = ""

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
        activityPaymentStatus = intent.getStringExtra("ActivityPaymentStatus")
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
        upiID = intent.getStringExtra("upiID")

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
        var tvActivityPaymentStatus = findViewById<TextView>(R.id.tacAgentTransactionDetailsActivityPaymentStatus)
        tvActivityPaymentStatus.setText(activityPaymentStatus)
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
        var tvBalanceB2B = findViewById<TextView>(R.id.tacAgentTransactionDetailsBalanceB2B)
        tvBalanceB2B.setText(balB2B)
        var tvTotalActivityPrice = findViewById<TextView>(R.id.tacAgentTransactionDetailsTotalActivityPrice)
        tvTotalActivityPrice.setText(totActivityPrice)
        var tvTAC = findViewById<TextView>(R.id.tacAgentTransactionDetailsTAC)
        tvTAC.setText(tac)
        var tvBalanceTAC = findViewById<TextView>(R.id.tacAgentTransactionDetailsBalanceTAC)
        tvBalanceTAC.setText(balTAC)
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
        payAmount.setText(balTAC)

        var pay = findViewById<Button>(R.id.tacAgentTransactionDetailsPay)
        pay.setOnClickListener{
            if (balTAC!!.contains("---")){
                Toast.makeText(this@TacAgentTransactionDetails, "No TAC Payment Amount", Toast.LENGTH_SHORT).show()
            } else if (balTAC!!.equals("0.0")){
                Toast.makeText(this@TacAgentTransactionDetails, "Balance TAC Amount is 0", Toast.LENGTH_SHORT).show()
            } else{
                payUsingUpi(upiID!!, selectedCo!!, "Payment", tvBalanceTAC!!.text.toString())
            }
        }
    }

    fun payUsingUpi(upiId: String, name: String, note: String, amount: String) {

        val uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", name)
            .appendQueryParameter("tn", note)
            .appendQueryParameter("am", amount)
            .appendQueryParameter("cu", "INR")
            .build()

        Log.d(TAG, "payUsingUpi: uri" + uri)

        val upiPayIntent = Intent(Intent.ACTION_VIEW)
        upiPayIntent.data = uri

        // will always show a dialog to user to choose an app
        val chooser = Intent.createChooser(upiPayIntent, "Pay with")

        // check if intent resolves
        if (null != chooser.resolveActivity(packageManager)) {
            startActivityForResult(chooser, UPI_PAYMENT)
        } else {
            Toast.makeText(this@TacAgentTransactionDetails, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show()
        }
    }

    private fun upiPaymentDataOperation(data: ArrayList<String>) {
        if (isConnectionAvailable(this@TacAgentTransactionDetails)) {
            var str: String? = data[0]
            Log.d("UPIPAY", "upiPaymentDataOperation: " + str!!)
            var paymentCancel = ""
            if (str == null) str = "discard"
            var status = ""
            var approvalRefNo = ""
            val response = str.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in response.indices) {
                val equalStr = response[i].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (equalStr.size >= 2) {
                    if (equalStr[0].toLowerCase() == "Status".toLowerCase()) {
                        status = equalStr[1].toLowerCase()
                    } else if (equalStr[0].toLowerCase() == "ApprovalRefNo".toLowerCase() || equalStr[0].toLowerCase() == "txnRef".toLowerCase()) {
                        approvalRefNo = equalStr[1]
                    }
                } else {
                    paymentCancel = "Payment cancelled by user."
                }
            }

            if (status == "success") {
                //Code to handle successful transaction here.
                paymentStatus = "Success"
                //insertCheckin()
                Log.d("UPI", "responseStr: $approvalRefNo")
            } else if ("Payment cancelled by user." == paymentCancel) {
                Toast.makeText(this@TacAgentTransactionDetails, "Payment cancelled by user.", Toast.LENGTH_SHORT).show()
                paymentStatus = "Cancelled"
            } else {
                Toast.makeText(this@TacAgentTransactionDetails, "Transaction Failed.", Toast.LENGTH_SHORT).show()
                paymentStatus = "Failed"
                //insertCheckin()
            }
        } else {
            Toast.makeText(this@TacAgentTransactionDetails, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            UPI_PAYMENT -> if (Activity.RESULT_OK == resultCode || resultCode == 11) {
                if (data != null) {
                    val trxt = data.getStringExtra("response")
                    Log.d("UPI", "onActivityResult: $trxt")
                    val dataList = ArrayList<String>()
                    if (trxt == null){
                        dataList.add("Payment Cancelled")
                    }
                    else {
                        dataList.add(trxt!!)
                    }
                    upiPaymentDataOperation(dataList)
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null")
                    val dataList = ArrayList<String>()
                    dataList.add("nothing")
                    upiPaymentDataOperation(dataList)
                }
            } else {
                Log.d("UPI", "onActivityResult: " + "Return data is null") //when user simply back without payment
                val dataList = ArrayList<String>()
                dataList.add("nothing")
                upiPaymentDataOperation(dataList)
            }
        }
    }

    companion object {
        fun isConnectionAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val netInfo = connectivityManager.activeNetworkInfo
                if (netInfo != null && netInfo.isConnected && netInfo.isConnectedOrConnecting && netInfo.isAvailable) {
                    return true
                }
            }
            return false
        }
    }
}