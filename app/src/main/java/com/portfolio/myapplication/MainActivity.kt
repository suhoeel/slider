package com.portfolio.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val slider = findViewById<VerticalSlider>(R.id.slider)
        val slider2 = findViewById<VerticalSlider>(R.id.slider2)
        val text = findViewById<TextView>(R.id.text_slider)
        val text2 = findViewById<TextView>(R.id.text_slider2)


        slider.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            slider.setStep(4)

        }
        slider2.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            slider2.setStep(6)
        }

        slider.setSliderCallbackListener(object : SliderCallbackListener {
            override fun getCurrentY(yPos: Float) {
//                Log.d("VIEW", "SliderY $yPos")
            }

            override fun getCurrentStep(step: Int) {
                slider2.setStep(step)
                text.text = step.toString()
                Log.d("VIEW", "SliderStep $step")
            }
        })

        slider2.setSliderCallbackListener(object : SliderCallbackListener {
            override fun getCurrentY(yPos: Float) {
//                Log.d("VIEW", "SliderY2 $yPos")
            }

            override fun getCurrentStep(step: Int) {
                text2.text = step.toString()
                Log.d("VIEW", "SliderStep2 $step")
            }
        })

    }

}