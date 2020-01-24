package com.example.tanimals

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class weatherPicker : AppCompatActivity() {

    private var weatherData: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_picker)
    }


}
