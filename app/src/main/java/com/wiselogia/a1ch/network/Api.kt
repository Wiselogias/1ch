package com.wiselogia.a1ch.network

import com.wiselogia.a1ch.Message
import com.wiselogia.a1ch.models.SendModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.http.*
import java.io.File

interface Api {
    @GET("/1ch")
    suspend fun getMessages(
        @Query("limit") limit: Int,
        @Query("lastKnownId") lastKnownId: Int
    ) : List<Message>

    @POST("/1ch")
    suspend fun postTextMessage(
        @Body data: SendModel
    )

    @Multipart
    @POST("/1ch")
    suspend fun postImageMessage(
        @Part from: MultipartBody.Part,
        @Part data: MultipartBody.Part
    )
}