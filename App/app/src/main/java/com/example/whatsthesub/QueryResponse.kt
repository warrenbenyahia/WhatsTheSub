package com.example.whatsthesub

import com.google.gson.annotations.SerializedName

class QueryResponse {
    @SerializedName("data")
    var rootData: RootData? = null
}

class RootData {
    @SerializedName("children")
    var childrenList: MutableList<Child?>? = null
}

class Child {
    @SerializedName("data")
    var childData: ChildData? = null
}

class ChildData {
    @SerializedName("subreddit")
    var subreddit: String? = null

    @SerializedName("subreddit_name_prefixed")
    var subredditPre: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("url")
    var url: String? = null
}

