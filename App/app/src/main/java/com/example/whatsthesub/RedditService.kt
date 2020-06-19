package com.example.whatsthesub

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditService {

    @GET("/r/random.json")
    fun fetchRandomSubreddit(): Call<QueryResponse>

    @GET("/r/{subreddit}/random.json")
    fun fetchSubredditRandomPost(@Path("subreddit") subreddit: String): Call<List<QueryResponse>>

    @GET("/r/subreddits/search.json")
    fun fetchSubredditsSearch(
        @Query("q") query: String
    ): Call<QueryResponse>
}