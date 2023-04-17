package com.example.cobot19

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    data class RequestBody(@SerializedName("message") val message: String)
    data class ResponseData(@SerializedName("response") val response: String)

    @POST("/api/kotlin")
    fun sendPostRequest(@Body requestBody: RequestBody): Call<ResponseData>
}
