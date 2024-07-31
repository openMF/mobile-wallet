package org.mifospay.utils

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.core.app.NotificationCompat
import org.mifospay.R
import org.mifospay.common.Constants
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Created by ankur on 23/July/2018
 */
class NotificationUtils(private val mContext: Context) {
    @JvmOverloads
    fun showNotificationMessage(
        title: String, message: String?,
        timeStamp: String, intent: Intent, imageUrl: String? = null
    ) {
        // Check for empty push message
        if (TextUtils.isEmpty(message)) {
            return
        }

        // notification icon
        val icon = R.drawable.feature_accounts_ic_bank
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val resultPendingIntent = PendingIntent.getActivity(
            mContext,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val mBuilder = NotificationCompat.Builder(
            mContext
        )

//        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                + "://" + mContext.getPackageName() + "/raw/notification");
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        if (!TextUtils.isEmpty(imageUrl)) {
            if (imageUrl != null && imageUrl.length > 4 && Patterns.WEB_URL.matcher(
                    imageUrl
                ).matches()
            ) {
                val bitmap = getBitmapFromURL(imageUrl)
                if (bitmap != null) {
                    showBigNotification(
                        bitmap, mBuilder, icon, title, message, timeStamp,
                        resultPendingIntent, alarmSound
                    )
                } else {
                    showSmallNotification(
                        mBuilder, icon, title, message, timeStamp,
                        resultPendingIntent, alarmSound
                    )
                }
            }
        } else {
            showSmallNotification(
                mBuilder, icon, title, message, timeStamp, resultPendingIntent,
                alarmSound
            )
            playNotificationSound()
        }
    }

    private fun showSmallNotification(
        mBuilder: NotificationCompat.Builder, icon: Int, title: String,
        message: String?, timeStamp: String, resultPendingIntent: PendingIntent, alarmSound: Uri
    ) {
        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.addLine(message)
        val notification: Notification
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentIntent(resultPendingIntent)
            .setSound(alarmSound)
            .setStyle(inboxStyle)
            .setWhen(getTimeMilliSec(timeStamp))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, icon))
            .setContentText(message)
            .build()
        val notificationManager = mContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.notify(Constants.NOTIFICATION_ID, notification)
    }

    private fun showBigNotification(
        bitmap: Bitmap, mBuilder: NotificationCompat.Builder, icon: Int,
        title: String, message: String?, timeStamp: String, resultPendingIntent: PendingIntent,
        alarmSound: Uri
    ) {
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
        bigPictureStyle.setBigContentTitle(title)
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString())
        bigPictureStyle.bigPicture(bitmap)
        val notification: Notification
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentIntent(resultPendingIntent)
            .setSound(alarmSound)
            .setStyle(bigPictureStyle)
            .setWhen(getTimeMilliSec(timeStamp))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, icon))
            .setContentText(message)
            .build()
        val notificationManager = mContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.notify(Constants.NOTIFICATION_ID_BIG_IMAGE, notification)
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    fun getBitmapFromURL(strURL: String?): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            Log.e(TAG, "Unable to create bitmap", e)
            null
        }
    }

    // Playing notification sound
    fun playNotificationSound() {
        try {
//            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                    + "://" + mContext.getPackageName() + "/raw/notification");
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(mContext, defaultSoundUri)
            r.play()
        } catch (e: Exception) {
            Log.d(TAG, "playNotificationSound: " + e.message)
        }
    }

    companion object {
        private val TAG = NotificationUtils::class.java.simpleName

        /**
         * Method checks if the app is in background or not
         */
        fun isAppIsInBackground(context: Context): Boolean {
            var isInBackground = true
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                val runningProcesses = am.runningAppProcesses
                for (processInfo in runningProcesses) {
                    if (processInfo.importance
                        == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    ) {
                        for (activeProcess in processInfo.pkgList) {
                            if (activeProcess == context.packageName) {
                                isInBackground = false
                            }
                        }
                    }
                }
            } else {
                val taskInfo = am.getRunningTasks(1)
                val componentInfo = taskInfo[0].topActivity
                if (componentInfo!!.packageName == context.packageName) {
                    isInBackground = false
                }
            }
            return isInBackground
        }

        // Clears notification tray messages
        fun clearNotifications(context: Context) {
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.cancelAll()
        }

        fun getTimeMilliSec(timeStamp: String?): Long {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            try {
                val date = format.parse(timeStamp)
                return date.time
            } catch (e: ParseException) {
                Log.e(TAG, "Unable to parse the timestamp", e)
            }
            return 0
        }
    }
}
