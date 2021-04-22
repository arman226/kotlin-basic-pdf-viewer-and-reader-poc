package com.example.pdf_poc


import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var myDownloadId: Long = 0
    var STORAGE_PERMISSION_CODE = 1000;
    val PDF_URL =
        "https://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_download.setOnClickListener { handleOnDownloadButton() }
        btn_view.setOnClickListener {
            val intent = Intent(this, PdfActivity::class.java)
            intent.putExtra("PDF_URL", PDF_URL)
            startActivity(intent)
        }
        setBroadcastReceiver()

    }

    private fun handleOnDownloadButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) === PackageManager.PERMISSION_DENIED) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            } else {
                download()
            }
        } else {
            download()
        }
    }

    private fun download() {
        Toast.makeText(applicationContext, "Download Started", Toast.LENGTH_LONG).show()
        var req =
            DownloadManager.Request(Uri.parse(PDF_URL))
                .setTitle("Simple PDF File")
                .setDescription("PDF File Being Downloaded")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "${System.currentTimeMillis()}.pdf"
                )
        var downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        myDownloadId = downloadManager.enqueue(req)
    }

    private fun setBroadcastReceiver() {
        var broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                var id: Long? = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == myDownloadId) {
                    Toast.makeText(
                        applicationContext,
                        "Your file has been Downloaded",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    download()
                } else {
                    Toast.makeText(applicationContext, "Permission not granted", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}