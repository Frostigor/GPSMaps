package com.mayuresh.sony.gpsmaps;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by SONY on 29-03-2016.
 */
public class service extends Service implements SensorEventListener,LocationListener{

    private SensorManager sensorManager;
    double lat;
    double lon;
    double ax, ay, az;
    private double mAccel;
    private double mAccelCurrent;
    private double mAccelLast;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("max","onStartCommand");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.d("max", "onSensorChanged");
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = Math.sqrt(ax * ax + ay * ay + az * az);
            double delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;


            Log.d("max"," "+ax+" "+ay+" "+az);
            int temp = compare((int) ax, (int) ay, (int) az);

            if (temp == 0) {
                //orientation x
                Log.d("max","X orientation");
                Log.d("max",""+(mAccelLast-mAccelCurrent));
                if ((mAccelLast - mAccelCurrent) > 7) {
                    Toast.makeText(this, "pothole x", Toast.LENGTH_SHORT).show();
                    Log.d("max", "pothole x");

                    sendLocationData(lat, lon);
                }
            } else if (temp == 1) {
                //orientation y
                Log.d("max","y orientation");
                Log.d("max",""+(mAccelLast-mAccelCurrent));
                if ((mAccelLast - mAccelCurrent) > 7) {
//                    Toast.makeText(this, "pothole y", Toast.LENGTH_SHORT).show();
                    Log.d("max", "pothole y");

                    sendLocationData(lat, lon);

                }
            } else if (temp == 2) {
                //orientation z
                Log.d("max","z orientation");
                Log.d("max","cur:"+mAccelCurrent+"      last:"+mAccelLast);
                if ((mAccelLast - mAccelCurrent) > 7) {
//                    Toast.makeText(this, "pothole z", Toast.LENGTH_SHORT).show();
                    Log.d("max",""+(mAccelLast-mAccelCurrent));
                    Log.d("max", "pothole z");

                    sendLocationData(lat, lon);

                }
            }

        }


    }

    private int compare(int ax, int ay, int az) {
        ax = Math.abs(ax);
        ay = Math.abs(ay);
        az = Math.abs(az);
        if (ax > ay) {
            if (ax > az) return 0;
        } else if (ay > az) return 1;
        else return 2;

        return -1;
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d("max", "in Service onLocationChanged");

        lat = location.getLatitude();
        lon = location.getLongitude();
//        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("max", "end in Service onLocationChanged");

    }
    public void sendLocationData(double lat, double lon) {

//        try {
//            FileOutputStream fos = openFileOutput("mayuresh", MODE_APPEND);
//            String lati = "" + lat;
//            String longi = "" + lon;
//            String data = "$" + lati + "&" + longi;
//            fos.write(data.getBytes());
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String longi = "" +lat;
//        String lati = "" +lon;
//
//        String link = "http://convene.co.in/appprocess_update.php";


        class mayuresh implements Runnable {
            double lat, lon;

            mayuresh(double lat, double lon) {
                this.lat = lat;
                this.lon = lon;
            }

            @Override
            public void run() {
                Log.d("max", "Thread Run");


            String strUrl = "http://projectnoob.site88.net/save.php";
            URL url = null;
            try {
                Log.d("max", "try To COnnect OnSend");
                url = new URL(strUrl);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                        connection.getOutputStream());

                outputStreamWriter.write("lat=" + lat + "&lng="+lon);
                outputStreamWriter.flush();
                outputStreamWriter.close();

                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";

                while( (line = reader.readLine()) != null){
                    sb.append(line);
                }

                reader.close();
                iStream.close();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            }
        }

        Thread thread = new Thread(new mayuresh(lat, lon));
        thread.start();

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
