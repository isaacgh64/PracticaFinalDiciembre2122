package com.example.hagotestymemuevounmontn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Ajustes extends AppCompatActivity implements SensorEventListener{
    private static final String ID_CANAL = "El nombre de mi canal";
    private static final int CODIGO_ALARMA = 777;
    SensorManager sensorManager;

    ListView listView;
    Button buttonBorrarD, buttonActivar, buttonCompartir;
    EditText editTextminutos;

    ManejadorLogros manejadorLogros = new ManejadorLogros(this);
    ManejadorBDatos manejadorBDatos= new ManejadorBDatos(this);

    ArrayList<Integer> ID= new ArrayList();
    ArrayList<String> FechaHora= new ArrayList();
    ArrayList<String> Resultado= new ArrayList();
    Switch switchSonido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        listView=findViewById(R.id.listView1);
        switchSonido=findViewById(R.id.switchSonido);
        buttonBorrarD=findViewById(R.id.buttonBorrarD);
        buttonActivar=findViewById(R.id.buttonActivar);
        buttonCompartir=findViewById(R.id.buttonCompartir);
        editTextminutos=findViewById(R.id.editTextMinutos);


        if(Sonido.SONIDO==true){
            switchSonido.setChecked(true);
        }
        else{
            switchSonido.setChecked(false);
        }
        switchSonido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchSonido.isChecked()){
                    Sonido.SONIDO=true;
                }
                else{
                    Sonido.SONIDO=false;
                }
            }
        });
        MostrarDatos();
        //Botón que al ser pulsado Borra nuestros datos de las Tablas
        buttonBorrarD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manejadorBDatos.borrar();
                manejadorLogros.borrar();
                ID.remove(ID);
                FechaHora.remove(FechaHora);
                Resultado.remove(Resultado);
                RellenarDatos();
                lanzarNotificacion();
            }
        });

        //Botón que al ser pulsado nos activa nuestra alarma
        buttonActivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("BOTÓN","AQUÍ ESTOY");
                try{
                    int minutos= Integer.parseInt(editTextminutos.getText().toString().trim()+000);
                    Alarma.parar=false;
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), Alarma.class);
                    PendingIntent pendingIntent =  PendingIntent.getBroadcast(getApplicationContext(), CODIGO_ALARMA,intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),minutos*60, pendingIntent);
                }catch (NumberFormatException e){
                    Toast.makeText(Ajustes.this, "Debes poner los minutos correctamente", Toast.LENGTH_SHORT).show();
                }


            }
        });

        //Botón que comparte los datos de nuestra base de datos logros
        buttonCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),SensorManager.SENSOR_DELAY_NORMAL);
    }

    //Función que nos lanza una notificación cuando borramos los datos
    public void lanzarNotificacion(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ID_CANAL);

        builder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("DATOS BORRADOS")
                .setAutoCancel(false).setContentText("Los Datos se han borrado con exito");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String idChannel = "Canal 1";
            String nombreCanal = "Mi canal favorito";
            NotificationChannel notificationChannel = new NotificationChannel(idChannel, nombreCanal, NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            builder.setChannelId(idChannel);
            notificationManager.createNotificationChannel(notificationChannel);

        } else {

            //Menor que oreo
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        }

    }
    //Función que nos muestra en todo momento como está relleno nuestro listView
    public void MostrarDatos(){

        Cursor cursor = manejadorLogros.listar();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ID.add(cursor.getInt(0));
                FechaHora.add(cursor.getString(1));
                Resultado.add(cursor.getString(2));
            }
            RellenarDatos();
        }
    }
    //Función que nos rellena los datos de la aplicación
    private void RellenarDatos(){
        AdaptadorParaLogros adaptadorParaLogros = new AdaptadorParaLogros(this,R.layout.adaptador2,FechaHora);
        listView.setAdapter(adaptadorParaLogros);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType()==Sensor.TYPE_PROXIMITY){
            if(sensorEvent.values[0]==0){
                Log.i("SENSOR","CERCA");
                stopService(new Intent(getBaseContext(), ServicioMp3.class));
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), Alarma.class);
                PendingIntent pendingIntent =  PendingIntent.getBroadcast(getApplicationContext(), CODIGO_ALARMA,intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    //Clase con la que adaptaremos nuestras preguntas para que se vean mejor
    private class AdaptadorParaLogros extends ArrayAdapter<String> {

        public AdaptadorParaLogros(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
            super(context, resource, objects);

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return rellenarFila(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return rellenarFila(position, convertView, parent);
        }

        public View rellenarFila(int posicion, View view, ViewGroup padre) {

            LayoutInflater inflater = getLayoutInflater();
            View mifila = inflater.inflate(R.layout.adaptador2, padre, false);

            TextView id = mifila.findViewById(R.id.textViewID);
            id.setText(ID.get(posicion)+"");

            TextView fechayhora = mifila.findViewById(R.id.textViewFecha);
            fechayhora.setText(FechaHora.get(posicion));

            TextView logro = mifila.findViewById(R.id.textViewLogro);
            logro.setText(Resultado.get(posicion));

            return mifila;
        }
    }
}