package com.cse226.ca

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    lateinit var et: EditText
    lateinit var txt: TextView
    lateinit var btn: Button
    lateinit var img: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        et = findViewById(R.id.et)
        txt = findViewById(R.id.txt)
        btn = findViewById(R.id.btn)
        img = findViewById(R.id.img)

        btn.setOnClickListener {
            val url = et.text.toString()
            downloadAndDisplay(url)
        }
    }

    private fun downloadAndDisplay(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main) {
                    txt.text = "Downloading File..."
                }
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        txt.text = "Can't download File"
                    }
                } else {
                    val inputStream = response.body?.byteStream()
                    val file = File(cacheDir, "download_file.png")
                    val fos = FileOutputStream(file)
                    inputStream.use { inputStream ->
                        fos.use { outputStream ->
                            inputStream?.copyTo(outputStream) ?: null
                        }
                    }

                    val imgBitmap = BitmapFactory.decodeFile(file.absolutePath)
                    img.setImageBitmap(imgBitmap)

                    withContext(Dispatchers.Main) {
                        txt.text = "File Downloaded!"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    txt.text = "Error occured: ${e.localizedMessage}"
                    e.printStackTrace()
                }
            }
        }
    }
}