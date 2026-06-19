package io.initialcode.logdropandroiddemoapp.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.logdrop.sdk.LogDrop

class AppMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (LogDrop.isLogDropPush(remoteMessage.data)) {
            LogDrop.onRemoteMessageReceived(
                context = this,
                data = remoteMessage.data,
                notificationTitle = remoteMessage.notification?.title,
                notificationBody = remoteMessage.notification?.body
            )
        }
    }

    override fun onNewToken(token: String) {
        LogDrop.onNewFcmPushToken(token)
    }
}
