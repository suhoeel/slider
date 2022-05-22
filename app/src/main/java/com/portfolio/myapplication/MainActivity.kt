package com.portfolio.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val slider = findViewById<VerticalSlider>(R.id.slider)
        val slider2 = findViewById<VerticalSlider>(R.id.slider2)

        var lastStep = slider.getStep()
        var lastStep2 = slider2.getStep()
        slider.setSliderCallbackListener(object : SliderCallbackListener {
            override fun getCurrentY(yPos: Float) {
                Log.d("VIEW", "SliderY $yPos")
            }

            override fun getCurrentStep(step: Int) {
                lastStep = step
                if(step > lastStep2) {
                    slider2.setYPosWithVol(step)
                    lastStep2 = step
                } else if (step < lastStep2) {
                    slider2.setYPosWithVol(step)
                    lastStep2 = step
                }
                Log.d("VIEW", "SliderStep $step")
            }
        })

        slider2.setSliderCallbackListener(object : SliderCallbackListener {
            override fun getCurrentY(yPos: Float) {
                Log.d("VIEW", "SliderY2 $yPos")
            }

            override fun getCurrentStep(step: Int) {
                lastStep2 = step
                Log.d("VIEW", "SliderStep2 $step")
            }
        })

    }

}