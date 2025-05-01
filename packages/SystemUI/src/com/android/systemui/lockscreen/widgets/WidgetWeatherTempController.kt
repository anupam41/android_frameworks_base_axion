package com.android.systemui.lockscreen.widgets

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.widget.TextView
import com.android.internal.util.omni.OmniJawsClient
import com.android.systemui.res.R

class WidgetWeatherTempController(
    private val context: Context,
    private val view: View
) : OmniJawsClient.OmniJawsObserver {

    private val weatherText: TextView = view.findViewById(R.id.weather_temp_text)
    private val client: OmniJawsClient = OmniJawsClient.getInstance(context)
    private val observer: ContentObserver

    init {
        client.addObserver(this)
        observer = object : ContentObserver(Handler()) {
            override fun onChange(selfChange: Boolean) {
                queryAndUpdate()
            }
        }
        context.contentResolver.registerContentObserver(
            Settings.System.getUriFor(OmniJawsClient.WEATHER_ENABLED),
            false, observer
        )
        queryAndUpdate()
    }

    private fun queryAndUpdate() {
        if (client.isOmniJawsEnabled) {
            client.queryWeather()
        } else {
            fadeInText("--°")
        }
    }

    override fun weatherUpdated() {
        val weatherInfo = client.weatherInfo ?: return
        val temp = weatherInfo.temp
        val displayText = if (temp.isNotBlank()) "$temp°" else "--°"
        fadeInText(displayText)
    }

    override fun weatherError(errorReason: Int) {
        fadeInText("--°")
    }

    private fun fadeInText(text: String) {
        weatherText.alpha = 0f
        weatherText.text = text
        weatherText.animate().alpha(1f).setDuration(300).start()
    }

    fun destroy() {
        client.removeObserver(this)
        context.contentResolver.unregisterContentObserver(observer)
    }
}