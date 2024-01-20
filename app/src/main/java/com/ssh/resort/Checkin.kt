package com.ssh.resort

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RectF
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.ssh.appdataremotedb.HTTPDownload
import com.ssh.appdataremotedb.Utils
import com.ssh.resort.data.ExistingAgentListData
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class Checkin : AppCompatActivity() {

    val TAG = "Checkin"

    var UPI_ID: String? = "8088756868@ybl"

    var paymentStatus: String? = ""
    var paymentAmount: String? = ""

    internal val UPI_PAYMENT = 0

    var radioGroupActivities: RadioGroup? = null
    var radioGroupDriver: RadioGroup? = null
    var radioGroupPaymentType: RadioGroup? = null
    var radioButtonActivities: RadioButton? = null
    var radioButtonDriver: RadioButton? = null
    var radioButtonPaymentType: RadioButton? = null

    val agentList: ArrayList<ExistingAgentListData> = ArrayList()
    val agentShowList: ArrayList<String> = ArrayList()
    var currentAgent : String = ""
    var activities :String? = null

    var guestName :EditText? = null
    var packagePerHeadChild :EditText? = null
    var roomNumber :EditText? = null
    var b2bPrice :EditText? = null
    var advanceAmount :EditText? = null
    var noOfPerson :EditText? = null
    var noOfChildren :EditText? = null
    var packagePerHeadAddult :EditText? = null
    var driverCost :EditText? = null
    var etNoOfPersonForActivity :EditText? = null
    var tvB2B :TextView? = null
    var tvActivityPrice :TextView? = null
    var tvTAC :TextView? = null
    var tvTotal :TextView? = null
    var tvGrandTotal :TextView? = null
    var b2b :String? = null
    var activityPrice :String? = null
    var tac :String? = null
    var total :String? = null
    var grandTotal :String? = null
    var etCashAmount :EditText? = null

    var ivQrCode: ImageView? = null
    var expandImage :ImageView? = null
    var qrCodeImageBitmap: Bitmap? = null
    var container: FrameLayout? = null

    // Hold a reference to the current animator so that it can be canceled midway.
    private var currentAnimator: Animator? = null

    // The system "short" animation time duration in milliseconds. This duration is ideal for subtle
    // animations or animations that occur frequently.
    private var shortAnimationDuration: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin)

        var layoutActivities = findViewById<RelativeLayout>(R.id.layoutActivities)
        var noOfPersonActivity = findViewById<LinearLayout>(R.id.noOfPersonsForActivity)
        var layoutDriver = findViewById<LinearLayout>(R.id.layoutDriver)
        var layoutPayment = findViewById<LinearLayout>(R.id.layoutPayment)

        layoutActivities.visibility = View.GONE
        layoutDriver.visibility = View.GONE
        noOfPersonActivity.visibility = View.GONE
        layoutPayment.visibility = View.GONE

        //Get Agent Details
        getAgentDetailsFromServer()

        //access the items of the list
        var activity = resources.getStringArray(R.array.Activities)

        //Set Agent Dropdown Spinner
        var arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, agentShowList)
        val agentDropDownSpinner = findViewById(R.id.coSpinner) as Spinner
        agentDropDownSpinner.adapter = arrayAdapter

        agentDropDownSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //view!!.setBackgroundColor(Color.rgb(30,144,255))
                parent?.getItemAtPosition(position) as String
                currentAgent = agentShowList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        //Set Activities Dropdown Spinner
        val spinner = findViewById<Spinner>(R.id.activitiesSpinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, activity)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    activities = activity[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        //radio Group Activities
        radioGroupActivities = findViewById(R.id.radioGroupActivities)

        // Uncheck or reset the radio buttons initially
        radioGroupActivities!!.clearCheck()

        // Add the Listener to the RadioGroup Activities
        radioGroupActivities!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val radioButtonActivities = group.findViewById<View>(checkedId) as RadioButton
            if (radioButtonActivities.text.contains("Yes")){
                layoutActivities.visibility = View.VISIBLE
                noOfPersonActivity.visibility = View.VISIBLE
            }
            else{
                layoutActivities.visibility = View.GONE
                noOfPersonActivity.visibility = View.GONE
            }
        })

        //radio Group Driver
        radioGroupDriver = findViewById(R.id.radioGroupDriver)

        // Uncheck or reset the radio buttons initially
        radioGroupDriver!!.clearCheck()

        // Add the Listener to the RadioGroup Driver
        radioGroupDriver!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val radioButtonDriver = group.findViewById<View>(checkedId) as RadioButton
            if (radioButtonDriver.text.contains("Yes")){
                layoutDriver.visibility = View.VISIBLE
            }
            else{
                layoutDriver.visibility = View.GONE
            }
        })

        //Radio Group Payment Type
        radioGroupPaymentType = findViewById(R.id.radioGroupPaymentType)

        // Uncheck or reset the radio buttons initially
        radioGroupPaymentType!!.clearCheck()

        // Add the Listener to the RadioGroup Payment Type
        radioGroupPaymentType!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val radioButtonPaymentType = group.findViewById<View>(checkedId) as RadioButton
            if (radioButtonPaymentType.text.contains("Cash + UPI")){
                layoutPayment.visibility = View.VISIBLE
            }
            else{
                layoutPayment.visibility = View.GONE
            }
        })

        //Initialization
        guestName = findViewById(R.id.etCheckinGuestName)
        packagePerHeadChild = findViewById(R.id.etPackagePerHeadForChild)
        roomNumber = findViewById(R.id.etCheckinRoomNumber)
        b2bPrice = findViewById(R.id.etB2BPrice)
        advanceAmount = findViewById(R.id.etAdvanceAmount)
        noOfPerson = findViewById(R.id.etCheckinNoOfPersons)
        noOfChildren = findViewById(R.id.etCheckinNoOfChildrens)
        packagePerHeadAddult = findViewById(R.id.etPackagePerHeadForAdult)
        driverCost = findViewById(R.id.etDriverCost)
        tvB2B = findViewById(R.id.tvCheckinB2B)
        tvActivityPrice = findViewById(R.id.tvCheckinActivityPrice)
        tvTAC = findViewById(R.id.tvCheckinTAC)
        tvTotal = findViewById(R.id.tvCheckinTotal)
        tvGrandTotal = findViewById(R.id.tvCheckinGrandTotal)
        etNoOfPersonForActivity = findViewById(R.id.etNoOfPersonsActivities)
        etCashAmount = findViewById(R.id.etCashAmount)
        ivQrCode = findViewById(R.id.idIVQrcode)
        expandImage = findViewById(R.id.expanded_image)
        container = findViewById(R.id.container)

        //Calculate Data
        var calculate = findViewById<Button>(R.id.btnCalculate)
        calculate.setOnClickListener{
            val selectedDriverId: Int = radioGroupDriver!!.getCheckedRadioButtonId()
            radioButtonDriver = radioGroupDriver!!.findViewById(selectedDriverId)

            val selectedActivitiesId: Int = radioGroupActivities!!.getCheckedRadioButtonId()
            radioButtonActivities = radioGroupActivities!!.findViewById(selectedActivitiesId)

            if (guestName!!.text.toString().equals("") || packagePerHeadAddult!!.text.toString().equals("")
                || packagePerHeadChild!!.text.toString().equals("") || noOfPerson!!.text.toString().equals("")
                || noOfChildren!!.text.toString().equals("") || b2bPrice!!.text.toString().equals("")
                || advanceAmount!!.text.toString().equals("") || roomNumber!!.text.toString().equals("")) {
                Toast.makeText(this, "Please Enter all the fields", Toast.LENGTH_SHORT).show()
            }
            else if (selectedActivitiesId == -1) {
                Toast.makeText(this@Checkin, "No Activities has been selected", Toast.LENGTH_SHORT).show()
            }
            else if (selectedDriverId == -1){
                Toast.makeText(this@Checkin, "No Driver has been selected", Toast.LENGTH_SHORT).show()
            }
            else {
                //Driver Radio Button
                if (radioButtonDriver!!.text.equals("Yes")) {
                    if (driverCost!!.text.toString().equals("")) {
                        Toast.makeText(this, "Please Enter Driver Cost", Toast.LENGTH_SHORT).show()
                    } else {
                        //Calculate B2B
                        b2b = (((b2bPrice!!.text.toString().toFloat() * noOfPerson!!.text.toString().toFloat()) +
                                (noOfChildren!!.text.toString().toFloat() * packagePerHeadChild!!.text.toString().toFloat())) + (driverCost!!.text.toString().toFloat())).toString()
                        Log.d(TAG, "b2b: " + b2b)
                        tvB2B!!.setText(b2b)
                    }
                } else {
                    //Calculate B2B
                    b2b = ((b2bPrice!!.text.toString().toFloat() * noOfPerson!!.text.toString().toFloat()) +
                            (noOfChildren!!.text.toString().toFloat() * packagePerHeadChild!!.text.toString().toFloat())).toString()
                    Log.d(TAG, "b2b: " + b2b)
                    tvB2B!!.setText(b2b)
                }

                //Activities Radio Button
                if (radioButtonActivities!!.text.contains("Yes")) {

                    //Get Only Numbers
                    val str = activities
                    val amountOnly = str!!.replace("[^0-9]".toRegex(), "")

                    //Calculate Activity Price
                    if (amountOnly.toInt() == 150) {
                        activityPrice = ((etNoOfPersonForActivity!!.text.toString().toFloat() * 150)).toString()
                        Log.d(TAG, "activityPrice: " + activityPrice)
                        tvActivityPrice!!.setText(activityPrice)
                    } else if (amountOnly.toInt() == 250) {
                        activityPrice = ((etNoOfPersonForActivity!!.text.toString().toFloat() * 250)).toString()
                        Log.d(TAG, "activityPrice: " + activityPrice)
                        tvActivityPrice!!.setText(activityPrice)
                    } else if (amountOnly.toInt() == 400) {
                        activityPrice = ((etNoOfPersonForActivity!!.text.toString().toFloat() * 400)).toString()
                        Log.d(TAG, "activityPrice: " + activityPrice)
                        tvActivityPrice!!.setText(activityPrice)
                    } else if (amountOnly.toInt() == 500) {
                        activityPrice = ((etNoOfPersonForActivity!!.text.toString().toFloat() * 500)).toString()
                        Log.d(TAG, "activityPrice: " + activityPrice)
                        tvActivityPrice!!.setText(activityPrice)
                    } else if (amountOnly.toInt() == 550) {
                        activityPrice = ((etNoOfPersonForActivity!!.text.toString().toFloat() * 550)).toString()
                        Log.d(TAG, "activityPrice: " + activityPrice)
                        tvActivityPrice!!.setText(activityPrice)
                    } else {
                        activityPrice = ((etNoOfPersonForActivity!!.text.toString().toFloat() * 1050)).toString()
                        Log.d(TAG, "activityPrice: " + activityPrice)
                        tvActivityPrice!!.setText(activityPrice)
                    }
                } else {
                    tvActivityPrice!!.setText("0.0")
                }

                //Calculate TAC
                tac = ((packagePerHeadAddult!!.text.toString().toFloat() - b2bPrice!!.text.toString().toFloat()) * noOfPerson!!.text.toString().toFloat()).toString()
                Log.d(TAG, "tac: " + tac)
                tvTAC!!.setText(tac)

                //Calculate Total
                total = ((tvB2B!!.text.toString().toFloat() + tvActivityPrice!!.text.toString().toFloat()) +
                        (tvTAC!!.text.toString().toFloat())).toString()
                Log.d(TAG, "total: " + total)
                tvTotal!!.setText(total)

                //Calculate Grand Total
                grandTotal = (total!!.toFloat() - advanceAmount!!.text.toString().toFloat()).toString()
                Log.d(TAG, "grandTotal: " + grandTotal)
                tvGrandTotal!!.setText(grandTotal)
            }
        }

        // Pay Button
        var pay = findViewById<Button>(R.id.btnPay)
        pay.setOnClickListener(View.OnClickListener {
            val selectedActivitiesId: Int = radioGroupActivities!!.getCheckedRadioButtonId()
            val selectedDriverId: Int = radioGroupDriver!!.getCheckedRadioButtonId()
            val selectedPaymentTypeId: Int = radioGroupPaymentType!!.getCheckedRadioButtonId()

            if (selectedActivitiesId == -1) {
                Toast.makeText(this@Checkin, "No Activities has been selected", Toast.LENGTH_SHORT).show()
            }
            else if (selectedDriverId == -1){
                Toast.makeText(this@Checkin, "No Driver has been selected", Toast.LENGTH_SHORT).show()
            }
            else if (selectedPaymentTypeId == -1){
                Toast.makeText(this@Checkin, "Please Select Payment Type", Toast.LENGTH_SHORT).show()
            }
            else if (guestName!!.text.toString().equals("") || packagePerHeadAddult!!.text.toString().equals("")
                || packagePerHeadChild!!.text.toString().equals("") || noOfPerson!!.text.toString().equals("")
                || noOfChildren!!.text.toString().equals("") || b2bPrice!!.text.toString().equals("")
                || advanceAmount!!.text.toString().equals("") || roomNumber!!.text.toString().equals("")) {
                Toast.makeText(this, "Please Enter all the fields", Toast.LENGTH_SHORT).show()
            }
            else if (tvB2B!!.text.toString().equals("") || tvTAC!!.text.toString().equals("") || tvTotal!!.text.toString().equals("")) {
                Toast.makeText(this, "Please Calculate Total", Toast.LENGTH_SHORT).show()
            }
            else if (Utils.checkInternetConnectivity(this) == false){
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            }
            else {
                radioButtonActivities = radioGroupActivities!!.findViewById(selectedActivitiesId)
                radioButtonDriver = radioGroupDriver!!.findViewById(selectedDriverId)
                radioButtonPaymentType = radioGroupPaymentType!!.findViewById(selectedPaymentTypeId)
                Log.d(TAG, "RadioGroupActivity: " + radioButtonActivities!!.text)
                Log.d(TAG, "RadioGroupDriver: " + radioButtonDriver!!.text)
                Log.d(TAG, "RadioGroupPaymentType: " + radioButtonPaymentType!!.text)

                if (radioButtonPaymentType!!.text.equals("Cash")) {
                    paymentAmount = tvGrandTotal!!.text.toString()
                    insertCheckin()
                }
                else if (radioButtonPaymentType!!.text.equals("UPI")){
                    paymentAmount = tvGrandTotal!!.text.toString()
                    payUsingUpi(paymentAmount!!, UPI_ID!!, "", "Pay")
                }
                else{
                    paymentAmount = tvGrandTotal!!.text.toString()
                    if (etCashAmount!!.text.toString().equals("")){
                        Toast.makeText(this, "Please Enter Amount", Toast.LENGTH_SHORT).show()
                    }
                    else if (etCashAmount!!.text.toString().toFloat() > tvGrandTotal!!.text.toString().toFloat()){
                        moreAmountEnteredDialog()
                    }
                    else{
                        var payAmount = paymentAmount!!.toFloat() - etCashAmount!!.text.toString().toFloat()
                        if (payAmount != 0.0f){
                            payUsingUpi(payAmount.toString(), UPI_ID!!, "", "Pay")
                        }
                        else{
                            enteredFullAmountDialog()
                        }
                    }
                }
            }
        })

        //Generate QR Code Button
        var generateQR = findViewById<Button>(R.id.idBtnGenerateQR)
        generateQR.setOnClickListener{
            val selectedPaymentTypeId: Int = radioGroupPaymentType!!.getCheckedRadioButtonId()

            if (selectedPaymentTypeId == -1){
                Toast.makeText(this@Checkin, "Please Select Payment Type", Toast.LENGTH_SHORT).show()
            }
            else if (tvB2B!!.text.toString().equals("") || tvTAC!!.text.toString().equals("") || tvTotal!!.text.toString().equals("")) {
                Toast.makeText(this, "Please Calculate Total", Toast.LENGTH_SHORT).show()
            }
            else if (Utils.checkInternetConnectivity(this) == false){
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            }
            else {
                radioButtonPaymentType = radioGroupPaymentType!!.findViewById(selectedPaymentTypeId)
                Log.d(TAG, "RadioGroupPaymentType: " + radioButtonPaymentType!!.text)

                if (radioButtonPaymentType!!.text.equals("Cash")) {
                    paymentAmount = tvTotal!!.text.toString()
                    cashPayQRDialog()
                }
                else if (radioButtonPaymentType!!.text.equals("UPI")){
                    paymentAmount = tvGrandTotal!!.text.toString()
                    generateQR(paymentAmount)
                }
                else{
                    paymentAmount = tvGrandTotal!!.text.toString()
                    if (etCashAmount!!.text.toString().equals("")){
                        Toast.makeText(this, "Please Enter Amount", Toast.LENGTH_SHORT).show()
                    }
                    else if (etCashAmount!!.text.toString().toFloat() > tvGrandTotal!!.text.toString().toFloat()){
                        moreAmountEnteredDialog()
                    }
                    else{
                        var payAmount = paymentAmount!!.toFloat() - etCashAmount!!.text.toString().toFloat()
                        if (payAmount != 0.0f){
                            generateQR(payAmount.toString())
                        }
                        else{
                            enteredFullAmountDialog()
                        }
                    }
                }
            }
        }

        // Hook up taps on the thumbnail views.
        //Zoom QR Code
        ivQrCode!!.setOnClickListener {
            zoomImageFromThumb(it, qrCodeImageBitmap)
        }

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
    }

    private fun generateQR(amount: String?) {
        val url = "upi://pay?pa=" +   // payment method.
                UPI_ID!! +         // VPA number.
                "&am="+ amount +       // this param is for fixed amount (non editable).
                "&pn=HillStone%20Resort"+      // to showing your name in app.
                "&cu=INR" +                  // Currency code.
                "&mode=02"                // mode O2 for Secure QR Code.
//                "&orgid=189999" +            //If the transaction is initiated by any PSP app then the respective orgID needs to be passed.
//                "&sign=MEYCIQC8bLDdRbDhpsPAt9wR1a0pcEssDaV" +   // Base 64 encoded Digital signature needs to be passed in this tag
//                "Q7lugo8mfJhDk6wIhANZkbXOWWR2lhJOH2Qs/OQRaRFD2oBuPCGtrMaVFR23t"

        try {
            qrCodeImageBitmap = textToImageEncode(url)
            ivQrCode!!.setImageBitmap(qrCodeImageBitmap)
            ivQrCode!!.invalidate()
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun textToImageEncode(Value: String): Bitmap? {
        val bitMatrix: BitMatrix
        try {
            bitMatrix = MultiFormatWriter().encode(Value, BarcodeFormat.QR_CODE, 400, 400, null)
        } catch (Illegalargumentexception: IllegalArgumentException) {
            return null
        }
        val bitMatrixWidth = bitMatrix.getWidth()
        val bitMatrixHeight = bitMatrix.getHeight()
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix.get(x, y))
                    Color.parseColor("#000000")
                else
                    Color.parseColor("#ffffff")
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, 400, 0, 0, 400, 400)
        return bitmap
    }

    private fun zoomImageFromThumb(thumbView: View, imageBitmap: Bitmap?) {
        // If there's an animation in progress, cancel it immediately and
        // proceed with this one.
        currentAnimator?.cancel()

        // Load the high-resolution "zoomed-in" image.
        expandImage!!.setImageBitmap(imageBitmap)

        // Calculate the starting and ending bounds for the zoomed-in image.
        val startBoundsInt = android.graphics.Rect()
        val finalBoundsInt = android.graphics.Rect()
        val globalOffset = android.graphics.Point()

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the
        // container view. Set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBoundsInt)
        container!!.getGlobalVisibleRect(finalBoundsInt, globalOffset)
        startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
        finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)

        val startBounds = RectF(startBoundsInt)
        val finalBounds = RectF(finalBoundsInt)

        // Using the "center crop" technique, adjust the start bounds to be the
        // same aspect ratio as the final bounds. This prevents unwanted
        // stretching during the animation. Calculate the start scaling factor.
        // The end scaling factor is always 1.0.
        val startScale: Float
        if ((finalBounds.width() / finalBounds.height() > startBounds.width() / startBounds.height())) {
            // Extend start bounds horizontally.
            startScale = startBounds.height() / finalBounds.height()
            val startWidth: Float = startScale * finalBounds.width()
            val deltaWidth: Float = (startWidth - startBounds.width()) / 2
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            // Extend start bounds vertically.
            startScale = startBounds.width() / finalBounds.width()
            val startHeight: Float = startScale * finalBounds.height()
            val deltaHeight: Float = (startHeight - startBounds.height()) / 2f
            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it positions the zoomed-in view in the place of the
        // thumbnail.
        thumbView.alpha = 0f

        animateZoomToLargeImage(startBounds, finalBounds, startScale)

        setDismissLargeImageAnimation(thumbView, startBounds, startScale)
    }

    private fun animateZoomToLargeImage(startBounds: RectF, finalBounds: RectF, startScale: Float) {
        expandImage!!.visibility = View.VISIBLE

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the
        // top-left corner of the zoomed-in view. The default is the center of
        // the view.
        expandImage!!.pivotX = 0f
        expandImage!!.pivotY = 0f

        // Construct and run the parallel animation of the four translation and
        // scale properties: X, Y, SCALE_X, and SCALE_Y.
        currentAnimator = AnimatorSet().apply {
            play(
                ObjectAnimator.ofFloat(expandImage, View.X, startBounds.left, finalBounds.left)).apply {
                with(ObjectAnimator.ofFloat(expandImage, View.Y, startBounds.top, finalBounds.top))
                with(ObjectAnimator.ofFloat(expandImage, View.SCALE_X, startScale, 1f))
                with(ObjectAnimator.ofFloat(expandImage, View.SCALE_Y, startScale, 1f))
            }
            duration = shortAnimationDuration.toLong()
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    currentAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    currentAnimator = null
                }
            })
            start()
        }
    }

    private fun setDismissLargeImageAnimation(thumbView: View, startBounds: RectF, startScale: Float) {
        // When the zoomed-in image is tapped, it zooms down to the original
        // bounds and shows the thumbnail instead of the expanded image.
        expandImage!!.setOnClickListener {
            currentAnimator?.cancel()

            // Animate the four positioning and sizing properties in parallel,
            // back to their original values.
            currentAnimator = AnimatorSet().apply {
                play(ObjectAnimator.ofFloat(expandImage, View.X, startBounds.left)).apply {
                    with(ObjectAnimator.ofFloat(expandImage, View.Y, startBounds.top))
                    with(ObjectAnimator.ofFloat(expandImage, View.SCALE_X, startScale))
                    with(ObjectAnimator.ofFloat(expandImage, View.SCALE_Y, startScale))
                }
                duration = shortAnimationDuration.toLong()
                interpolator = DecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        thumbView.alpha = 1f
                        expandImage!!.visibility = View.GONE
                        currentAnimator = null
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        thumbView.alpha = 1f
                        expandImage!!.visibility = View.GONE
                        currentAnimator = null
                    }
                })
                start()
            }
        }
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

                val agentDetailsUrl = "https://sshsoftwares.in/Resort/GetAgents.php"

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
                agentList.clear()

                for (i in 0 until agentResult.length()) {
                    val json_data = agentResult.getJSONObject(i)
                    val agentListData = ExistingAgentListData(
                        json_data.getString("id"),
                        json_data.getString("name"),
                        json_data.getString("mobile"),
                        json_data.getString("phonePe"),
                        json_data.getString("email"))
                    agentList.add(agentListData)
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
                    agentShowList.clear()
                    for (i in 0 until agentList.size) {
                        agentShowList.add(agentList.get(i).name)
                    }

                    var arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, agentShowList)
                    val coDropDownSpinner = findViewById(R.id.coSpinner) as Spinner
                    coDropDownSpinner.adapter = arrayAdapter
                }
            }
        }
        var result = AgentDetails().execute()
    }

    //Save Checkin Data in Server
    @SuppressLint("LongLogTag")
    fun insertCheckin() {
        Log.d(TAG, "insertCheckin: ")

        var pd = Dialog(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.progress, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.setCancelable(false)

        class CheckinData : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String {
                var response: String = getCheckinData()
                Log.d(TAG, "response: " + response)
                return response
            }

            override fun onPreExecute() {
                super.onPreExecute()
                pd.show()
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                Log.d(TAG, "onPostExecute: result " + result)
                pd.cancel()
                Toast.makeText(this@Checkin, result, Toast.LENGTH_LONG).show()
                guestName!!.setText("")
                noOfPerson!!.setText("")
                noOfChildren!!.setText("")
                packagePerHeadAddult!!.setText("")
                packagePerHeadChild!!.setText("")
                b2bPrice!!.setText("")
                advanceAmount!!.setText("")
                roomNumber!!.setText("")
                driverCost!!.setText("")
                driverCost!!.setText("")
                tvB2B!!.setText("")
                tvActivityPrice!!.setText("")
                etNoOfPersonForActivity!!.setText("")
                tvTAC!!.setText("")
                tvTotal!!.setText("")
                tvGrandTotal!!.setText("")
                etCashAmount!!.setText("")
            }
        }
        var saveData = CheckinData().execute()
    }

    //Export Checkin Data Using HTTP POST Method
    @SuppressLint("LongLogTag")
    fun getCheckinData(): String {

        var response: String = ""

        val url = URL("https://sshsoftwares.in/Resort/InsertCheckin.php")
        Log.d(TAG, "getCheckinData URL: " + url)
        var client: HttpURLConnection? = null
        try {
            client = url.openConnection() as HttpURLConnection
            client.setRequestMethod("POST")
            client.setRequestProperty("Content-Type", "application/json")
            client.setDoInput(true)
            client.setDoOutput(true)
            val os: OutputStream = client.getOutputStream()
            val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
            writer.write(getDataToJson())
            writer.flush()
            writer.close()
            os.close()
            val responseCode: Int = client.getResponseCode()
            Log.d(TAG, "responseCode: " + responseCode)

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                val br = BufferedReader(InputStreamReader(client.getInputStream()))
                var line: String? = br.readLine()
                while (line != null) {
                    response += line
                    line = br.readLine()
                    Log.d(TAG, "if true response: " + response)
                }
            } else {
                response = ""
                Log.d(TAG, "response empty: " + response)
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } finally {
            if (client != null) // Make sure the connection is not null.
                client.disconnect()
        }
        return response
    }

    fun getDataToJson(): String {
        var dataJson: JSONObject = JSONObject()
        dataJson.put("GuestName", guestName!!.text.toString())
        dataJson.put("NoOfPersons", noOfPerson!!.text.toString())
        dataJson.put("NoOfChildrens", noOfChildren!!.text.toString())
        dataJson.put("PackageForAdult", packagePerHeadAddult!!.text.toString())
        dataJson.put("PackageForChild", packagePerHeadChild!!.text.toString())
        dataJson.put("SelectedCo", currentAgent)
        dataJson.put("B2BPrice", b2bPrice!!.text.toString())
        dataJson.put("Advance", advanceAmount!!.text.toString())
        dataJson.put("Activities", radioButtonActivities!!.text)

        if (radioButtonActivities!!.text.equals("Yes")) {
            dataJson.put("SelectedActivity", activities)
            dataJson.put("NoPersons", etNoOfPersonForActivity!!.text.toString())
        }
        else{
            dataJson.put("SelectedActivity", "Nothing")
            dataJson.put("NoPersons", "0")
        }

        dataJson.put("RoomNumber", roomNumber!!.text.toString())
        dataJson.put("Driver", radioButtonDriver!!.text)

        if (radioButtonDriver!!.text.equals("Yes")) {
            dataJson.put("DriverCost", driverCost!!.text.toString())
        }
        else{
            dataJson.put("DriverCost", "0")
        }

        dataJson.put("TotalB2B", tvB2B!!.text.toString())
        dataJson.put("TotalActivityPrice", tvActivityPrice!!.text.toString())
        dataJson.put("TotalTAC", tvTAC!!.text.toString())
        dataJson.put("Total", tvTotal!!.text.toString())
        dataJson.put("GrandTotal", tvGrandTotal!!.text.toString())

        dataJson.put("PaymentType", radioButtonPaymentType!!.text)
        if (radioButtonPaymentType!!.text.equals("Cash")) {
            dataJson.put("Cash", tvGrandTotal!!.text.toString())
            dataJson.put("UPI", "0")
            dataJson.put("CashPayStatus", "Received")
            dataJson.put("UPIPayStatus", "No")
        }
        else if (radioButtonPaymentType!!.text.equals("UPI")){
            dataJson.put("Cash", "0")
            dataJson.put("UPI", tvGrandTotal!!.text.toString())
            dataJson.put("CashPayStatus", "No")
            dataJson.put("UPIPayStatus", paymentStatus)
        }
        else{
            dataJson.put("Cash", etCashAmount!!.text.toString())
            dataJson.put("UPI", tvGrandTotal!!.text.toString().toFloat() - etCashAmount!!.text.toString().toFloat())
            dataJson.put("CashPayStatus", "Received")
            dataJson.put("UPIPayStatus", paymentStatus)
        }

        Log.d(TAG, "getDataToJson: " + dataJson.toString())
        return dataJson.toString()
    }

    fun payUsingUpi(amount: String, upiId: String, name: String, note: String) {

        val uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", name)
            .appendQueryParameter("tn", note)
            .appendQueryParameter("am", amount)
            .appendQueryParameter("cu", "INR")
            .build()


        val upiPayIntent = Intent(Intent.ACTION_VIEW)
        upiPayIntent.data = uri

        // will always show a dialog to user to choose an app
        val chooser = Intent.createChooser(upiPayIntent, "Pay with")

        // check if intent resolves
        if (null != chooser.resolveActivity(packageManager)) {
            startActivityForResult(chooser, UPI_PAYMENT)
        } else {
            Toast.makeText(this@Checkin, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show()
        }
    }

    private fun upiPaymentDataOperation(data: ArrayList<String>) {
        if (isConnectionAvailable(this@Checkin)) {
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
                insertCheckin()
                Log.d("UPI", "responseStr: $approvalRefNo")
            } else if ("Payment cancelled by user." == paymentCancel) {
                Toast.makeText(this@Checkin, "Payment cancelled by user.", Toast.LENGTH_SHORT).show()
                paymentStatus = "Cancelled"
            } else {
                Toast.makeText(this@Checkin, "Transaction Failed.", Toast.LENGTH_SHORT).show()
                paymentStatus = "Failed"
                insertCheckin()
            }
        } else {
            Toast.makeText(this@Checkin, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show()
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
                    dataList.add(trxt!!)
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

    //More Amount Entered Dialog
    fun moreAmountEnteredDialog(){
        MaterialAlertDialogBuilder(this, R.style.MyAlertDialogTheme)
            .setIcon(R.drawable.ic_announcement)
            //.setView(R.layout.edit_text)
            .setMessage("Please Enter Amount Less Than or Equal to Grand Total Amount..!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    fun enteredFullAmountDialog(){
        MaterialAlertDialogBuilder(this, R.style.MyAlertDialogTheme)
            .setIcon(R.drawable.ic_announcement)
            //.setView(R.layout.edit_text)
            .setMessage("If you want to pay full cash amount then please select cash option..!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    fun cashPayQRDialog(){
        MaterialAlertDialogBuilder(this, R.style.MyAlertDialogTheme)
            .setIcon(R.drawable.ic_announcement)
            //.setView(R.layout.edit_text)
            .setMessage("Please select UPI or UPI+Cash Payment Type to Generate QR Code..")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}