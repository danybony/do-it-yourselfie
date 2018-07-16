package net.bonysoft.doityourselfie.camera

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.CAMERA_SERVICE
import android.graphics.ImageFormat
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.TotalCaptureResult
import android.media.ImageReader
import android.os.Handler
import android.util.Size
import android.view.Surface
import timber.log.Timber

private const val MAX_IMAGES = 1

class SelfieCamera
private constructor() {

    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            Timber.d("Opened camera.")
            this@SelfieCamera.cameraDevice = cameraDevice
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            Timber.d("Camera disconnected, closing.")
            cameraDevice.close()
        }

        override fun onError(cameraDevice: CameraDevice, i: Int) {
            Timber.d("Camera device error, closing.")
            cameraDevice.close()
        }

        override fun onClosed(cameraDevice: CameraDevice) {
            Timber.d("Closed camera, releasing")
            this@SelfieCamera.cameraDevice = null
        }
    }


    private val mSessionCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
            // The camera is already closed
            if (cameraDevice == null) {
                return
            }

            // When the session is ready, we start capture.
            captureSession = cameraCaptureSession
            triggerImageCapture()
        }

        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
            Timber.e("Failed to configure camera")
        }
    }

    private val mCaptureCallback = object : CameraCaptureSession.CaptureCallback() {

        override fun onCaptureProgressed(session: CameraCaptureSession,
                                         request: CaptureRequest,
                                         partialResult: CaptureResult) {
            Timber.d("Partial result")
        }

        override fun onCaptureCompleted(session: CameraCaptureSession,
                                        request: CaptureRequest,
                                        result: TotalCaptureResult) {
            if (session != null) {
                session.close()
                captureSession = null
                Timber.d("CaptureSession closed")
            }
        }
    }

    private object InstanceHolder {
        internal val camera = SelfieCamera()
    }

    @SuppressLint("MissingPermission")
    fun initializeCamera(context: Context,
                         backgroundHandler: Handler,
                         imageAvailableListener: ImageReader.OnImageAvailableListener) {
        // Discover the camera instance
        val manager = context.getSystemService(CAMERA_SERVICE) as CameraManager
        var camIds = arrayOf<String>()
        try {
            camIds = manager.cameraIdList
        } catch (e: CameraAccessException) {
            Timber.e(e, "Cam access exception getting IDs")
        }

        if (camIds.isEmpty()) {
            Timber.e("No cameras found")
            return
        }
        val id = camIds[0]
        Timber.d("Using camera id $id")

        val outputSize = getHighestSize(manager, id)

        // Initialize the image processor
        imageReader = ImageReader.newInstance(outputSize.width, outputSize.height, ImageFormat.JPEG, MAX_IMAGES)
        imageReader!!.setOnImageAvailableListener(imageAvailableListener, backgroundHandler)

        // Open the camera resource
        try {
            manager.openCamera(id, stateCallback, backgroundHandler)
        } catch (cae: CameraAccessException) {
            Timber.d(cae, "Camera access exception")
        }

    }

    private fun getHighestSize(manager: CameraManager, id: String): Size {
        val characteristics = manager.getCameraCharacteristics(id)
        val configs = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val highestSize = configs.getOutputSizes(ImageFormat.JPEG).maxBy { it.width * it.height }!!
        Timber.d("Using output resolution: $highestSize")
        return highestSize
    }

    fun takePicture() {
        if (cameraDevice == null) {
            Timber.e("Cannot capture image. Camera not initialized.")
            return
        }

        // Here, we create a CameraCaptureSession for capturing still images.
        try {
            cameraDevice!!.createCaptureSession(
                listOf<Surface>(imageReader!!.surface),
                mSessionCallback, null)
        } catch (cae: CameraAccessException) {
            Timber.e(cae, "access exception while preparing pic")
        }

    }

    private fun triggerImageCapture() {
        try {
            val captureBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(imageReader!!.surface)
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO)
            captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO)
            Timber.d("Session initialized.")
            captureSession!!.capture(captureBuilder.build(), mCaptureCallback, null)
        } catch (cae: CameraAccessException) {
            Timber.e(cae, "camera capture exception")
        }

    }

    /**
     * Close the camera resources
     */
    fun shutDown() {
        cameraDevice?.close()
    }

    companion object {

        val instance: SelfieCamera
            get() = InstanceHolder.camera
    }
}
