package com.example.hagotestymemuevounmontn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //Creamos las Variables que vamos a usar en la primera ventana
    Button buttonEntrar,buttonFoto;
    ImageView imageView;
    EditText editTextContrasena;
    Boolean primera_vez=false;
    String contrasena_guardada;
    PantallaEncendida pantallaEncendida = new PantallaEncendida();
    ManejadorBDatos manejadorBDatos = new ManejadorBDatos(this);

    //Variables estáticas para el sharedpreferences
    static final String NOMBRE_FICHERO="DATOS";
    static final String ETIQUETA_CONTRA="CONTRA";
    static final String ETIQUETA_FOTO="FOTO";
    static final String ETIQUETA_CONTRA_GUARDADA="BOOLEAN";

    //Variables para usar las fotos
    private static final int VENGO_DE_GALERIA = 100;
    private static final int PEDI_PERMISO_ESCRITURA = 1;
    private static final int VENGO_DE_CAMARA_CON_CALIDAD = 2;
    Uri imagenUri;

    //Fichero para poder usar la foto
    private File fichero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Asociamos cada variable con su id del layout
        buttonFoto=findViewById(R.id.buttonFoto);
        buttonEntrar=findViewById(R.id.buttonAcceder);
        imageView=findViewById(R.id.imageView);
        editTextContrasena=findViewById(R.id.editTextContrasena);

        //Usamos el IntentFilter para que cada vez que se encienda la Pantalla me recoja los datos
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        getBaseContext().registerReceiver(pantallaEncendida, intentFilter);


        //Doy permisos para que la cámara pueda ver el fichero que he creado
        StrictMode.VmPolicy.Builder builder= new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //Ponemos que al cargar nos compruebe si tenemos una foto ya en sharedpreferences para que nos la guarde y nos coja la contraseña
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
        //Buscamos también que esté guardado nuestra contraseña
        SharedPreferences miContra= getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
        contrasena_guardada=(miContra.getString(ETIQUETA_CONTRA,null));
        //Buscamos también que no tengamos guardado nada en nuestra variable Boolean
        SharedPreferences entrado1vez= getSharedPreferences(NOMBRE_FICHERO,MODE_PRIVATE);
        primera_vez=(entrado1vez.getBoolean(ETIQUETA_CONTRA_GUARDADA,false));
        if(primera_vez){
            editTextContrasena.setHint(R.string.contra);
        }


        //Configuramos el botón de la foto para que al darle nos permita hacernos una foto
        buttonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pedirpermisoParaFoto();
            }
        });

        //Configuramos el botón de entrar para que nos guarde la contreaseña y así pueda entrar
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
                            Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                            startActivity(intent);
                        }
                    }
                    else {
                        //Guardamos la contraseña en nuestro sharedpreferences
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

    //Función con la que le pedimos al usuario permiso para acceder a la cámara y hacerla con calidad
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
    //Dialogo que nos muestra que podemos seleccionar la cámara o la galería
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
    //Función que pide los permisos que necesitamos
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

    }
    //Función que hace la foto con Calidad
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
    //Función que nos abre la galería
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
    //Función que crea el Fichero
    private File crearFicheroFoto() throws IOException {
        String fechaYHora = new SimpleDateFormat("yyyyMMdd_HH_mm_ss_").format(new Date());
        String nombreFichero = "fotos_" + fechaYHora;
        File carpetaFotos = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        carpetaFotos.mkdirs();
        File imagenAltaResolucion = File.createTempFile(nombreFichero, ".jpg", carpetaFotos);
        return imagenAltaResolucion;
    }
    //Clase que realiza la función de saber si está la pantalla encendida o no
    private class PantallaEncendida extends BroadcastReceiver {
        String ETIQUETA = "ESTADO";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

                Log.i(ETIQUETA, "Pantalla Encendida/ Bateria= ");

            }
        }
    }
}