package com.ssh.resort

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.ssh.appdataremotedb.Utils
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class UpdateApp : AppCompatActivity() {

    val TAG = "UpdateApp"
    val REQUEST_INSTALL = 100

    val FILE_NAME = "Resort.apk"
    val FILE_BASE_PATH = "file://"
    val MIME_TYPE = "application/vnd.android.package-archive"
    val PROVIDER_PATH = ".provider"
    val APP_INSTALL_PATH = "\"application/vnd.android.package-archive\""
    val apkUrl = "https://hillstoneresort.com/Resorts/APK/DownloadResort.php"

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
                //downloadAPK()
                enqueueDownload(apkUrl)
            }
        }
    }

    fun downloadAPK(){
        var destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/Resort.apk"
        val uri = Uri.parse("file://$destination")

        //Delete update file if exists
        val file = File(destination)
        if (file.exists()) //file.delete() - test this, I think sometimes it doesnt work
            file.delete()

        //get url of app on server
        val url = "https://hillstoneresort.com/Resorts/APK/DownloadResort.php"

        //set downloadmanager
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDescription("Downloading APK File")
        request.setTitle("Resort")

        //set destination
        request.setDestinationUri(uri)

        // get download service and enqueue file
        val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)

        //set BroadcastReceiver to install app when .apk is downloaded
        val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                try {
                    val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + "Resort.apk" // path to file apk
                    val file = File(filePath)
                    val uri: Uri = getApkUri(file.path) // get Uri for  each SDK Android
                    val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                    intent.setData(uri)
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
                    intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, applicationInfo.packageName)
                    if (packageManager.queryIntentActivities(intent, 0) != null) { // checked on start Activity
                        startActivityForResult(intent, REQUEST_INSTALL)
                    } else {
                        throw Exception("don`t start Activity.")
                    }
                } catch (e: Exception) {
                    Log.i("$TAG:InstallApk", "Failed installl APK file", e)
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun getApkUri(path: String): Uri {

        // Before N, a MODE_WORLD_READABLE file could be passed via the ACTION_INSTALL_PACKAGE Intent.
        // Since N, MODE_WORLD_READABLE files are forbidden, and a FileProvider is recommended.
        val useFileProvider = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        val tempFilename = "Resort.apk"
        val buffer = ByteArray(16384)
        val fileMode = if (useFileProvider) MODE_PRIVATE else MODE_WORLD_READABLE
        try {
            FileInputStream(File(path)).use { `is` ->
                openFileOutput(tempFilename, fileMode).use { fout ->
                    var n: Int
                    while (`is`.read(buffer).also { n = it } >= 0) {
                        fout.write(buffer, 0, n)
                    }
                }
            }
        } catch (e: IOException) {
            Log.i("$TAG:getApkUri", "Failed to write temporary APK file", e)
        }
        return if (useFileProvider) {
            val toInstall = File(this.filesDir, tempFilename)
            FileProvider.getUriForFile(applicationContext, BuildConfig.APPLICATION_ID + ".provider", toInstall)
        } else {
            Uri.fromFile(getFileStreamPath(tempFilename))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_INSTALL) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Install succeeded!", Toast.LENGTH_SHORT).show()
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Install canceled!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Install Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileToExternalStoragePath() : Uri? {

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, "Resort")
        contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/vnd.android.package-archive")
        contentValues.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        val fileUri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        Log.d(TAG, "saveFileToExternalStoragePath: " + fileUri)

        return fileUri
    }

    fun enqueueDownload(url: String) {
        var destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/"
        destination += FILE_NAME
        val uri = Uri.parse("${FILE_BASE_PATH}$destination")
        val file = File(destination)
        if (file.exists()) file.delete()
        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)
        val request = DownloadManager.Request(downloadUri)
        request.setMimeType(MIME_TYPE)
        request.setTitle("APK is downloading")
        request.setDescription("Downloading...")
        // set destination
        request.setDestinationUri(uri)
        showInstallOption(destination, uri)
        // Enqueue a new download and same the referenceId
        downloadManager.enqueue(request)
        Toast.makeText(this, "Downloading...", Toast.LENGTH_LONG).show()
    }

    private fun showInstallOption(destination: String, uri: Uri) {
        // set BroadcastReceiver to install app when .apk is downloaded
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + PROVIDER_PATH, File(destination))
                    val install = Intent(Intent.ACTION_VIEW)
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                    install.data = contentUri
                    context.startActivity(install)
                    context.unregisterReceiver(this)
                } else {
                    val install = Intent(Intent.ACTION_VIEW)
                    install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    install.setDataAndType(uri, "application/vnd.android.package-archive")
                    context.startActivity(install)
                    context.unregisterReceiver(this)
                }
            }
        }
        this.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }
}