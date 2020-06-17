package com.example.whatsthesub

import com.google.gson.annotations.SerializedName

class RedditResponse {
    @SerializedName("data")
    var data: Data? = null
}

class Data {
    @SerializedName("title")
    var title: String? = null

    @SerializedName("description")
    var description: String? = null
}
