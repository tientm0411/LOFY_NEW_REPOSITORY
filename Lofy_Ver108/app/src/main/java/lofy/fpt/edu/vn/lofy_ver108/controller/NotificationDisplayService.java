package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.adapter.InforNotiMaker;
import lofy.fpt.edu.vn.lofy_ver108.business.MapMethod;
import lofy.fpt.edu.vn.lofy_ver108.dbo.QueryFirebase;
import lofy.fpt.edu.vn.lofy_ver108.entity.Notification;

public class NotificationDisplayService extends Service {

    final int NOTIFICATION_ID = 16;
    private Bitmap bitmap;
    private QueryFirebase queryFirebase = new QueryFirebase();

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

    private void displayNotification(final Notification noti) {
        final int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        Intent notificationIntent = new Intent(this, StartActivity.class);
        notificationIntent.putExtra("KEY_INTENT", "GROUP");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        bitmap = null;
        new LoadImageNoti() {
            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                bitmap = result;

                NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle(noti.getNotiName())
                        .setContentText(noti.getMess())
                        .setSmallIcon(R.drawable.ic_app_logo)
                        .setLargeIcon(bitmap)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        .setVibrate(new long[]{0, 300, 300, 300})
//                .setSound()
                        .setContentIntent(notificationPendingIntent)
                        .setLights(Color.WHITE, 1000, 5000)
//                .setWhen(System.currentTimeMillis()))
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(noti.getMess()))
//                .setOngoing(true)
//                .setOnlyAlertOnce(false)
                        ;

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(m, notification.build());
            }
        }.execute(noti.getNoti_icon());

        Log.d("TAG", "displayNotification: '" + bitmap);

    }


    public class LoadImageNoti extends AsyncTask<String, Void, Bitmap> {
        public LoadImageNoti() {
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL urlConnection = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                if (myBitmap == null)
                    return null;
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
        }

    }

}
