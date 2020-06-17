package com.example.whatsthesub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        findViewById<View>(R.id.button).setOnClickListener { getAbout() }
    }

    private fun getAbout()
    {
        val gson = GsonBuilder()
                .setLenient()
                .create()
        val url = "https://www.reddit.com"
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val service = retrofit.create(RedditService::class.java)

        val call = service.getSubredditAbout("pics")

        call.enqueue(object : Callback<RedditResponse> {
            override fun onResponse(call: Call<RedditResponse>, response: Response<RedditResponse>) {

                    val redditResponse = response.body()!!

                    val subDescription = redditResponse.data!!.description

                    textView!!.text = subDescription

            }

            override fun onFailure(call: Call<RedditResponse>, t: Throwable) {
                textView!!.text = t.message
            }
        })
    }
}
