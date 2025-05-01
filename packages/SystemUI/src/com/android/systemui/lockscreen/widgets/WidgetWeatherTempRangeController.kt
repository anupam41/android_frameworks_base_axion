package com.android.systemui.lockscreen.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.widget.TextView
import com.android.internal.util.omni.OmniJawsClient
import com.android.systemui.res.R

class WidgetWeatherTempRangeController(private val context: Context, private val view: View) {

private val highText: TextView = view.findViewById(R.id.temp_high)
private val lowText: TextView = view.findViewById(R.id.temp_low)
private val client = OmniJawsClient(context)

private val receiver = object : BroadcastReceiver() {
    override fun onReceive(c: Context?, intent: Intent?) {
        updateTempRange()
    }
}

init {
    context.registerReceiver(receiver, IntentFilter(OmniJawsClient.ACTION_WEATHER_UPDATE))
    updateTempRange()
}

fun destroy() {
    context.unregisterReceiver(receiver)
}

private fun updateTempRange() {
    if (!client.isOmniJawsEnabled) {
        highText.text = "--"
        lowText.text = "--"
        return
    }

    val info = client.weatherInfo ?: return
    val high = info.highTemp?.trim() ?: "--"
    val low = info.lowTemp?.trim() ?: "--"

    highText.text = high
    lowText.text = low
}

}

