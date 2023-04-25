package com.example.cobot19

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.*
import java.io.IOException

class WifiCommunicationStrategy : CommunicationStrategy {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.16.6:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(ApiService::class.java)

    override fun SendMessage(message: String) {
        val requestBody = ApiService.RequestBody(message)
        api.sendPostRequest(requestBody).enqueue(object : Callback<ApiService.ResponseData> {
            override fun onResponse(
                call: Call<ApiService.ResponseData>,
                response: Response<ApiService.ResponseData>
            ) {
                val responseData = response.body()
                Log.d("Response", responseData?.response ?: "")
            }

            override fun onFailure(call: Call<ApiService.ResponseData>, t: Throwable) {
                Log.e("Error", t.message ?: "Unknown error")
            }
        })
    }
}