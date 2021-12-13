package com.example.hagotestymemuevounmontn;

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
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Ajustes extends AppCompatActivity {
    private static final String ID_CANAL = "El nombre de mi canal";
    private static final int CODIGO_ALARMA = 777;
    ListView listView;
    Button buttonBorrarD, buttonActivar, buttonCompartir;
    EditText editTextminutos;
    ManejadorLogros manejadorLogros = new ManejadorLogros(this);
    ManejadorBDatos manejadorBDatos= new ManejadorBDatos(this);
    ArrayAdapter<String> arrayAdapter;
    List<String> lista;
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
        buttonCompartir=findViewById(R.id.buttonActivar);
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
                lista.removeAll(lista);
                listView.setAdapter(arrayAdapter);
                lanzarNotificacion();
            }
        });

        //Botón que al ser pulsado nos activa nuestra alarma
        buttonActivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minutos= Integer.parseInt(editTextminutos.getText().toString().trim());
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), Alarma.class);
                Toast.makeText(Ajustes.this, System.currentTimeMillis()+minutos+"", Toast.LENGTH_LONG).show();
                PendingIntent pendingIntent =  PendingIntent.getBroadcast(getApplicationContext(), CODIGO_ALARMA,intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),29000, pendingIntent);
            }
        });
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
        lista = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String fila = "";
                /*ID.add(cursor.getInt(0));
                FechaHora.add(cursor.getString(1));
                Resultado.add(cursor.getString(2));**/
                fila += "ID: " + cursor.getString(0);
                fila += " FECHAYHORA: " + cursor.getString(1);
                fila += " RESULTADO: " + cursor.getString(2);
                lista.add(fila);
            }
            arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, lista);
            listView.setAdapter(arrayAdapter);
        } else {
            Toast.makeText(Ajustes.this, "Nada que mostrar", Toast.LENGTH_SHORT).show();
        }
    }
}