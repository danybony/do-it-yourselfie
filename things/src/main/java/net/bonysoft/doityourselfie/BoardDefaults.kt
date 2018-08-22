package net.bonysoft.doityourselfie

import android.os.Build

object BoardDefaults {
    private const val DEVICE_RPI3 = "rpi3"
    private const val DEVICE_IMX6UL_PICO = "imx6ul_pico"
    private const val DEVICE_IMX7D_PICO = "imx7d_pico"

    val gpioForButton = when (Build.DEVICE) {
        DEVICE_RPI3 -> "BCM21" // Rainbow Hat "A" button
        DEVICE_IMX6UL_PICO -> "GPIO2_IO03"
        DEVICE_IMX7D_PICO -> "GPIO6_IO14"
        else -> throw IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE)
    }
}
