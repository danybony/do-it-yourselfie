package net.bonysoft.doityourselfie

import android.arch.persistence.room.Room
import android.graphics.BitmapFactory
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.widget.ImageView
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import com.orhanobut.hawk.Hawk
import net.bonysoft.doityourselfie.camera.SelfieCamera
import net.bonysoft.doityourselfie.camera.dumpFormatInfo
import net.bonysoft.doityourselfie.communication.TokenManager
import net.bonysoft.doityourselfie.communication.TokenReceiver
import net.bonysoft.doityourselfie.queue.QueueDatabase
import timber.log.Timber

class MainActivity : AppCompatActivity(), TokenReceiver {

    companion object {
        const val TOKEN_KEY = "authentication_token"
    }

    private lateinit var ledGpio: Gpio
    private lateinit var buttonInputDriver: ButtonInputDriver

    private lateinit var camera: SelfieCamera
    private lateinit var cameraThread: HandlerThread
    private lateinit var cameraHandler: Handler
    private lateinit var picturesUploadQueue: PicturesUploadQueue
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.image)

        if (BuildConfig.DEBUG) dumpFormatInfo(this)

        val logicState = if (BuildConfig.NC_BUTTON) Button.LogicState.PRESSED_WHEN_HIGH else Button.LogicState.PRESSED_WHEN_LOW
        buttonInputDriver = ButtonInputDriver(
            BoardDefaults.gpioForButton,
            logicState,
            KeyEvent.KEYCODE_SPACE)
        buttonInputDriver.register()

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

        ledGpio = PeripheralManager.getInstance().openGpio(BoardDefaults.gpioForLED)
            .apply {
                setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            }

        if (Hawk.contains(TOKEN_KEY)) {
            Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show()
            onTokenReceived(Hawk.get(TOKEN_KEY))
        }
        TokenManager.attachTo(this)
    }

    override fun onTokenReceived(token: String) {
        Timber.d("New token received")
        Hawk.put(TOKEN_KEY, token)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            ledGpio.value = true
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            ledGpio.value = false
            Timber.d("Button pressed")
            startShootingProcess()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun startShootingProcess() {
        camera.takePicture()
    }

    override fun onStop() {
        buttonInputDriver.unregister()
        buttonInputDriver.close()
        ledGpio.close()
        super.onStop()
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

        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        runOnUiThread {
            imageView.setImageBitmap(bitmap)
        }

        scheduleUploadWorker()
    }

    private fun scheduleUploadWorker() {
        val uploadConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workerTag = "upload"
        val workRequest = OneTimeWorkRequest.Builder(PicturesUploadWorker::class.java)
            .setConstraints(uploadConstraints)
            .addTag(workerTag)
            .build()

        with(WorkManager.getInstance()) {
            cancelAllWorkByTag("upload")
            enqueue(workRequest)
        }
    }

}
