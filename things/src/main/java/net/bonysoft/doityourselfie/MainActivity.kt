package net.bonysoft.doityourselfie

import android.app.Activity
import android.arch.persistence.room.Room
import android.graphics.BitmapFactory
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.widget.ImageView
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import net.bonysoft.doityourselfie.camera.SelfieCamera
import net.bonysoft.doityourselfie.camera.dumpFormatInfo
import net.bonysoft.doityourselfie.queue.QueueDatabase
import java.util.concurrent.TimeUnit

class MainActivity : Activity() {

    private lateinit var camera: SelfieCamera
    private lateinit var cameraThread: HandlerThread
    private lateinit var cameraHandler: Handler
    private lateinit var picturesUploadQueue: PicturesUploadQueue
    private val imageViews = ArrayList<ImageView>()
    private var nextImageId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageViews.add(findViewById(R.id.imageTopLeft))
        imageViews.add(findViewById(R.id.imageTopRight))
        imageViews.add(findViewById(R.id.imageBottomLeft))
        imageViews.add(findViewById(R.id.imageBottomRight))

        if (BuildConfig.DEBUG) dumpFormatInfo(this)

        picturesUploadQueue = PicturesUploadQueue(Room.databaseBuilder(applicationContext, QueueDatabase::class.java, "pictures_queue").build())

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
        scheduleUploadWorker()
        startShootingProcess() // TODO do it on button clicked
    }

    private fun startShootingProcess() {
        camera.takePicture()
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.shutDown()
        cameraThread.quitSafely()
    }

    private fun onPictureTaken(imageBytes: ByteArray?) {
        if (imageBytes == null) {
            return
        }
        Thread {
            val storedPath = saveImageToFile(imageBytes)
            if (storedPath != null) {
                picturesUploadQueue.put(storedPath)
            }
        }.start()

        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) // TODO resize depending on the view
        runOnUiThread {
            imageViews[nextImageId].setImageBitmap(bitmap)
            nextImageId++
            if (nextImageId >= 4) {
                nextImageId = 0
            } else {
                Thread.sleep(3000)
                camera.takePicture()
            }
        }
    }

    private fun scheduleUploadWorker() {
        val uploadConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequest.Builder(PicturesUploadWorker::class.java, 1, TimeUnit.MINUTES)
            .setConstraints(uploadConstraints)
            .build()

        WorkManager.getInstance().enqueue(workRequest)
    }

}
