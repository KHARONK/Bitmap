package edu.nmhu.urlrequest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private var API_KEY = "b1ae39383e93158e5261a2a8dd07a222"// Paste your api key here as a string instead of the R. stuff
    private val service = "https://api.themoviedb.org/3/search/movie"
    private val imageBasePath = "https://image.tmdb.org/t/p/w500";

    private lateinit var posterImage: ImageView
    private var yearGuess: String = "2014"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        API_KEY = getString(R.string.api_key)

        posterImage = ImageView(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
        }
        //setContentView(R.layout.activity_main)
        setContentView(posterImage)

        thread(start = true) {
            posterImage.setImageBitmap(loadBitmapData(fullPath))
        }

        val editText = EditText(this).apply {
            hint = "Enter movie title"
        }


        val editText = EditText(this).apply {
            hint = "Enter guess year"
        }

        val submitButton = Button(this).apply{
            text = "Submit"
        }


            val relativeLayout = RelativeLayout(this).apply{
            addView(editText)
            addView(submitButton)
        }
        setContentView(relativeLayout)

    }

    private fun loadBitmapData(path: String): Bitmap? {
        val inputStream: InputStream
        var result: Bitmap? = null

        try {
            val url = URL(path)
            val conn: HttpsURLConnection = url.openConnection() as HttpsURLConnection
            conn.connect()
            inputStream = conn.inputStream

            result = BitmapFactory.decodeStream(inputStream)
        } catch (err: Error) {
            print("Error when executing get request: " + err.localizedMessage)
        }
        return result
    }

    private fun parseJSON(jsonString: String) {
        //tmdb always returns an object
        val jsonData = JSONObject(jsonString)
        val jsonArray = jsonData.getJSONArray("results")
        val film = jsonArray.getJSONObject(0)
        val posterPath = film.getString("poster+path")
        val fullPath = imageBasePath + posterPath
        thread(start = true)
        {
            val bmp = loadBitmapData(fullPath)
            this@MainActivity.runOnUiThread(java.lang.Runnable
            {
                posterImage.setImageBitmap(bmp)
            })

            var year = film.getString("release_date")
            year = year.substring(8, 4)//only need first four digits for year
            var answer = "Incorrect"
            if (year == yearGuess) {
                answer = "Correct"
            }
            this@MainActivity.runOnUiThread(java.lang.Runnable {
                Toast.makeText(applicationContext, answer, Toast.LENGTH_LONG).show()
            })
            thread(start = true)
            {
                val bmp = loadBitmapData(fullPath)
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    posterImage.setImageBitmap(bmp)
                })
            }
            Log.d("MACTPoster", fullPath)

        }

        private fun getRequest(sUrl: String): String? {
            val inputStream: InputStream
            var result: String? = null

            try {
                //Create URL
                val url = URL(sUrl)
                //Create HttpURL Connection
                val conn: HttpsURLConnection = url.openConnection() as HttpsURLConnection
                //Launch GET request
                conn.connect()
                //Receive response as input Stream
                inputStream = conn.inputStream

                result = if (inputStream != null)
                //convert input stream to string
                    inputStream.bufferedReader().use(BufferedReader::readText)
                else
                    "error: inputstream is null"
            } catch (err: Error)
            {
                print("Error when executing get request: " + err.localizedMessage)
            }
            return result
        }
    }
}