package com.study.chapter3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.gson.Gson
import com.study.chapter3.databinding.ActivityMainBinding
import okhttp3.Callback
import okhttp3.OkHttpClient
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class MainActivity : AppCompatActivity() {

    var serverHost = ""
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.serverHostEditText.addTextChangedListener {
            serverHost = it.toString()
        }

        binding.connectLocalButton.setOnClickListener {
            val request = okhttp3.Request.Builder()
                .url("http://${serverHost}:3001")
                .build()

            val callback = object : Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "수신에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }

                    Log.e("Client", e.toString())
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    if (response.isSuccessful) {
                        val response = response.body?.string()

                        val user = Gson().fromJson(response, User::class.java)
                        val name = user.name
                        val age = user.age
                        val str = "이름: $name\n나이: $age"
                        runOnUiThread {
                            binding.informationTextView.isVisible = true
                            binding.informationTextView.text = str

                            binding.serverHostEditText.isVisible = false
                            binding.connectLocalButton.isVisible = false
                        }

                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "수신에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            client.newCall(request).enqueue(callback)

        }


    }
}