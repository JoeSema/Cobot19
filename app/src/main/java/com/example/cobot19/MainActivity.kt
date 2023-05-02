package com.example.cobot19

import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.jackandphantom.joystickview.JoyStickView
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.*
import java.io.IOException



private const val TAG = "com.example.cobot19.MainActivity"
private const val PICK_IMAGE_REQUEST = 1
private const val REQUEST_ENABLE_BT = 1
private const val REQUEST_PERMISSION =2
private var communicationStrategy: CommunicationStrategy = WifiCommunicationStrategy()


class MainActivity : AppCompatActivity(){



    lateinit var toggle : ActionBarDrawerToggle
    private lateinit var joystickView: JoyStickView
    private lateinit var buttonsLayout: LinearLayout
    private lateinit var showJoystickButton: Button
    private lateinit var showButtonsButton: Button
    private lateinit var scrollView: ScrollView
    private lateinit var loginPopup: Dialog
    private lateinit var signupPopup: Dialog
    private var macAddress: String = "C8:F0:9E:4E:2A:26"

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
        val login = findViewById<ImageButton>(R.id.menu_button)
        val blue= findViewById<Button>(R.id.button_blue)
        val green= findViewById<Button>(R.id.button_green)
        val red= findViewById<Button>(R.id.button_red)
        val pomp= findViewById<ImageButton>(R.id.button_p)
        val drawerLayout : DrawerLayout= findViewById(R.id.drawerLayout)
        val navView : NavigationView= findViewById(R.id.nav_view)
        fun goToSite() {
            val url = "https://www.usj.edu.lb/news.php?id=9819"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }


        toggle= ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_picture ->{val intent = Intent()
                intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)}
                R.id.nav_home ->Toast.makeText(applicationContext, "Clicked Home", Toast.LENGTH_SHORT).show()
                R.id.nav_location ->Toast.makeText(applicationContext, "Clicked Location", Toast.LENGTH_SHORT).show()
                R.id.nav_login ->showLoginPopup()
                R.id.nav_rate_us ->Toast.makeText(applicationContext, "Clicked Rate Us", Toast.LENGTH_SHORT).show()
                R.id.nav_settings ->Toast.makeText(applicationContext, "Clicked Settings", Toast.LENGTH_SHORT).show()
                R.id.nav_share ->Toast.makeText(applicationContext, "Clicked Share", Toast.LENGTH_SHORT).show()

                R.id.nav_about_us-> goToSite()
            }
            true
        }






        joystickView = findViewById(R.id.joy)
        buttonsLayout = findViewById(R.id.my_buttons)
        showJoystickButton = findViewById(R.id.joystick_button)
        showButtonsButton = findViewById(R.id.button_layout_button)
        scrollView=findViewById(R.id.scroll_view)

        // set click listeners for the buttons
        showJoystickButton.setOnClickListener {
            joystickView.visibility = View.VISIBLE
            buttonsLayout.visibility = View.GONE
        }
        showButtonsButton.setOnClickListener {
            Log.d(TAG, "showButtonsButton clicked")
            joystickView.visibility = View.GONE
            buttonsLayout.visibility = View.VISIBLE
            scrollView.post { val y = buttonsLayout.y.toInt()
                // Scroll to the top of the buttons layout
                scrollView.scrollTo(0, y)}
            joystickView.visibility = View.GONE
        }


        fun sendMessage(message: String) {
            when (communicationStrategy) {
                is WifiCommunicationStrategy -> {
                    // Send message via WiFi
                    (communicationStrategy as WifiCommunicationStrategy).sendMessage(message)
                }
                is BluetoothCommunicationStrategy -> {
                    // Send message via Bluetooth
                    (communicationStrategy as BluetoothCommunicationStrategy).sendMessage(message)
                }
            }
        }


        val joyStickView= findViewById<JoyStickView>(R.id.joy)
        var previousDirection: String? =null
        joyStickView.setOnMoveListener { angle, strength ->

            val direction = when(angle.toInt()) {
                in 23..67 -> "FR" // Forward-Forward
                in 68..112 -> "FF " // Forward-Left
                in 113..157 -> "FL" // Left-Left
                in 158..202 -> "LL" // Backward-Left
                in 203..247 -> "BL" // Backward-Backward
                in 248..292 -> "BB" // Backward-Right
                in 293..337 -> "BR" // Right-Right
                else -> "RR" // Forward-Right
            }
            if (direction != previousDirection) {
                sendMessage(direction)
                previousDirection = direction
            }

        }

        val btn_wifi = findViewById<ImageButton>(R.id.connect_wifi)

        btn_wifi.setOnClickListener {
            communicationStrategy=WifiCommunicationStrategy()
            sendMessage("wifi connection")
        }




        connectButton.setOnClickListener {
            communicationStrategy=BluetoothCommunicationStrategy(this, macAddress)
            (communicationStrategy as BluetoothCommunicationStrategy).connect()

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
        red.setOnClickListener { sendMessage("Red") }
        green.setOnClickListener { sendMessage("Green") }
        blue.setOnClickListener { sendMessage("Blue") }
        pomp.setOnClickListener { sendMessage("P") }

        loginPopup = Dialog(this)
        loginPopup.setContentView(R.layout.login_dialog)
        val loginButton = loginPopup.findViewById<Button>(R.id.login_button)
        val loginUsername = loginPopup.findViewById<EditText>(R.id.email_edittext)
        val loginPassword = loginPopup.findViewById<EditText>(R.id.password_edittext)
        val signupTextView = loginPopup.findViewById<TextView>(R.id.signup_textview)
        loginButton.setOnClickListener {
            sendLoginInfoToServer(loginUsername.text.toString(), loginPassword.text.toString())
            loginPopup.dismiss()
        }
        signupTextView.setOnClickListener {
            loginPopup.dismiss()
            showSignupPopup()
        }

        signupPopup = Dialog(this)
        signupPopup.setContentView(R.layout.signup_dialog)
        val signupButton = signupPopup.findViewById<Button>(R.id.buttonSignUp)
        val signupUsername = signupPopup.findViewById<EditText>(R.id.editTextUsernameSignUp)
        val signupPassword = signupPopup.findViewById<EditText>(R.id.editTextPasswordSignUp)
        val loginTextView = signupPopup.findViewById<TextView>(R.id.signup_textview)
        signupButton.setOnClickListener {
            sendSignupInfoToServer(signupUsername.text.toString(), signupPassword.text.toString())
            signupPopup.dismiss()
        }
        loginTextView.setOnClickListener {
            signupPopup.dismiss()
            showLoginPopup()
        }



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoginPopup() {
        loginPopup.show()

    }

    private fun showSignupPopup() {
        signupPopup.show()
    }
    private val retrofit1 = Retrofit.Builder()
        .baseUrl("http://192.168.18.15:3400")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private fun sendLoginInfoToServer(username: String, password: String) {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val menu = navigationView.menu
        val usernames= findViewById<TextView>(R.id.user_name)
        val profile= findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.nav_picture)
        val api1 = retrofit1.create(ApiService::class.java)
        val loginItem = menu?.findItem(R.id.nav_login)
        val logoutItem = menu?.findItem(R.id.nav_logout)
        val loginRequest = ApiService.LoginRequest(username, password)
        api1.loginUser(loginRequest).enqueue(object : Callback<ApiService.LoginResponse> {
            override fun onResponse(call: Call<ApiService.LoginResponse>, response: Response<ApiService.LoginResponse>) {
                val loginResponse = response.body()
                if (loginResponse != null && loginResponse.success) {
                    // Login successful
                    usernames.setText(username)
                    profile.setImageResource(R.drawable.avatar1)
                    loginItem?.isVisible = false
                    logoutItem?.isVisible = true
                    Toast.makeText(this@MainActivity, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    // Login failed
                    loginItem?.isVisible = true
                    logoutItem?.isVisible = false
                    Toast.makeText(this@MainActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.LoginResponse>, t: Throwable) {
                // Network or other error occurred
                Toast.makeText(this@MainActivity, "Error: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendSignupInfoToServer(username: String, password: String) {
        val api = retrofit1.create(ApiService::class.java)
        val registerRequest =
            ApiService.RegisterRequest(username, username + "@example.com", password)
        api.signup(registerRequest).enqueue(object : Callback<ApiService.RegisterResponse> {
            override fun onResponse(call: Call<ApiService.RegisterResponse>, response: Response<ApiService.RegisterResponse>) {
                val registerResponse = response.body()
                if (registerResponse != null && registerResponse.success) {
                    // Registration successful
                    Toast.makeText(this@MainActivity, "Signed up successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    // Registration failed
                    Toast.makeText(this@MainActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.RegisterResponse>, t: Throwable) {
                // Network or other error occurred
                Toast.makeText(this@MainActivity, "Error: " + t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

}