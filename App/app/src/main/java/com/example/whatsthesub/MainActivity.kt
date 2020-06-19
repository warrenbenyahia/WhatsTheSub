package com.example.whatsthesub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Contacts
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Console
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var textView: TextView? = null

    private var imageView1: ImageView? = null

    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button

    private lateinit var buttonList: List<Button>

    private var service : RedditService? = null

    private var randomSubData : QueryResponse? = null
    private var randomSubName : String? = null

    private var randomPostData : List<QueryResponse>? = null
    private var randomPostTitle : String? = null
    private var randomPostImageUrl : String? = null

    private lateinit var similarSubredditsData : QueryResponse
    private var subredditsNameList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRetroFit()

        textView = findViewById(R.id.textView)

        imageView1 = findViewById(R.id.imageView1)

        button1 = findViewById(R.id.button_answer1)
        button2 = findViewById(R.id.button_answer2)
        button3 = findViewById(R.id.button_answer3)
        button4 = findViewById(R.id.button_answer4)

        buttonList = listOf(button1, button2, button3, button4)

        button1.setOnClickListener {
            buttonClicked()
        }
    }

    private fun initRetroFit()
    {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val url = "https://www.reddit.com"

        service = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(RedditService::class.java)
    }

    private fun buttonClicked()
    {
        //setRandomSubredditData()
        setRandomPostData()
        //getSimilarSubreddits()
    }

    private fun setRandomSubredditData()
    {
        val call = service?.fetchRandomSubreddit()

        call?.enqueue(object : Callback<QueryResponse> {
            override fun onResponse(call: Call<QueryResponse>, response: Response<QueryResponse>) {
                randomSubData = response.body()!!
                randomSubName = randomSubData!!.rootData!!.childrenList?.get(0)?.childData?.subreddit

                setRandomPostData()
            }

            override fun onFailure(call: Call<QueryResponse>, t: Throwable) {
                textView!!.text = t.message
            }
        })
    }

    private fun setRandomPostData()
    {
        randomSubName = "pics"
        val call = service?.fetchSubredditRandomPost("pics")

        call?.enqueue(object : Callback<List<QueryResponse>> {
            override fun onResponse(call: Call<List<QueryResponse>>, response: Response<List<QueryResponse>>) {
                if(!response.isSuccessful)
                    Log.e("ICI", response.message())

                randomPostData = response.body()!!

                randomPostTitle = randomPostData!![0].rootData!!.childrenList?.get(0)?.childData?.title
                randomPostImageUrl = randomPostData!![0].rootData!!.childrenList?.get(0)?.childData?.url

                if (!randomPostImageUrl.isNullOrEmpty() && !randomPostImageUrl!!.contains(".jpg"))
                {
                    setRandomPostData()
                }
                else {
                    setTitleImage()
                }
            }

            override fun onFailure(call: Call<List<QueryResponse>>, t: Throwable) {
                textView!!.text = t.message
            }
        })
    }

    private fun setTitleImage()
    {
        Picasso.get()
            .load(randomPostImageUrl)
            .fit()
            .centerInside()
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .into(imageView1, object: com.squareup.picasso.Callback {
                override fun onSuccess() {
                    //set textView
                    textView!!.text = randomPostTitle

                    getSimilarSubreddits()
                }

                override fun onError(e: java.lang.Exception?) {
                    //do smth when there is picture loading error
                }
            });

    }

    private fun getSimilarSubreddits()
    {
        val call = service?.fetchSubredditsSearch("pics")

        call?.enqueue(object : Callback<QueryResponse> {
            override fun onResponse(call: Call<QueryResponse>, response: Response<QueryResponse>) {
                if (response.isSuccessful)
                    similarSubredditsData = response.body()!!

                setSubredditsNameList()
                setButtonText()
            }

            override fun onFailure(call: Call<QueryResponse>, t: Throwable) {
                textView!!.text = t.message
            }
        })
    }

    private fun setButtonText()
    {
        val correctAnswer = randomSubName
        val n = Random.nextInt(0,3)
        
        for ((index, button) in buttonList.withIndex()) {
            if (index == n)
                button.text = "r/$correctAnswer"
            else
                button.text = getRelatedSubName()
        }

        //clear list
        subredditsNameList.clear()
    }

    private fun setSubredditsNameList()
    {
        for (child in similarSubredditsData.rootData?.childrenList!!)
        {
            var subName = child?.childData?.subreddit
            if (!subName.isNullOrEmpty() && !subredditsNameList.contains(subName) && subName != randomSubName)
                subredditsNameList?.add(subName)
        }
    }

    private fun getRelatedSubName() : String
    {
        var relatedSubName : String
        var i : Int
        val childrenListSize = subredditsNameList.size

        i = Random.nextInt(childrenListSize - 1)
        relatedSubName = subredditsNameList[i]
        subredditsNameList.removeAt(i)

        return "r/$relatedSubName"
    }
}
