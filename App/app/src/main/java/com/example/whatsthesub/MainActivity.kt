package com.example.whatsthesub

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Contacts
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import java.lang.Exception
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var textView: TextView? = null

    private var imageView1: ImageView? = null

    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button

    private lateinit var buttonList: List<Button>

    private lateinit var service : RedditService

    private lateinit var randomSubData : QueryResponse
    private lateinit var randomSubName : String

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

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(RedditService::class.java)

        if (retrofit != null)
        {
            service = retrofit
        }
        else {
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Retrofit builder failed")
                .setNegativeButton("Close") { _, _ -> }
                .show()
        }
    }

    private fun buttonClicked()
    {
        setRandomSubredditData()
    }

    //Retrieve a random subreddit
    private fun setRandomSubredditData()
    {
        val call = service.getRandomSubreddit()

        call.enqueue(object : Callback<QueryResponse> {
            override fun onResponse(call: Call<QueryResponse>, response: Response<QueryResponse>) {

                if (!response.isSuccessful ||  response.body() == null)
                    throw Exception("GET RANDOM SUBREDDIT FAILED")

                randomSubData = response.body()!!

                val sub = randomSubData.rootData!!.childrenList?.get(0)?.childData?.subreddit

                if (!sub.isNullOrEmpty())
                    randomSubName = sub
                else
                    setRandomSubredditData()

                setRandomPostData()
            }

            override fun onFailure(call: Call<QueryResponse>, t: Throwable) {
                textView!!.text = t.message
            }
        })
    }

    //Retrieve a random post from the random subreddit
    private fun setRandomPostData()
    {
        randomSubName = "pics"
        val call = service.getSubredditRandomPost("pics")

        call.enqueue(object : Callback<List<QueryResponse>> {
            override fun onResponse(call: Call<List<QueryResponse>>, response: Response<List<QueryResponse>>)
            {
                randomPostData = response.body()!!

                randomPostTitle = randomPostData!![0].rootData!!.childrenList?.get(0)?.childData?.title
                randomPostImageUrl = randomPostData!![0].rootData!!.childrenList?.get(0)?.childData?.url

                if (!randomPostImageUrl.isNullOrEmpty() && !randomPostImageUrl!!.contains(".jpg"))
                {
                    //Retrieve another post if the current one is not an image
                    setRandomPostData()
                }
                else {
                    fetchSimilarSubreddits()
                }
            }

            override fun onFailure(call: Call<List<QueryResponse>>, t: Throwable)
            {
                textView!!.text = t.message
            }
        })
    }

    //Retrieve a list of subreddits via a search with the random subreddit name
    private fun fetchSimilarSubreddits()
    {
        val call = service.getSubredditsSearch("pics")

        call.enqueue(object : Callback<QueryResponse> {
            override fun onResponse(call: Call<QueryResponse>, response: Response<QueryResponse>) {
                if (response.isSuccessful) {
                    similarSubredditsData = response.body()!!
                    setSubredditsNameList()
                }
            }

            override fun onFailure(call: Call<QueryResponse>, t: Throwable) {
                textView!!.text = t.message
            }
        })
    }

    //Set the list of similar subreddits
    private fun setSubredditsNameList()
    {
        for (child in similarSubredditsData.rootData?.childrenList!!)
        {
            var subName = child?.childData?.subreddit

            // Add if the subreddit is not already in the list and note the random one we retrieved at the beginning
            if (!subName.isNullOrEmpty() && !subredditsNameList.contains(subName) && subName != randomSubName)
                subredditsNameList.add(subName)
        }

        setUIElements()
    }

    private fun setUIElements()
    {
       var answersList = getSubredditsAnswers()

        //Load and set Image
        Picasso.get()
            .load(randomPostImageUrl)
            .fit()
            .centerInside()
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .into(imageView1, object: com.squareup.picasso.Callback {
                override fun onSuccess() {
                    //set textView
                    textView!!.text = randomPostTitle

                    for ((index, answer) in answersList.withIndex())
                    {
                        buttonList[index].text = answer
                    }
                }

                override fun onError(e: java.lang.Exception?) {
                    //do smth when there is picture loading error
                }
            });
    }

    private fun getSubredditsAnswers() : List<String>
    {
        var answers : MutableList<String> = mutableListOf()

        val n = Random.nextInt(buttonList.size)

        for (i in buttonList.indices)
        {
            if (i == n)
                answers.add("r/$randomSubName")
            else
                answers.add(getRelatedSubName())
        }

        return answers
    }

    private fun getRelatedSubName() : String
    {
        var relatedSubName : String
        val childrenListSize = subredditsNameList.size

        //i = Random.nextInt(childrenListSize - 1)
        var i : Int = Random.nextInt(childrenListSize)

        relatedSubName = subredditsNameList[i]
        subredditsNameList.removeAt(i)

        return "r/$relatedSubName"
    }
}
