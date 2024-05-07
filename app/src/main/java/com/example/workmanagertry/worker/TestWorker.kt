package com.example.workmanagertry.worker

import com.example.workmanagertry.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class Worker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    val notificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    // カテゴリー名（通知設定画面に表示される情報）
    val name = "通知のタイトル的情報を設定"
    // システムに登録するChannelのID
    val id = "casareal_chanel"
    // 通知の詳細情報（通知設定画面に表示される情報）
    val notifyDescription = "この通知の詳細情報を設定します"


    init {
        // Channelの取得と生成
        notificationManager.getNotificationChannel(id) == null
        val mChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
        mChannel.apply {
            description = notifyDescription
        }
        notificationManager.createNotificationChannel(mChannel)
    }

    override fun doWork(): Result {

        repeat(10) {

            val notification = NotificationCompat.Builder(applicationContext,id).apply {

                setContentText("${it}回目のメッセージ")
                setSmallIcon(R.drawable.ic_launcher_background)
            }

            notificationManager.notify(1, notification.build())

            Thread.sleep(1000)
        }
        return Result.success()
    }
}
