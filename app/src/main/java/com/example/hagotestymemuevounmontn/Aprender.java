package com.example.hagotestymemuevounmontn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Aprender extends AppCompatActivity {

    //Botones que usamos para aprender
    Button buttonSiguiente, buttonAnterior;

    //TextView  que nos mostrará las preguntas
    TextView textViewPregunta;

    //Spinner con el que mostraremos las respuestas
    ListView spinner;

    //Array de números donde iremos almacenando todos los números
    int[] numeros = new int[5];
    int[] respuestas= new int[3];
    int posicionP = 0;
    int posicionR=0;
    int preguntasAcertadas=0;

    //Preparamos el arraylist de clases
    ArrayList<Preguntas> preguntas = new ArrayList<>();
    ManejadorBD manejadorBD = new ManejadorBD(this);
    String[] respuestas1;
    ArrayAdapter<String> adaptador;

    //MediaPlayer que vamos a usar para hacer nuestro juego
    MediaPlayer mediaPlayerCorrecto,mediaPlayerIncorrecto ;

    //Static que usaremos para nuestra notificación
    private static final String ID_CANAL = "Canal para saber las notas";

    //Manejador de la Base de Datos Logros que nos permitirá meter los datos
    ManejadorLogros manejadorLogros= new ManejadorLogros(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aprender);
        buttonSiguiente=findViewById(R.id.buttonSiguiente);
        textViewPregunta=findViewById(R.id.textViewPreguntas);
        spinner=findViewById(R.id.spinner);
        //Creamos los mediaplayers que vamos a usar
        mediaPlayerCorrecto = MediaPlayer.create(this, R.raw.correcto);
        mediaPlayerIncorrecto=MediaPlayer.create(this,R.raw.incorrecto);
        //Llamos a nuestra función de generar datos y generamos las respuestas aleatorias
        MostrarDatos();
        numeros[posicionP]=GenerarRandomPreguntas();
        textViewPregunta.setText((posicionP+1)+"."+preguntas.get(numeros[posicionP]).getPregunta());
        respuestas[posicionR]=GenerarRandomRespuestas();
        if(respuestas[posicionR]==1){
            respuestas1=new String[]{preguntas.get(numeros[posicionP]).getRespuestaC(),preguntas.get(numeros[posicionP]).getRespuestaI1(),preguntas.get(numeros[posicionP]).getRespuestaI2()};
        }
        else if(respuestas[posicionR]==2){
            respuestas1=new String[]{preguntas.get(numeros[posicionP]).getRespuestaI1(),preguntas.get(numeros[posicionP]).getRespuestaC(),preguntas.get(numeros[posicionP]).getRespuestaI2()};
        }
        else{
            respuestas1=new String[]{preguntas.get(numeros[posicionP]).getRespuestaI2(),preguntas.get(numeros[posicionP]).getRespuestaI1(),preguntas.get(numeros[posicionP]).getRespuestaC()};
        }
        RellenarSpinner();
        buttonSiguiente.setEnabled(false);

        buttonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSiguiente.setEnabled(false);
                if(posicionP<4){
                    spinner.setEnabled(true);



                    spinner.setBackgroundColor(Color.WHITE);
                    spinner.setAdapter(null);
                    RepetirPreguntas();
                    textViewPregunta.setText((posicionP+1)+"."+preguntas.get(numeros[posicionP]).getPregunta());
                    respuestas[posicionR]=GenerarRandomRespuestas();
                    if(respuestas[posicionR]==1){
                        respuestas1=new String[]{preguntas.get(numeros[posicionP]).getRespuestaC(),preguntas.get(numeros[posicionP]).getRespuestaI1(),preguntas.get(numeros[posicionP]).getRespuestaI2()};
                    }
                    else if(respuestas[posicionR]==1){
                        respuestas1=new String[]{preguntas.get(numeros[posicionP]).getRespuestaI1(),preguntas.get(numeros[posicionP]).getRespuestaC(),preguntas.get(numeros[posicionP]).getRespuestaI2()};
                    }
                    else{
                        respuestas1=new String[]{preguntas.get(numeros[posicionP]).getRespuestaI2(),preguntas.get(numeros[posicionP]).getRespuestaI1(),preguntas.get(numeros[posicionP]).getRespuestaC()};
                    }
                    RellenarSpinner();
                }
                else{

                }

            }
        });

        //Hacemos que cuando seleccione una pregunta en el Spinner nos la coja por defecto
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinner.getItemAtPosition(i)==preguntas.get(numeros[posicionP]).getRespuestaC()){
                    if(Sonido.SONIDO==true){
                        mediaPlayerCorrecto.start();
                    }

                    preguntasAcertadas++;
                    spinner.setBackgroundColor(Color.RED);
                    spinner.getChildAt(i).setBackgroundColor(Color.GREEN);

                }
                else{
                    for(int j=0;j<spinner.getCount();j++){
                        if(spinner.getItemAtPosition(j)==preguntas.get(numeros[posicionP]).getRespuestaC()){
                            spinner.setBackgroundColor(Color.RED);
                            spinner.getChildAt(j).setBackgroundColor(Color.GREEN);
                            break;
                        }
                        if(Sonido.SONIDO==true){
                            mediaPlayerIncorrecto.start();
                        }

                    }
                }
                if(posicionP<4) {
                    buttonSiguiente.setEnabled(true);
                }
                else{
                    buttonSiguiente.setEnabled(false);
                    lanzarNotificacionConFoto();
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    manejadorLogros.insertar(date,Integer.toString(preguntasAcertadas));

                }
                spinner.setEnabled(false);
            }
        });


    }
    //Función que nos manda la Notificación para que sepamos la Nota que vamos a sacar
    private void lanzarNotificacionConFoto() {
        String idChannel = "Canal 4";
        String nombreCanal = "Mi canal con fotos";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ID_CANAL);

        builder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Has acerado "+preguntasAcertadas+" de 5")
                .setContentText("Despliega para ver más");

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        Bitmap bitmap;
        if(preguntasAcertadas==5){
             bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.diez);
        }
        else if(preguntasAcertadas>0){
             bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.resto);
        }
        else{
            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.cero);
        }

        bigPictureStyle.bigPicture(bitmap);
        bigPictureStyle.setBigContentTitle("La nota que has sacado");
        bigPictureStyle.setSummaryText("Enhorabuena, has acertado "+preguntasAcertadas+"/5 preguntas");

        builder.setStyle(bigPictureStyle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

        notificationManager.notify(4, builder.build());
    }
    //Función que nos rellena el spinner
    public void RellenarSpinner(){
        adaptador = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, respuestas1);
        spinner.setAdapter(adaptador);
    }

    //Función que nos comprueba que no se repite ninguna pregunta
    public void RepetirPreguntas(){
        while(true){
            Boolean repetido=false;
            int aleatorio=GenerarRandomPreguntas();
            for(int i=0;i<numeros.length-1;i++){
                if(numeros[i]==aleatorio){
                    repetido=true;
                }
            }
            if(!repetido){
                posicionP++;
                numeros[posicionP]=aleatorio;
                break;
            }
        }
    }
    //Función que nos muestra las preguntas
    public void MostrarDatos() {
        preguntas = new ArrayList<>();
        Cursor cursor = manejadorBD.listar();


        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                preguntas.add(new Preguntas(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            }
        }
    }
    //Función que nos genera un número aleatorio y lo devuelve el número
    public int GenerarRandomPreguntas(){
        int numero= (int)  Math.floor(Math.random()*preguntas.size());
        return numero;
    }
    //Función que nos genera las respuestas de manera aleatoria
    public int GenerarRandomRespuestas(){
        int numero= (int)  Math.floor(Math.random()*3+1);
        return numero;
    }
}