package com.example.whatsthesub

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RedditService {

    @GET("/r/{subreddit}/about.json")
    fun getSubredditAbout(@Path("subreddit") subreddit: String): Call<RedditResponse>
}