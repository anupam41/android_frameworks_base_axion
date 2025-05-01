package com.android.systemui.lockscreen.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.internal.util.omni.OmniJawsClient
import com.android.systemui.res.R

class WidgetWeatherController(private val context: Context, private val view: View) {

    private val icon: ImageView = view.findViewById(R.id.weather_icon)
    private val text: TextView = view.findViewById(R.id.weather_condition)
    private val client = OmniJawsClient(context)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            updateWeather()
        }
    }

    init {
        context.registerReceiver(receiver, IntentFilter(OmniJawsClient.ACTION_WEATHER_UPDATE))
        updateWeather()
    }

    fun destroy() {
        context.unregisterReceiver(receiver)
    }

    private fun updateWeather() {
        if (!client.isOmniJawsEnabled) {
            text.text = context.getString(R.string.weather_unavailable)
            icon.setImageResource(R.drawable.ic_weather_na)
            return
        }

        val weatherInfo = client.weatherInfo ?: return
        text.text = weatherInfo.condition
        val iconDrawable: Drawable? = weatherInfo.conditionDrawable
        if (iconDrawable != null) {
            icon.setImageDrawable(iconDrawable)
        } else {
            icon.setImageResource(R.drawable.ic_weather_na)
        }
    }
}