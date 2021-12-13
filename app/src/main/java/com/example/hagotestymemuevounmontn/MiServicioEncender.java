package com.example.hagotestymemuevounmontn;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import java.util.Random;


public class MiServicioEncender extends JobIntentService {
    private static final int ID_TRABAJO = 777;
    String ETIQUETA = "SERVICIOINTENSO";
    String latitud;
    String altitud;
    int contador = 1;
    PantallaEncendida pantallaEncendida= new PantallaEncendida();
    //Variables para obtener la localización
    LocationManager locationManager;
    LocationListener locationListener;
    public MiServicioEncender() {

    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(ETIQUETA, "Comenzamos a trabajar");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        getBaseContext().registerReceiver(pantallaEncendida, intentFilter);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitud= String.valueOf(location.getLatitude());
                altitud=String.valueOf(location.getAltitude());
            }
        };
    }

    static void encolarTrabajo(Context context, Intent trabajo){
        enqueueWork(context, MiServicioEncender.class, ID_TRABAJO,  trabajo);
    }

    class PantallaEncendida extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = registerReceiver(null, ifilter);

                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                float bateria = (level / (float)scale)*100;
                Toast.makeText(getApplicationContext(), "Hola", Toast.LENGTH_SHORT).show();
                Log.i(ETIQUETA, "Pantalla Encendida/ Bateria="+bateria+" /Latitud= "+latitud+" Altitud= "+altitud);

            }
        }
    }
}