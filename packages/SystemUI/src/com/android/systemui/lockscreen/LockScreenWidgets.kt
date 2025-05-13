package com.android.systemui.lockscreen

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.GridLayout

class LockScreenWidgets(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val widgetContainer: GridLayout
        get() = findViewById(R.id.main_widgets_container)

    private var isPreviewMode: Boolean = false

    fun init(preview: Boolean = false) {
        isPreviewMode = preview
        LockScreenWidgetsController.addView(this, isPreview = preview)
    }

    fun deInit() {
        LockScreenWidgetsController.removeView(this)
    }

    fun getWidgetContainer(): GridLayout {
        return widgetContainer
    }

    fun isInPreviewMode(): Boolean = isPreviewMode
}