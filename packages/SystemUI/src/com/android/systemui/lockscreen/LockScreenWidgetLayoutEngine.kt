package com.android.systemui.lockscreen

class LockScreenWidgetLayoutEngine {
    companion object {
        private const val MAX_UNITS = 4
    }

    fun arrangeWidgets(widgets: List<WidgetAction>): List<WidgetAction> {
        val result = mutableListOf<WidgetAction>()
        var usedUnits = 0

        for (widget in widgets) {
            val units = if (widget.shape == WidgetShape.PILL) 2 else 1
            if (usedUnits + units <= MAX_UNITS) {
                result.add(widget)
                usedUnits += units
            } else {
                break
            }
        }

        return result
    }
}