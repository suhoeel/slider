package com.portfolio.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {

    private val parentJob = SupervisorJob()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scope = CoroutineScope(Dispatchers.Main + parentJob)
        scope.launch {
            val a: Job = launch {
                Log.d("TEST", "a")
                delay(2000L)
            }

            val b: Job = launch {
                delay(2000L)
                Log.d("TEST", "b")
            }

        }
//            Log.d("TEST", "${this.coroutineContext}")

    }

}