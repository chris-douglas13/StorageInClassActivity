package com.example.networkapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.Exception

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save and load comic info automatically when app starts)

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }

        try {
            val fileName = "comic.txt"
            val fileInputStream: FileInputStream = openFileInput(fileName)
            val length: Int = fileInputStream.available()
            val buffer = ByteArray(length)
            fileInputStream.read(buffer)
            fileInputStream.close()

            val jsonString = String(buffer)
            val comicObject = JSONObject(jsonString)

            Log.d("MainActivity", "Comic loaded from file: $fileName")

            titleTextView.text = comicObject.getString("title")
            descriptionTextView.text = comicObject.getString("alt")
            Picasso.get().load(comicObject.getString("img")).into(comicImageView)

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun downloadComic (comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add (
            JsonObjectRequest(url, {showComic(it)}, {
            })
        )
    }

    private fun showComic (comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
        saveComic(comicObject)
    }

    private fun saveComic(comicObject: JSONObject){
        try {
            val fileName = "comic.txt"
            val file = File(filesDir, fileName)
            val fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write(comicObject.toString().toByteArray())
            Log.d("MainActivity", "file saved to: $file")
            fileOutputStream.close()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

}