package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.Date;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.entity.Notification;

public class NotificationDisplayService extends Service {

    final int NOTIFICATION_ID = 16;

    public NotificationDisplayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification data = (Notification) intent.getExtras().get("KEY_NOTI");
        displayNotification(data);
        stopSelf(); // Beendet den Service nach dem Ausführen des Codes (nachträglich ergänzt)
        return super.onStartCommand(intent, flags, startId);
    }

    private void displayNotification(Notification noti) {


        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        Intent notificationIntent = new Intent(this, StartActivity.class);
        notificationIntent.putExtra("KEY_INTENT","GROUP");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setContentTitle(noti.getNotiName())
                .setContentText(noti.getMess())
                .setSmallIcon(R.drawable.ic_app_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_police))

                .setColor(getResources().getColor(R.color.colorPrimary))
                .setVibrate(new long[]{0, 300, 300, 300})
                //.setSound()
                .setContentIntent(notificationPendingIntent)
                .setLights(Color.WHITE, 1000, 5000)
//                .setWhen(System.currentTimeMillis()))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(noti.getMess()))
//                .setOngoing(true)
                //.setOnlyAlertOnce(false)
                ;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(m, notification.build());
    }
}
