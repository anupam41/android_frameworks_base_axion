package com.android.systemui.lockscreen.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.internal.util.omni.OmniJawsClient
import com.android.systemui.res.R

class WidgetWeatherHumidityController(private val context: Context, private val view: View) {

    private val icon: ImageView = view.findViewById(R.id.round_icon)
    private val label: TextView = view.findViewById(R.id.round_label)
    private val client = OmniJawsClient(context)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            updateHumidity()
        }
    }

    init {
        context.registerReceiver(receiver, IntentFilter(OmniJawsClient.ACTION_WEATHER_UPDATE))
        updateHumidity()
    }

    fun destroy() {
        context.unregisterReceiver(receiver)
    }

    private fun updateHumidity() {
        if (!client.isOmniJawsEnabled) {
            icon.setImageResource(R.drawable.ic_humidity)
            label.text = "--%"
            return
        }

        val weatherInfo = client.weatherInfo ?: return
        val humidity = weatherInfo.humidity ?: "--%"

        icon.setImageResource(R.drawable.ic_humidity)
        label.text = humidity
    }
}
