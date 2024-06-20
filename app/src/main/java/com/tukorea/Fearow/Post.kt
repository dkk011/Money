package com.tukorea.Fearow

data class Post(val postId: Int, val userId: String, val title: String, val content: String, val price: Int, val imageUrl: String) {
    constructor() : this(0, "", "", "", 0, "")
}
