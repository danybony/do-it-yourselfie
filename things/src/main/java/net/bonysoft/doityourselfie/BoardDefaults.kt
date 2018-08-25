package net.bonysoft.doityourselfie

import android.os.Build

object BoardDefaults {

    private const val DEVICE_RPI3 = "rpi3"
    private const val DEVICE_IMX6UL_PICO = "imx6ul_pico"
    private const val DEVICE_IMX7D_PICO = "imx7d_pico"

    val gpioForLED = when (Build.DEVICE) {
        DEVICE_RPI3 -> "BCM6"
        DEVICE_IMX6UL_PICO -> "GPIO4_IO22"
        DEVICE_IMX7D_PICO -> "GPIO2_IO02"
        else -> throw IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE)
    }

    val gpioForButton = when (Build.DEVICE) {
        DEVICE_RPI3 -> if (USE_RAINBOW_HAT) "BCM21" else "BCM18"
        DEVICE_IMX6UL_PICO -> "GPIO2_IO03"
        DEVICE_IMX7D_PICO -> "GPIO6_IO14"
        else -> throw IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE)
    }
}

private const val USE_RAINBOW_HAT = false
internal const val NORMALLY_CLOSED_BUTTON = true
