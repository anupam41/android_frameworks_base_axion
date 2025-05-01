package com.android.systemui.lockscreen.widgets

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.systemui.res.R
import java.util.*
import java.util.concurrent.TimeUnit

class WidgetWellbeingController(private val context: Context, private val view: View) {

    private val icon: ImageView = view.findViewById(R.id.wellbeing_icon)
    private val label: TextView = view.findViewById(R.id.wellbeing_text)

    init {
        updateScreenTime()
    }

    private fun updateScreenTime() {
        val statsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val beginTime = calendar.timeInMillis

        val usageStatsList = statsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            beginTime,
            endTime
        )

        var totalTime = 0L
        usageStatsList?.forEach { stat ->
            totalTime += stat.totalTimeInForeground
        }

        val hours = TimeUnit.MILLISECONDS.toHours(totalTime)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime) % 60
        val screenTimeText = String.format("%dh %02dm", hours, minutes)

        icon.setImageResource(R.drawable.ic_wellbeing)
        label.text = screenTimeText
    }
}
