package com.android.systemui.lockscreen.widgets

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.CalendarContract
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.systemui.res.R
import java.util.*

class WidgetCalendarController(private val context: Context, private val view: View) {

    private val iconView: ImageView = view.findViewById(R.id.calendar_icon)
    private val titleView: TextView = view.findViewById(R.id.calendar_event_title)

    init {
        updateNextEvent()
        view.setOnClickListener { openCalendarApp() }
    }

    private fun updateNextEvent() {
        val projection = arrayOf(
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.ALL_DAY
        )

        val now = System.currentTimeMillis()
        val end = now + (30 * 24 * 60 * 60 * 1000L) // next 30 days

        val uri = CalendarContract.Instances.CONTENT_URI.buildUpon()
            .appendPath(now.toString())
            .appendPath(end.toString())
            .build()

        val selection = ("(${CalendarContract.Instances.ALL_DAY} = 1 OR " +
                         "${CalendarContract.Instances.TITLE} LIKE ?)")
        val selectionArgs = arrayOf("%birthday%")

        val cursor: Cursor? = context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            "${CalendarContract.Instances.BEGIN} ASC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val title = it.getString(0)
                val date = it.getLong(1)
                val cal = Calendar.getInstance().apply { timeInMillis = date }
                val formatted = android.text.format.DateFormat.format("MMM dd", cal)
                titleView.text = "$title • $formatted"
            } else {
                titleView.text = "No upcoming events"
            }
        } ?: run {
            titleView.text = "Calendar unavailable"
        }
    }

    private fun openCalendarApp() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_CALENDAR)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Calendar app not found
        }
    }

    fun destroy() {
        // Placeholder for observer logic if added
    }
}