package com.ejs.birthchart.utils;

import static com.ejs.birthchart.utils.msg.log;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.view.ContextThemeWrapper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.ejs.birthchart.R;

public class notifications {
    public static final String NOTIFICATION_CHANNEL_ID = "new_notifications";
    public static final String NOTIFICATION_CHANNEL_ID_MOON = "new_notifications_moon";
    public static final String NOTIFICATION_CHANNEL_ID_ECLIPSE = "new_notifications_eclipse";
    public static final String NOTIFICATION_CHANNEL_ID_UPDATE = "new_notifications_update";
    public static final int NOTIFICATION_ID = 0;
    public static final int NOTIFICATION_ID_MOON = 1;
    public static final int NOTIFICATION_ID_ECLIPSE = 2;
    public static final int NOTIFICATION_ID_UPDATE = 3;

    /**
     * Notification builder object
     *
     * @param context app context
     * @param title title
     * @param msg message
     * @param icon icon
     * @param largeIcon bitmap
     * @param pi pending intent
     * @return return object notification builder
     */
    public static NotificationCompat.Builder notificationBuilder(Context context, String channel_id , String title, String msg, int icon, Bitmap largeIcon, PendingIntent pi) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channel_id);

        notificationBuilder
                //.setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                //.setWhen(System.currentTimeMillis())
                //.setShowWhen(false)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setLargeIcon(largeIcon)
                .setSmallIcon(icon)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setContentIntent(pi)
                /*.setAutoCancel(true)
                .setOnlyAlertOnce(true)*/
                /*.addAction(action1)
                .addAction(action2)
                .addAction(action3)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
                        // Show our playback controls in the compact notification view.
                        .setShowActionsInCompactView(0, 1, 2))*/
                // Set the Notification color
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true);
        return notificationBuilder;
    }

    /**
     * Notification Channel for normal notifications
     *
     * @param context app context
     * @param NOTIFICATION_CHANNEL_ID Notification Id
     * @return return NotificationChannel object
     */
    private static NotificationChannel notificationChannelNormal(Context context, String NOTIFICATION_CHANNEL_ID) {

        String NOTIFICATION_CHANNEL_NAME = "App Notifications";
        String NOTIFICATION_CHANNEL_DESCRIPTION = context.getString(R.string.channel_notification_desc);

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        // Configure the notification channel.
        notificationChannel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        notificationChannel.enableVibration(true);
        return notificationChannel;
    }

    /**
     * Notification Channel for updates notifications
     *
     * @return return NotificationChannel object
     */
    private static NotificationChannel notificationChannelUpdate() {

        String NOTIFICATION_CHANNEL_NAME = "New Update";
        String NOTIFICATION_CHANNEL_DESCRIPTION = "New Update";

        NotificationChannel notificationChannel = new NotificationChannel(notifications.NOTIFICATION_CHANNEL_ID_UPDATE, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        // Configure the notification channel.
        notificationChannel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        notificationChannel.enableVibration(true);
        return notificationChannel;
    }

    /**
     * Notifications.
     *
     * @param context App context
     */
    public static void notification(Context context, String title, String msg) {
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.createNotificationChannel(notificationChannelNormal(context, NOTIFICATION_CHANNEL_ID));
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder(context, NOTIFICATION_CHANNEL_ID, title, msg, R.drawable.ic_launcher_foreground, largeIcon, null).build());
    }

    /**
     * Moon Notifications.
     *
     * @param context App context
     */
    public static void notificationMoon(Context context, String title, String msg, Bitmap drawable) {
        Bitmap largeIcon = drawable;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.createNotificationChannel(notificationChannelNormal(context, NOTIFICATION_CHANNEL_ID_MOON));
        notificationManager.notify(NOTIFICATION_ID_MOON, notificationBuilder(context, NOTIFICATION_CHANNEL_ID_MOON, title, msg, R.drawable.ic_launcher_foreground, largeIcon, null).build());
    }

    /**
     * Eclipse Notifications.
     *
     * @param context App context
     */
    public static void notificationEclipse(Context context, String title, String msg, int drawable) {
        log("e", "eclipse", "notificationEclipse ");
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), drawable);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.createNotificationChannel(notificationChannelNormal(context, NOTIFICATION_CHANNEL_ID_ECLIPSE));
        notificationManager.notify(NOTIFICATION_ID_ECLIPSE, notificationBuilder(context, NOTIFICATION_CHANNEL_ID_ECLIPSE, title, msg, R.drawable.ic_launcher_foreground, largeIcon, null).build());
    }

    /**
     * Update Notifications.
     *
     * @param context App context
     */
    public static void notificationUpdate(Context context, String title, String msg) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()));

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        PendingIntent pendingIntent = PendingIntent.getActivity( context, 0, browserIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.createNotificationChannel(notificationChannelUpdate());
        notificationManager.notify(NOTIFICATION_ID_UPDATE, notificationBuilder(context, NOTIFICATION_CHANNEL_ID_UPDATE, title, msg, R.drawable.ic_launcher_foreground, largeIcon, pendingIntent).build());
    }

    /**
     * Alert dialog for updates
     *
     * @param mCompat app context
     * @param title title
     * @param msg message
     * @param Mandatory is mandatory
     * @return return Alert builder object
     */
    public static AlertDialog.Builder alertUpdate(AppCompatActivity mCompat, String title, String msg, boolean Mandatory) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mCompat, R.style.Theme_BirthChartCalculator));
        builder.setTitle(title);
        builder.setMessage(msg);
        if (Mandatory) {
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.str_btn_update, (dialog, which) -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + mCompat.getPackageName()));
                mCompat.startActivity(browserIntent);
                mCompat.finish();
            });
        } else {
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.str_btn_update, (dialog, which) -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + mCompat.getPackageName()));
                mCompat.startActivity(browserIntent);
                dialog.dismiss();
            });
            builder.setNegativeButton(R.string.str_btn_later, (dialog, which) -> dialog.dismiss());
        }

        return builder;
    }

    /**
     *  Remove Notification
     *
     * @param context app context
     * @param NOTIFICATION_ID Notification Id
     */
    private void removeNotification(Context context, int NOTIFICATION_ID) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

}
