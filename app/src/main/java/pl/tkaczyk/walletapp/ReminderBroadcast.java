package pl.tkaczyk.walletapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyWallet")
                .setSmallIcon(R.drawable.wallet)
                .setContentTitle("Pamiętaj o dodaniu wydatków")
                .setContentText("Pamiętaj o dodaniu swoich wydatków aby ciągle je śledzić")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(200,builder.build());
    }

}
