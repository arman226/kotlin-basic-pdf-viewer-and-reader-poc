package com.example.pdf_poc

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import kotlinx.android.synthetic.main.activity_pdf.*
import kotlinx.coroutines.*
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class PdfActivity : AppCompatActivity(), OnLoadCompleteListener {

    lateinit var mypdf1: PDFView
    lateinit var ind: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)

        val url = Uri.parse("http://www.africau.edu/images/default/sample.pdf")

        mypdf1 = mypdf
        ind = pg_ind


        prepareStuff()


    }


    fun prepareStuff() {
        CoroutineScope(Dispatchers.Default).launch {
            val r1 = async {
                loadStream()
            }.await()

            val r2 = async {
                    showStream(r1)
            }.await()


        }

    }

    fun onLoadingDone() {
        ind.visibility = View.GONE
        mypdf1.visibility = View.VISIBLE
    }

    fun showStream(inputStream: InputStream?) {
        mypdf1.fromStream(inputStream)
            .onLoad(this@PdfActivity)
            .load()


    }

    fun loadStream(): InputStream? {
        var inputStream: InputStream? = null
        try {
            val url =
                URL("https://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf")
            // below is the step where we are
            // creating our connection.
            val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection
            if (urlConnection.getResponseCode() === 200) {
                // response is success.
                // we are getting input stream from url
                // and storing it in our variable.
                inputStream = BufferedInputStream(urlConnection.getInputStream())
            }
        } catch (e: IOException) {
            // this is the method
            // to handle errors.
            e.printStackTrace()
            return null
        }

        return inputStream
    }

    override fun loadComplete(nbPages: Int) {

        ind.visibility = View.GONE
//        mypdf1.visibility = View.VISIBLE
    }

}