/*
 * Copyright (C) 2025 the AxionAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.systemui.lockscreen

import android.view.View
import android.content.IntentFilter
import android.media.AudioManager

import com.android.systemui.Dependency
import com.android.systemui.res.R
import com.android.systemui.statusbar.connectivity.*
import com.android.systemui.statusbar.policy.*
import com.android.systemui.util.*

enum class WidgetShape {
    ROUND,
    PILL
}

enum class WidgetAction(
    val activeRes: Int,
    val inactiveRes: Int,
    val onClick: (LockScreenWidgetsController.ViewController) -> Unit,
    val onLongClick: ((LockScreenWidgetsController.ViewController, View) -> Boolean)? = null,
    val registerCallback: (LockScreenWidgetsController.ViewController) -> Unit = {},
    val unregisterCallback: (LockScreenWidgetsController.ViewController) -> Unit = {}
) {
    WIFI(
        shape = WidgetShape.ROUND,
        LsWidgetsRes.WIFI_ACTIVE, LsWidgetsRes.WIFI_INACTIVE,
        onClick = { it.toggleWiFi() },
        onLongClick = { c, v -> c.showInternetDialog(v); true },
        registerCallback = { controller ->
            Dependency.get(NetworkController::class.java).addCallback(controller.callbacks.wifiSignalCallback)
        },
        unregisterCallback = { controller ->
            Dependency.get(NetworkController::class.java).removeCallback(controller.callbacks.wifiSignalCallback)
        }
    ),
    DATA(
        shape = WidgetShape.ROUND,
        LsWidgetsRes.DATA_ACTIVE, LsWidgetsRes.DATA_INACTIVE,
        onClick = { it.toggleMobileData() },
        onLongClick = { c, v -> c.showInternetDialog(v); true },
        registerCallback = { controller ->
            Dependency.get(NetworkController::class.java).addCallback(controller.callbacks.cellSignalCallback)
        },
        unregisterCallback = { controller ->
            Dependency.get(NetworkController::class.java).removeCallback(controller.callbacks.cellSignalCallback)
        }
    ),
    RINGER(
        shape = WidgetShape.ROUND,
        LsWidgetsRes.RINGER_ACTIVE, LsWidgetsRes.RINGER_INACTIVE,
        onClick = { it.toggleRingerMode() },
        registerCallback = { controller ->
            val filter = IntentFilter(AudioManager.INTERNAL_RINGER_MODE_CHANGED_ACTION)
            controller.context.registerReceiver(controller.callbacks.ringerModeReceiver, filter)
            controller.isRingerReceiverRegistered = true
        },
        unregisterCallback = { controller ->
            if (controller.isRingerReceiverRegistered) {
                controller.context.unregisterReceiver(controller.callbacks.ringerModeReceiver)
                controller.isRingerReceiverRegistered = false
            }
        }
    ),
    BT(
        shape = WidgetShape.ROUND,
        LsWidgetsRes.BT_ACTIVE, LsWidgetsRes.BT_INACTIVE,
        onClick = { it.toggleBluetooth() },
        onLongClick = { c, v -> c.showBluetoothDialog(v); true },
        registerCallback = { controller ->
            Dependency.get(BluetoothController::class.java).addCallback(controller.callbacks.btCallback)
        },
        unregisterCallback = { controller ->
            Dependency.get(BluetoothController::class.java).removeCallback(controller.callbacks.btCallback)
        }
    ),
    TORCH(
        shape = WidgetShape.ROUND,
        LsWidgetsRes.TORCH_RES_ACTIVE, LsWidgetsRes.TORCH_RES_INACTIVE,
        onClick = { it.toggleFlashlight() },
        registerCallback = { controller ->
            Dependency.get(FlashlightController::class.java).addCallback(controller.callbacks.flashlightCallback)
        },
        unregisterCallback = { controller ->
            Dependency.get(FlashlightController::class.java).removeCallback(controller.callbacks.flashlightCallback)
        }
    ),
    MEDIA(
        shape = WidgetShape.ROUND,
        R.drawable.ic_media_pause, R.drawable.ic_media_play,
        onClick = { it.toggleMediaPlaybackState() },
        registerCallback = { controller ->
            controller.mediaSessionManagerHelper.addMediaMetadataListener(controller)
        },
        unregisterCallback = { controller ->
            controller.mediaSessionManagerHelper.removeMediaMetadataListener(controller)
        }
    ),
    HOTSPOT(
        shape = WidgetShape.ROUND,
        LsWidgetsRes.HOTSPOT_ACTIVE, LsWidgetsRes.HOTSPOT_INACTIVE,
        onClick = { it.toggleHotspot() },
        onLongClick = { c, v -> c.showInternetDialog(v); true },
        registerCallback = { controller ->
            Dependency.get(HotspotController::class.java).addCallback(controller.callbacks.hotspotCallback)
        },
        unregisterCallback = { controller ->
            Dependency.get(HotspotController::class.java).removeCallback(controller.callbacks.hotspotCallback)
        }
    ),
    TIMER(
        shape = WidgetShape.ROUND,
        R.drawable.ic_alarm, R.drawable.ic_alarm,
        onClick = { it.activityLauncherUtils.launchTimer() }
    ),
    CALCULATOR(
        shape = WidgetShape.ROUND,
        R.drawable.ic_calculator, R.drawable.ic_calculator,
        onClick = { it.activityLauncherUtils.launchCalculator() }
    ),
    WALLET(
        shape = WidgetShape.ROUND,
        R.drawable.ic_wallet_lockscreen, R.drawable.ic_wallet_lockscreen,
        onClick = { it.activityLauncherUtils.launchWalletApp() }
    ),
    QRSCANNER(
        shape = WidgetShape.ROUND,
        R.drawable.ic_qr_code_scanner, R.drawable.ic_qr_code_scanner,
        onClick = { it.activityLauncherUtils.launchQrScanner() }
    ),
    WEATHER_TEMP(
        shape = WidgetShape.ROUND,
        activeRes = R.drawable.ic_weather_temp,
        inactiveRes = R.drawable.ic_weather_temp,
        onClick = { /* handled by view/controller */ },
        registerCallback = { it.initWeatherTempWidget() },
        unregisterCallback = { it.destroyWeatherTempWidget() }
    ),

    WEATHER_HUMIDITY(
        shape = WidgetShape.ROUND,
        activeRes = R.drawable.ic_humidity,
        inactiveRes = R.drawable.ic_humidity,
        onClick = { /* handled by view/controller */ },
        registerCallback = { it.initWeatherHumidityWidget() },
        unregisterCallback = { it.destroyWeatherHumidityWidget() }
    ),

    WEATHER_RANGE(
        shape = WidgetShape.ROUND,
        activeRes = R.drawable.ic_temp_range,
        inactiveRes = R.drawable.ic_temp_range,
        onClick = { /* handled by view/controller */ },
        registerCallback = { it.initWeatherRangeWidget() },
        unregisterCallback = { it.destroyWeatherRangeWidget() }
    ),

    // --- Pill Widgets ---
    BT_BATTERY(
        shape = WidgetShape.PILL,
        activeRes = R.drawable.ic_bt_battery,
        inactiveRes = R.drawable.ic_bt_battery,
        onClick = { /* handled by controller */ },
        registerCallback = { it.initBatteryCombinedWidget() },
        unregisterCallback = { it.destroyBatteryCombinedWidget() }
    ),

    WEATHER(
        shape = WidgetShape.PILL,
        activeRes = R.drawable.ic_weather_sunny,
        inactiveRes = R.drawable.ic_weather_sunny,
        onClick = { it.activityLauncherUtils.launchWeatherApp() },
        registerCallback = { it.initWeatherWidget() },
        unregisterCallback = { it.destroyWeatherWidget() }
    ),
    
    WELLBEING(
        shape = WidgetShape.ROUND,
        activeRes = R.drawable.ic_wellbeing,
        inactiveRes = R.drawable.ic_wellbeing,
        onClick = { it.activityLauncherUtils.launchDigitalWellbeingApp() },
        registerCallback = { it.initWellbeingWidget() },
        unregisterCallback = { it.destroyWellbeingWidget() }
    );

    companion object {
        private const val SETTING_KEY = "lockscreen_widgets_extras"

        fun getEnabledFromSettings(context: Context): List<WidgetAction> {
            val raw = Settings.System.getStringForUser(
                context.contentResolver, SETTING_KEY, android.os.UserHandle.USER_CURRENT
            )
            if (raw.isNullOrEmpty()) return listOf(WIFI, BT, RINGER, TORCH)
            return raw.split(",").mapNotNull { name -> values().find { it.name == name } }
        }

        fun saveEnabledToSettings(context: Context, actions: List<WidgetAction>) {
            val value = actions.joinToString(",") { it.name }
            Settings.System.putStringForUser(
                context.contentResolver, SETTING_KEY, value, android.os.UserHandle.USER_CURRENT
            )
        }
    }
}