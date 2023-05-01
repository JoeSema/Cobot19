package com.example.cobot19

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    data class RegisterRequest(val username: String, val email: String, val password: String)
    data class RegisterResponse(val success: Boolean)

    data class LoginRequest(val email: String, val password: String)
    data class LoginResponse(val success: Boolean)

    data class RequestBody(@SerializedName("message") val message: String)
    data class ResponseData(@SerializedName("response") val response: String)

    @POST("/api/kotlin")
    fun sendPostRequest(@Body requestBody: RequestBody): Call<ResponseData>

    @POST("/api/signup")
    fun signup(@Body requestBody: RegisterRequest): Call<RegisterResponse>

    @POST("/api/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
}
