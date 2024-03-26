package com.ssh.resort

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class DisplayQR : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_qr)

        val byteArray = intent.getByteArrayExtra("QrCodeImage")
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

        var ivQR = findViewById<ImageView>(R.id.QrCodeImage)
        ivQR.setImageBitmap(bmp)
    }
}