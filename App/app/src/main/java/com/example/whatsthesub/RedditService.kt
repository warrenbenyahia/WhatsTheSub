package com.example.whatsthesub

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditService {

    @GET("/r/random.json")
    fun getRandomSubreddit(): Call<QueryResponse>

    @GET("/random.json")
    fun getRandomPost(): Call<List<QueryResponse>>

    @GET("/r/{subreddit}/random.json")
    fun getSubredditRandomPost(@Path("subreddit") subreddit: String): Call<List<QueryResponse>>

    @GET("/r/subreddits/search.json")
    fun getSubredditsSearch(
        @Query("q") query: String,
        @Query("limit") limit: Int = 50
    ): Call<QueryResponse>
}