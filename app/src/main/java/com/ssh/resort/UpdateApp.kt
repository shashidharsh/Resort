package com.ssh.resort

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.database.Cursor
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.ssh.appdataremotedb.Utils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class UpdateApp : AppCompatActivity() {

    val TAG = "UpdateApp"

    val apkUrl = Constants.BASE_URL + "APK/DownloadResort.php"

    var uri: Uri? = null
    var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_app)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        var updateApp = findViewById<Button>(R.id.btnUpdateApp)
        updateApp.setOnClickListener{
            if (Utils.checkInternetConnectivity(this) == false){
                Toast.makeText(this, "Please Turn On Your Mobile Data", Toast.LENGTH_LONG).show()
            }
            else {
                updateApplication()
            }
        }
    }

    //Update App from Server
    fun updateApplication() {
        Log.d(TAG, "updateApplication")

        val inflater = (this).layoutInflater
        val view = inflater.inflate(R.layout.progress_dialog, null)

        var cpTitle = view.findViewById<TextView>(R.id.cp_title)
        var cpCardView = view.findViewById<CardView>(R.id.cp_cardview)
        var progressBar = view.findViewById<ProgressBar>(R.id.cp_pbar)

        // Card Color
        cpCardView.setCardBackgroundColor(Color.parseColor("#70000000"))
        // Progress Bar Color
        setColorFilter(progressBar.indeterminateDrawable, ResourcesCompat.getColor(this.resources, R.color.colorAccent, null))
        // Text Color
        cpTitle.setText("Downloading APK")
        cpTitle.setTextColor(Color.WHITE)
        // Custom Dialog initialization
        var dialog = CustomDialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(view)

        class DownloadApp : AsyncTask<Void, Void, String>() {
            @RequiresApi(Build.VERSION_CODES.R)
            override fun doInBackground(vararg params: Void?): String {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    uri = saveFileToExternalStorage()
                    var filePath = FileUtils().getPath(this@UpdateApp, uri!!)
                    file = File(filePath)

                    if (fileExists("Resort.apk") == true) {
                        file!!.delete()
                    }

                } else {
                    file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Resort.apk")
                    uri = Uri.fromFile(file)
                    val path = FileHandler().getPathFromUri(this@UpdateApp, uri!!)
                    Log.d(TAG, "Path below Q: " + path)

                    //Delete update file if exists
                    if (file!!.exists() && file!=null) //file.delete() - test this, I think sometimes it doesnt work
                        file!!.delete()
                }

                var input: InputStream? = null
                var output: OutputStream? = null
                var connection: HttpURLConnection? = null
                try {
                    val sUrl = URL(apkUrl)
                    connection = sUrl.openConnection() as HttpURLConnection
                    connection!!.connect()

                    // download the file
                    input = connection!!.inputStream
                    output = FileOutputStream(file)
                    val data = ByteArray(4096)
                    var count: Int
                    while (input.read(data).also { count = it } != -1) {
                        // allow canceling with back button
                        if (isCancelled) {
                            input.close()
                            return "Install Cancelled"
                        }
                        output.write(data, 0, count)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        if (output != null) output.close()
                        if (input != null) input.close()
                    } catch (ignored: IOException) {
                        Log.d(TAG, "Exception: " + ignored)
                    }
                    connection?.disconnect()
                }

                return "Install Successful"
            }

            override fun onPreExecute() {
                super.onPreExecute()
                dialog.show()
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                Log.d(TAG, "downloadApplication onPostExecute result:" + result)
                dialog.cancel()
                if (result.equals("Install Cancelled")) {
                    Toast.makeText(applicationContext, "$result", Toast.LENGTH_SHORT).show()
                }
                else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                        intent.setDataAndType(uri, "application/vnd.android.package-archive")
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
                        intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, applicationInfo.packageName)
                        startActivity(intent)
                    } else {
                        val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                        intent.setDataAndType(uri, "application/vnd.android.package-archive")
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
                        intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, applicationInfo.packageName)
                        startActivity(intent)
                    }
                }
            }
        }
        var result = DownloadApp().execute()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileToExternalStorage() : Uri? {

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, "Resort.apk")
        contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/vnd.android.package-archive")
        contentValues.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        val fileUri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        return fileUri
    }

    fun fileExists(fname: String?): Boolean {
        val file = getFileStreamPath(fname)
        return file.exists()
    }

    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            // Set Semi-Transparent Color for Dialog Background
            window?.decorView?.rootView?.setBackgroundResource(R.color.transparent)
            window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                insets.consumeSystemWindowInsets()
            }
        }
    }

    private fun downloadapk() {
        try {
            val url = URL(Constants.BASE_URL + "APK/DownloadResort.php")
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.doOutput = true
            urlConnection.connect()
            val storage = Environment.DIRECTORY_DOWNLOADS
            val file = File(storage, "Resort.apk")
            if (file.exists()){
                file.delete()
            }
            val fileOutput = FileOutputStream(file)
            val inputStream = urlConnection.inputStream
            val buffer = ByteArray(1024)
            var bufferLength = 0
            while (inputStream.read(buffer).also { bufferLength = it } > 0) {
                fileOutput.write(buffer, 0, bufferLength)
            }
            fileOutput.close()
            this.installApk(file)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun installApk(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        var uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            FileProvider.getUriForFile(this@UpdateApp, BuildConfig.APPLICATION_ID + ".provider", file)
        else
            Uri.fromFile(file)
        //val uri = Uri.fromFile(File(path))
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        startActivity(intent)
    }

    @SuppressLint("NewApi")
    fun getRealPathFromURI_API11to18(context: Context?, contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        var result: String? = null
        val cursorLoader = CursorLoader(context, contentUri, proj, null, null, null)
        val cursor: Cursor = cursorLoader.loadInBackground()
        if (cursor != null) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            result = cursor.getString(column_index)
        }
        return result
    }
}