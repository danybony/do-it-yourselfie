package net.bonysoft.doityourselfie.camera

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import timber.log.Timber

/**
 * Helpful debugging method:  Dump all supported camera formats to log.  You don't need to run
 * this for normal operation, but it's very helpful when porting this code to different
 * hardware.
 */
fun dumpFormatInfo(context: Context) {
    val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    var camIds = arrayOf<String>()
    try {
        camIds = manager.cameraIdList
    } catch (e: CameraAccessException) {
        Timber.d("Cam access exception getting IDs")
    }

    if (camIds.isEmpty()) {
        Timber.d("No cameras found")
    }
    for (camId in camIds) {
        Timber.d("Using camera id $camId")
        try {
            val characteristics = manager.getCameraCharacteristics(camId)
            val configs = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            for (format in configs!!.outputFormats) {
                Timber.d("Getting sizes for format: $format")
                for (s in configs.getOutputSizes(format)) {
                    Timber.d("	$s")
                }
            }
            dumpAvailableCharacteristic(characteristics, CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS, "Effect available: ")
            dumpAvailableCharacteristic(characteristics, CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES, "AF Modes available: ")
            dumpAvailableCharacteristic(characteristics, CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES, "AE Modes available: ")
            dumpAvailableCharacteristic(characteristics, CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES, "Scene Modes available: ")
            dumpAvailableCharacteristic(characteristics, CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES, "AWB Modes available: ")
        } catch (e: CameraAccessException) {
            Timber.d("Cam access exception getting characteristics.")
        }

    }
}

private fun dumpAvailableCharacteristic(characteristics: CameraCharacteristics, controlAfAvailableModes: CameraCharacteristics.Key<IntArray>, s2: String) {
    val selectedCharacteristics = characteristics.get(controlAfAvailableModes) ?: return
    for (characteristic in selectedCharacteristics) {
        Timber.d("$s2$characteristic")
    }
}
