package com.tukorea.Fearow

data class ApplicationItem(
    val id: String,
    val postId: String,
    val applicantId: String,
    val postOwnerId: String,
    val application: String,
    var status: ApplicationStatus
)
