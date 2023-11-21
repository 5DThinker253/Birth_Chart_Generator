package com.ejs.birthchart.services;

import static com.ejs.birthchart.utils.notifications.notification;
import static com.ejs.birthchart.utils.notifications.notificationUpdate;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class FirebaseMessageReceiver extends FirebaseMessagingService {
    private final String tag = this.getClass().getSimpleName();
    private final String TAG = this.getClass().getSimpleName();

    // Override onNewToken to get new token
    @Override
    public void onNewToken(@NonNull String token)
    {
        Log.e(TAG, "Refreshed token: " + token);
    }
    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("TAG", "From: " + remoteMessage.getFrom());
        // First case when notifications are received via
        // data event
        // Here, 'title' and 'message' are the assumed names
        // of JSON
        // attributes. Since here we do not have any data
        // payload, This section is commented out. It is
        // here only for reference purposes.
		/*if(remoteMessage.getData().size()>0){
			showNotification(remoteMessage.getData().get("title"),
						remoteMessage.getData().get("message"));
		}*/

        // Second case when notification payload is
        // received.
        if (remoteMessage.getNotification() != null) {
            // Since the notification is received directly
            // from FCM, the title and the body can be
            // fetched directly as below.
            if (Objects.equals(remoteMessage.getNotification().getChannelId(), "update")){
                notificationUpdate(getApplicationContext(), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
                //updateNotification(getApplicationContext());
            } else if (Objects.equals(remoteMessage.getNotification().getChannelId(), "info")){
                notification(getApplicationContext(), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            }
        }
    }

}
