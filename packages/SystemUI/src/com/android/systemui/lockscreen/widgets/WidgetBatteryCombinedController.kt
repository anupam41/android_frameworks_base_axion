package com.android.systemui.lockscreen.widgets

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import com.android.systemui.util.RingProgressView
import com.android.systemui.res.R

class WidgetBatteryCombinedController(private val context: Context, private val view: View) {

    private val deviceRing = view.findViewById<RingProgressView>(R.id.device_battery_ring)
    private val deviceIcon = view.findViewById<ImageView>(R.id.device_icon)
    private val twsRing = view.findViewById<RingProgressView>(R.id.tws_battery_ring)
    private val twsIcon = view.findViewById<ImageView>(R.id.tws_icon)

    private val receiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            updateBatteryInfo()
        }
    }

    init {
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        updateBatteryInfo()
    }

    fun destroy() {
        context.unregisterReceiver(receiver)
    }

    private fun updateBatteryInfo() {
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        val devices = btAdapter?.bondedDevices ?: emptySet()

        val tws = devices.firstOrNull { it.isConnected() && it.name.contains("buds", true) }
        val watch = devices.firstOrNull { it.isConnected() && it.name.contains("watch", true) }

        val localBattery = getLocalDeviceBatteryLevel()

        val leftIconRes: Int
        val rightIconRes: Int
        val leftLevel: Int
        val rightLevel: Int

        when {
            watch != null && tws != null -> {
                leftIconRes = R.drawable.ic_watch
                rightIconRes = R.drawable.ic_tws_default
                leftLevel = watch.batteryLevel
                rightLevel = tws.batteryLevel
            }
            watch != null -> {
                leftIconRes = R.drawable.ic_watch
                rightIconRes = R.drawable.ic_device
                leftLevel = watch.batteryLevel
                rightLevel = localBattery
            }
            tws != null -> {
                leftIconRes = R.drawable.ic_device
                rightIconRes = R.drawable.ic_tws_default
                leftLevel = localBattery
                rightLevel = tws.batteryLevel
            }
            else -> {
                leftIconRes = R.drawable.ic_device
                rightIconRes = R.drawable.ic_device
                leftLevel = localBattery
                rightLevel = 0
            }
        }

        // Apply icons and battery levels
        deviceIcon.setImageResource(leftIconRes)
        twsIcon.setImageResource(rightIconRes)
        deviceRing.animateTo(leftLevel)
        twsRing.animateTo(rightLevel)

        // Accessibility labels
        deviceRing.contentDescription = "Battery: $leftLevel%"
        twsRing.contentDescription = "Battery: $rightLevel%"

        // Visibility control for second ring
        if (watch == null && tws == null) {
            twsRing.visibility = View.GONE
            twsIcon.visibility = View.GONE
        } else {
            twsRing.visibility = View.VISIBLE
            twsIcon.visibility = View.VISIBLE
        }
    }

    private fun getLocalDeviceBatteryLevel(): Int {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val battery = context.registerReceiver(null, filter)
        val level = battery?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = battery?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level >= 0 && scale > 0) (level * 100 / scale) else 0
    }

    private fun BluetoothDevice.isConnected(): Boolean {
        return try {
            val method = javaClass.getMethod("isConnected")
            method.invoke(this) as? Boolean ?: false
        } catch (e: Exception) {
            false
        }
    }

    private val BluetoothDevice.batteryLevel: Int
        get() = try {
            val method = javaClass.getMethod("getBatteryLevel")
            (method.invoke(this) as? Int)?.coerceIn(0, 100) ?: 0
        } catch (e: Exception) {
            0
        }

    private fun RingProgressView.animateTo(target: Int) {
        animate().cancel()
        animate().setDuration(400)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withStartAction { }
            .withEndAction { setProgress(target) }
            .start()
    }
}
