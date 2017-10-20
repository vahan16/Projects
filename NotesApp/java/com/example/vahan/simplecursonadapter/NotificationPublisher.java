package com.example.vahan.simplecursonadapter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

public class NotificationPublisher extends BroadcastReceiver {

    String state,state1;

    @Override
        public void onReceive(Context arg0, Intent arg1) {
                state  = arg1.getStringExtra("state");
                state1 = arg1.getStringExtra("state1");
            Intent it =  new Intent(arg0, NoteActivity.class);
            createNotification(arg0, it, "title", state, state1);
    }
    public void createNotification(Context context, Intent intent, CharSequence ticker, CharSequence title, CharSequence descricao){
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent p = PendingIntent.getActivity(context, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(p)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(descricao)
                .setSmallIcon(R.drawable.notesa)
                .setPriority(Notification.PRIORITY_HIGH);
        Notification n = builder.build();
        n.vibrate = new long[]{150, 300, 150, 400};
        n.flags = Notification.FLAG_AUTO_CANCEL;
        n.flags |= Notification.FLAG_SHOW_LIGHTS;
        n.ledARGB=Color.CYAN;
        n.ledOnMS = 1000;
        n.ledOffMS = 2000;
        Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone toque = RingtoneManager.getRingtone(context, som);
            toque.play();
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        nm.notify(m, n);
    }

}