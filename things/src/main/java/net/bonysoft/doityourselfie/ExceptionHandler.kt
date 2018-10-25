package net.bonysoft.doityourselfie

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class ExceptionHandler(private val activity: Activity) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtra("crash", true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
            or Intent.FLAG_ACTIVITY_CLEAR_TASK
            or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(activity.applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val mgr = activity.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
        activity.finish()
        System.exit(2)
    }

}
