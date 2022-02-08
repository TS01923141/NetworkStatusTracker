package com.example.networkstatustracker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.networkstatustracker.R

//參考實作
//https://markonovakovic.medium.com/android-better-internet-connection-monitoring-with-kotlin-flow-feac139e2a3
private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        with(viewModel) {
            state.observe(this@MainActivity) { state ->
                when(state) {
                    MyState.Fetched -> Log.d(TAG, "onCreate: Fetched")
                    MyState.Error -> Log.d(TAG, "onCreate: Error")
                }
            }
        }
    }
}