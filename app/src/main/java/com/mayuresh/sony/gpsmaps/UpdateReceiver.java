package com.mayuresh.sony.gpsmaps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by SONY on 28-03-2016.
 */
public class UpdateReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("max", "onReceive Broadcast Receiver");
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        Intent in = new Intent(context, service.class);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            // Do something
            context.startService(in);

            Log.d("max", "onReceive Broadcast Receiver Connected");

        } else {
            context.stopService(in);

            Log.d("max", "onReceive Broadcast Receiver Not Connected");
        }
    }
}
