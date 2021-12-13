package com.example.hagotestymemuevounmontn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

public class Alarma extends BroadcastReceiver {

    MediaPlayer mediaPlayer;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "La alarma se ha disparado", Toast.LENGTH_SHORT).show();
        Log.i("ALARMA", "La alarma se ha disparado");
        mediaPlayer=MediaPlayer.create(context.getApplicationContext(), R.raw.alarma);
        mediaPlayer.start();
    }
}