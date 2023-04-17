package com.example.cobot19

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jackandphantom.joystickview.JoyStickView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


private const val TAG = "com.example.cobot19.MainActivity"
private const val REQUEST_ENABLE_BT = 1
private const val REQUEST_PERMISSION =2
class MainActivity : AppCompatActivity() {



    private var bluetoothAdapter: BluetoothAdapter? = null
    private var socket: BluetoothSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val connectButton = findViewById<ImageButton>(R.id.connect_button)
        val statusText = findViewById<TextView>(R.id.status_text)
        val buttonFL = findViewById<ImageButton>(R.id.button_fl)
        val buttonFF = findViewById<ImageButton>(R.id.button_f)
        val buttonFR = findViewById<ImageButton>(R.id.button_fr)
        val buttonLL = findViewById<ImageButton>(R.id.button_l)
        val buttonSTOP = findViewById<ImageButton>(R.id.button_stop)
        val buttonRR = findViewById<ImageButton>(R.id.button_r)
        val buttonBL = findViewById<ImageButton>(R.id.button_bl)
        val buttonBB = findViewById<ImageButton>(R.id.button_b)
        val buttonBR = findViewById<ImageButton>(R.id.button_br)

        val joyStickView= findViewById<JoyStickView>(R.id.joy)
        joyStickView.setOnMoveListener { angle, strength -> }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.18.15:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)


        fun sendMessage(message: String) {
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

        val btn_wifi = findViewById<ImageButton>(R.id.connect_wifi)

        btn_wifi.setOnClickListener {
            sendMessage("wifi connection")
        }



        connectButton.setOnClickListener {
            val intent = Intent(this, BluetoothActivity::class.java)
            startActivity(intent)

        }

        buttonFL.setOnClickListener { sendMessage("FL") }
        buttonFF.setOnClickListener { sendMessage("FF") }
        buttonFR.setOnClickListener { sendMessage("FR") }
        buttonLL.setOnClickListener { sendMessage("LL") }
        buttonSTOP.setOnClickListener { sendMessage("STOP") }
        buttonRR.setOnClickListener { sendMessage("RR") }
        buttonBL.setOnClickListener { sendMessage("BL") }
        buttonBB.setOnClickListener { sendMessage("BB") }
        buttonBR.setOnClickListener { sendMessage("BR") }
    }













        // Set up the direction buttons





    override fun onDestroy() {
        super.onDestroy()
        try {
            socket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing socket: ${e.message}")
        }
    }
}