package net.bonysoft.doityourselfie

import android.app.Activity
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import net.bonysoft.doityourselfie.camera.SelfieCamera
import net.bonysoft.doityourselfie.camera.dumpFormatInfo

class MainActivity : Activity() {

    private lateinit var camera: SelfieCamera
    private lateinit var cameraThread: HandlerThread
    private lateinit var cameraHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (BuildConfig.DEBUG) dumpFormatInfo(this)

        cameraThread = HandlerThread("CameraBackgroundThread")
        cameraThread.start()
        cameraHandler = Handler(cameraThread.looper)

        camera = SelfieCamera.instance
        camera.initializeCamera(this, cameraHandler, ImageReader.OnImageAvailableListener {
            val image = it.acquireLatestImage()
            val imageBuf = image.planes[0].buffer
            val imageBytes = ByteArray(imageBuf.remaining())
            imageBuf.get(imageBytes)
            image.close()

            onPictureTaken(imageBytes)
        })
    }

    override fun onStart() {
        super.onStart()
        camera.takePicture()
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.shutDown()
        cameraThread.quitSafely()
    }

    private fun onPictureTaken(imageBytes: ByteArray?) {
        if (imageBytes != null) {
            saveImageToFile(imageBytes)
        }
    }

}
