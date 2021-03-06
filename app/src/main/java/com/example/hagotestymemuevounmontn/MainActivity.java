package com.example.hagotestymemuevounmontn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Creamos las Variables que vamos a usar en la primera ventana
    Button buttonEntrar, buttonFoto;
    ImageView imageView;
    EditText editTextContrasena;
    Boolean primera_vez = false;
    String contrasena_guardada;
    PantallaEncendida pantallaEncendida = new PantallaEncendida();

    ManejadorBDatos manejadorBDatos = new ManejadorBDatos(this);
    ManejadorLogros manejadorLogros= new ManejadorLogros(this);
    ManejadorBD manejadorBD= new ManejadorBD(this);

    private static final String ID_CANAL = "El nombre de mi canal";
    //Variables para obtener la localizaci??n
    LocationManager locationManager;
    LocationListener locationListener;
    String latitud;
    String altitud;

    //Variables est??ticas para el sharedpreferences
    static final String NOMBRE_FICHERO = "DATOS";
    static final String ETIQUETA_CONTRA = "CONTRA";
    static final String ETIQUETA_FOTO = "FOTO";
    static final String ETIQUETA_CONTRA_GUARDADA = "BOOLEAN";

    //Variables para usar las fotos y los permisos
    private static final int VENGO_DE_GALERIA = 100;
    private static final int PEDI_PERMISO_ESCRITURA = 1;
    private static final int VENGO_DE_CAMARA_CON_CALIDAD = 2;
    private static final long TIEMPO_REFRESCO = 10000;
    private static final int PERMISO_GPS = 5;
    Uri imagenUri;

    //Fichero para poder usar la foto
    private File fichero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Asociamos cada variable con su id del layout
        buttonFoto = findViewById(R.id.buttonFoto);
        buttonEntrar = findViewById(R.id.buttonAcceder);
        imageView = findViewById(R.id.imageView);
        editTextContrasena = findViewById(R.id.editTextContrasena);



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitud= String.valueOf(location.getLatitude());
                altitud=String.valueOf(location.getLongitude());
            }
            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };
        pedirPermisoGps();

        //Usamos el IntentFilter para que cada vez que se encienda la Pantalla me recoja los datos
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        getBaseContext().registerReceiver(pantallaEncendida, intentFilter);


        //Doy permisos para que la c??mara pueda ver el fichero que he creado
        StrictMode.VmPolicy.Builder builder= new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //Ponemos que al cargar nos compruebe si tenemos una foto ya en sharedpreferences para que nos la guarde y nos coja la contrase??a
        SharedPreferences misDatos = getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
        String ficheros=(misDatos.getString(ETIQUETA_FOTO,null));
        Log.d("Depurando", ""+ficheros);
        if(ficheros!=null){
            File fichero_foto= new File(ficheros);
            Bitmap rotacion=BitmapFactory.decodeFile(fichero_foto.getAbsolutePath());
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(fichero_foto.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            int grados=0;

            Matrix matrix= new Matrix();
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    grados=90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    grados=180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    grados=270;
                    break;
                default:
                    break;
            }
            imageView.setImageBitmap(rotacion);
            imageView.setRotation(grados);
        }
        //Buscamos tambi??n que est?? guardado nuestra contrase??a
        SharedPreferences miContra= getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
        contrasena_guardada=(miContra.getString(ETIQUETA_CONTRA,null));
        //Buscamos tambi??n que no tengamos guardado nada en nuestra variable Boolean
        SharedPreferences entrado1vez= getSharedPreferences(NOMBRE_FICHERO,MODE_PRIVATE);
        primera_vez=(entrado1vez.getBoolean(ETIQUETA_CONTRA_GUARDADA,false));
        if(primera_vez){
            editTextContrasena.setHint(R.string.contra);
        }

        //Configuramos el bot??n de la foto para que al darle nos permita hacernos una foto
        buttonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pedirpermisoParaFoto();
            }
        });

        //Configuramos el bot??n de entrar para que nos guarde la contrease??a y as?? pueda entrar
        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contrasena= editTextContrasena.getText().toString();
                if(contrasena.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.contra_blanco);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.show();
                }
                else{
                    //Comprobamos que no es la primera vez que entra
                    if(primera_vez){
                        if(contrasena_guardada.compareTo(contrasena)!=0){
                            Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
                            editTextContrasena.startAnimation(animation);
                            buttonEntrar.startAnimation(animation);
                            animation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    editTextContrasena.setTextColor(Color.RED);
                                    buttonEntrar.setTextColor(Color.RED);
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    editTextContrasena.setTextColor(Color.BLACK);
                                    buttonEntrar.setTextColor(Color.WHITE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                        }
                        else{
                            //Lanzamos a la actividad siguiente
                            LanzarNotificacion();
                            Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                            startActivity(intent);
                        }
                    }
                    else {
                        //Guardamos la contrase??a en nuestro sharedpreferences
                        SharedPreferences micontrasena = getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
                        SharedPreferences.Editor editor_contra = micontrasena.edit();
                        editor_contra.putString(ETIQUETA_CONTRA, editTextContrasena.getText().toString());
                        editor_contra.apply();
                        //Guardamos una variable Boolean para que
                        SharedPreferences misDatos1 = getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
                        SharedPreferences.Editor editor = misDatos1.edit();
                        editor.putBoolean(ETIQUETA_CONTRA_GUARDADA, true);
                        editor.apply();
                        //Lanzamos a la actividad siguiente
                        Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }
    //Funci??n que nos muestra una notificaci??n al entrar con nuestro ??ltimo resultado y con los metros que hemos recorrido desde la ??ltima vez
    private void LanzarNotificacion(){
        //Cogemos el ??ltimo logro que tenemos registrado en nuestra tabla
        Cursor cursor = manejadorLogros.listarUltimo();
        float dist;
        String logro="";
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                logro=cursor.getString(0);
            }

        }
        String idChannel = "Canal 3";
        String nombreCanal = "Mi canal muchas l??neas";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ID_CANAL);
        builder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(getString(R.string.NotifiM))
                .setContentText(getString(R.string.NotifiM1));

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        if(!logro.isEmpty()){
            Cursor cursor1= manejadorBDatos.listarUltimos();
            String[] latitud1= new String[2];
            String[] altitud1= new String[2];
            int i=0;
            if (cursor1 != null && cursor1.getCount() > 0) {
                while (cursor1.moveToNext()) {
                    latitud1[i]=cursor1.getString(0);
                    altitud1[i]=cursor1.getString(1);
                    i++;
                }
            }
            if(latitud1[0]!=null){
                double lat1=Double.parseDouble(latitud1[0]);
                double lat2=Double.parseDouble(latitud1[1]);

                double lng1=Double.parseDouble(altitud1[0]);
                double lng2=Double.parseDouble(altitud1[1]);

                double earthRadius = 6371; //kilometers
                double dLat = Math.toRadians(lat2-lat1);
                double dLng = Math.toRadians(lng2-lng1);
                double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                dist = (float) (earthRadius * c);

            }
            else{
                dist=0;
            }




            inboxStyle.setBigContentTitle(getString(R.string.Datos));
            inboxStyle.addLine(getString(R.string.Resultado)+logro+getString(R.string.puntos));
            inboxStyle.addLine(getString(R.string.Recorrido)+dist+getString(R.string.metrosult));
        }
        else{
            inboxStyle.setBigContentTitle(getString(R.string.nodata));
        }

        builder.setStyle(inboxStyle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(idChannel, nombreCanal, NotificationManager.IMPORTANCE_HIGH);
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
        notificationManager.notify(1, builder.build());
    }
    //Funci??n que nos lanza la notificaci??n del volumen
    public void LanzarNotificacion2(){
        //Cogemos el ??ltimo logro que tenemos registrado en nuestra tabla
        Cursor cursor = manejadorBD.listar();
        int posicion=0;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                posicion++;
            }

        }
        String idChannel = "Canal 3";
        String nombreCanal = "Mi canal muchas l??neas";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ID_CANAL);
        builder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Aqu?? van tus datos de la ??ltima vez")
                .setContentText("Pulsa para verlos mejor");

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle("Buenas tardes");
            inboxStyle.addLine("El total de preguntas en la aplicaci??n es de: "+posicion );
            if(Sonido.ULTCORRECTA==""){
                inboxStyle.addLine("Todav??a no has jugado ");
            }
            else{
                inboxStyle.addLine("La ult pregunta que fallaste fue "+Sonido.ULTINCORRECTA);
                inboxStyle.addLine("La respuesta Correcta era "+Sonido.ULTCORRECTA);
            }

        builder.setStyle(inboxStyle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(idChannel, nombreCanal, NotificationManager.IMPORTANCE_HIGH);
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
        notificationManager.notify(3, builder.build());
    }

    //Funci??n   que nos pide el permiso para usar el GPS
    private void pedirPermisoGps(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISO_GPS);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIEMPO_REFRESCO, 0, locationListener);
    }
    //Funci??n con la que le pedimos al usuario permiso para acceder a la c??mara y hacerla con calidad
    private void pedirpermisoParaFoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            }
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PEDI_PERMISO_ESCRITURA);
        }
        else{
            DialogoQueHacer();
        }
    }
    //Dialogo que nos muestra que podemos seleccionar la c??mara o la galer??a
    private void DialogoQueHacer(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.HacerFoto);
        builder.setPositiveButton(R.string.Camara, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                hacerLaFotoConCalidad();
            }
        });
        builder.setNegativeButton(R.string.Galeria, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SeleccionarGaleria();
            }
        });
        builder.show();
    }
    //Funci??n que pide los permisos que necesitamos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PEDI_PERMISO_ESCRITURA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                DialogoQueHacer();
            } else {
                Toast.makeText(this, R.string.SinPermiso, Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PERMISO_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.Sinper), Toast.LENGTH_SHORT).show();
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIEMPO_REFRESCO, 0, locationListener);
                }

            } else {
                Toast.makeText(this, getString(R.string.Necesitocosas), Toast.LENGTH_SHORT).show();
            }
        }
    }
    //Funci??n que hace la foto con Calidad
    private void hacerLaFotoConCalidad() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            fichero = crearFicheroFoto();
        } catch (IOException e) {
            e.printStackTrace();
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider",fichero));

        if (intent.resolveActivity(getPackageManager()) != null) { //Debo permitir la consulta en el android manifest
            startActivityForResult(intent, VENGO_DE_CAMARA_CON_CALIDAD);
        } else {
            Toast.makeText(MainActivity.this, R.string.NecesitasCamara, Toast.LENGTH_SHORT).show();
        }

    }
    //Funci??n que nos abre la galer??a
    private void SeleccionarGaleria(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, VENGO_DE_GALERIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VENGO_DE_GALERIA && resultCode == RESULT_OK) {
            imagenUri = data.getData();
            imageView.setImageURI(imagenUri);
            imageView.setRotation((float) 90.0);

        } else if (requestCode == VENGO_DE_CAMARA_CON_CALIDAD) {
            if(resultCode==RESULT_OK){
                Bitmap rotacion=BitmapFactory.decodeFile(fichero.getAbsolutePath());
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(fichero.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                int grados=0;

                Matrix matrix= new Matrix();
                switch (orientation){
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        grados=90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        grados=180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        grados=270;
                        break;
                    default:
                        break;
                }
                imageView.setImageBitmap(rotacion);
                imageView.setRotation(grados);
            }
            else{
                fichero.delete();
            }

            //Guardamos la foto en nuestro sharedpreferences
            SharedPreferences misDatos = getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
            SharedPreferences.Editor editor = misDatos.edit();
            editor.putString(ETIQUETA_FOTO, fichero.getAbsolutePath());
            editor.apply();
        }
    }
    //Funci??n que crea el Fichero
    private File crearFicheroFoto() throws IOException {
        String fechaYHora = new SimpleDateFormat("yyyyMMdd_HH_mm_ss_").format(new Date());
        String nombreFichero = "fotos_" + fechaYHora;
        File carpetaFotos = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        carpetaFotos.mkdirs();
        File imagenAltaResolucion = File.createTempFile(nombreFichero, ".jpg", carpetaFotos);
        return imagenAltaResolucion;
    }
    //Clase que realiza la funci??n de saber si est?? la pantalla encendida o no
    private class PantallaEncendida extends BroadcastReceiver {
        String ETIQUETA = "ESTADO";


        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = registerReceiver(null, ifilter);

                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                float bateria = (level / (float)scale)*100;

                String fechaYHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
                Log.i(ETIQUETA, "Pantalla Encendida/ Bateria="+bateria+" /Latitud= "+latitud+" Altitud= "+altitud+"/"+fechaYHora);
                manejadorBDatos.insertar(fechaYHora, String.valueOf(bateria),latitud,altitud);
                Log.i(ETIQUETA,"Datos Guardados");

            }
            if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
                Log.i("ESTADO_VOLUMEN","HE SUBIDO O BAJADO");
                LanzarNotificacion2();
            }
        }
    }
}