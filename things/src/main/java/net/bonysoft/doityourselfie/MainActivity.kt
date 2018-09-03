package net.bonysoft.doityourselfie

import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.graphics.BitmapFactory
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.State
import androidx.work.WorkManager
import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import com.orhanobut.hawk.Hawk
import io.github.krtkush.lineartimer.LinearTimer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch
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

    private var countdownRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            onTokenReceived(Hawk.get(TOKEN_KEY))
        }
        TokenManager.attachTo(this)
    }

    override fun onTokenReceived(token: String) {
        Timber.d("New token received")
        Hawk.put(TOKEN_KEY, token)
        launch {
            scheduleUploadWorker()
        }
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
        if (countdownRunning) {
            return
        }
        countdownRunning = true

        val timerDuration: Long = 3000
        val linearTimer = LinearTimer.Builder()
            .linearTimerView(progressCountdown)
            .duration(timerDuration)
            .timerListener(object : LinearTimer.TimerListener {
                override fun onTimerReset() {
                    // no-op
                }

                override fun animationComplete() {
                    progressCountdown.visibility = View.GONE
                    countdown.visibility = View.GONE
                    camera.takePicture()
                    countdownRunning = false
                }

                override fun timerTick(tickUpdateInMillis: Long) {
                    countdown.text = (((timerDuration - tickUpdateInMillis) / 1000) + 1).toString()
                }
            })
            .build()

        progressCountdown.visibility = View.VISIBLE
        countdown.visibility = View.VISIBLE
        linearTimer.startTimer()
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
        launch {
            val storedPath = saveImageToFile(imageBytes)
            if (storedPath != null) {
                picturesUploadQueue.put(storedPath)
            }
            displayQueueSize()
            scheduleUploadWorker()
        }

        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        runOnUiThread {
            image.setImageBitmap(bitmap)
        }
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
            cancelAllWorkByTag(workerTag)
            enqueue(workRequest)
            getStatusById(workRequest.id).observe(this@MainActivity, Observer { status ->
                if (status != null) {
                    when {
                        status.state == State.RUNNING -> display("Sync")
                        status.state == State.CANCELLED -> Timber.d("Worker canceled")
                        status.state == State.FAILED -> display(status.outputData.getString(KEY_FAILURE_REASON) ?: "Err?")
                        status.state.isFinished -> {
                            Timber.d("Worker completed")
                            displayQueueSize()
                        }
                    }
                }
            })
        }
    }

    private fun displayQueueSize() {
        launch {
            display(picturesUploadQueue.picturesToUpload().size.toString())
        }
    }

    private fun display(text: String) {
        with(RainbowHat.openDisplay()) {
            setBrightness(10)
            display(text)
            setEnabled(true)
            close()
        }
    }

}
