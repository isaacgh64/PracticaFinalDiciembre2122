package com.example.hagotestymemuevounmontn;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Aprender extends AppCompatActivity {

    //Botones que usamos para aprender
    Button buttonSiguiente, buttonAnterior;

    //TextView  que nos mostrará las preguntas
    TextView textViewPregunta;

    //Spinner con el que mostraremos las respuestas
    Spinner spinner;

    //Array de números donde iremos almacenando todos los números
    int[] numeros = new int[5];
    int[] respuestas= new int[3];
    int posicionP = 0;
    int posicionR=0;

    //Preparamos el arraylist de clases
    ArrayList<Preguntas> preguntas = new ArrayList<>();
    ManejadorBD manejadorBD = new ManejadorBD(this);
    String[] respuestas1;
    ArrayAdapter<String> adaptador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aprender);
        buttonSiguiente=findViewById(R.id.buttonSiguiente);
        buttonAnterior=findViewById(R.id.buttonAnterior);
        textViewPregunta=findViewById(R.id.textViewPreguntas);
        spinner=findViewById(R.id.spinner);

        //Llamos a nuestra función de generar datos y generamos las respuestas aleatorias
        MostrarDatos();
        numeros[posicionP]=GenerarRandomPreguntas();
        textViewPregunta.setText((posicionP+1)+"."+preguntas.get(numeros[posicionP]).getPregunta());
        respuestas[posicionR]=GenerarRandomRespuestas();
        if(respuestas[posicionR]==1){
            respuestas1=new String[]{"Selecciona Respuesta",preguntas.get(numeros[posicionP]).getRespuestaC(),preguntas.get(numeros[posicionP]).getRespuestaI1(),preguntas.get(numeros[posicionP]).getRespuestaI2()};
        }
        else if(respuestas[posicionR]==1){
            respuestas1=new String[]{"Selecciona Respuesta",preguntas.get(numeros[posicionP]).getRespuestaI1(),preguntas.get(numeros[posicionP]).getRespuestaC(),preguntas.get(numeros[posicionP]).getRespuestaI2()};
        }
        else{
            respuestas1=new String[]{"Selecciona Respuesta",preguntas.get(numeros[posicionP]).getRespuestaI2(),preguntas.get(numeros[posicionP]).getRespuestaI1(),preguntas.get(numeros[posicionP]).getRespuestaC()};
        }
        RellenarSpinner();

        buttonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(posicionP!=5){
                    spinner.setAdapter(null);
                    RepetirPreguntas();
                    textViewPregunta.setText((posicionP+1)+"."+preguntas.get(numeros[posicionP]).getPregunta());
                    respuestas[posicionR]=GenerarRandomRespuestas();
                    if(respuestas[posicionR]==1){
                        respuestas1=new String[]{"Selecciona Respuesta",preguntas.get(numeros[posicionP]).getRespuestaC(),preguntas.get(numeros[posicionP]).getRespuestaI1(),preguntas.get(numeros[posicionP]).getRespuestaI2()};
                    }
                    else if(respuestas[posicionR]==1){
                        respuestas1=new String[]{"Selecciona Respuesta",preguntas.get(numeros[posicionP]).getRespuestaI1(),preguntas.get(numeros[posicionP]).getRespuestaC(),preguntas.get(numeros[posicionP]).getRespuestaI2()};
                    }
                    else{
                        respuestas1=new String[]{"Selecciona Respuesta",preguntas.get(numeros[posicionP]).getRespuestaI2(),preguntas.get(numeros[posicionP]).getRespuestaI1(),preguntas.get(numeros[posicionP]).getRespuestaC()};
                    }
                    RellenarSpinner();
                }
                else{

                }

            }
        });


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
            for(int i=0;i<numeros.length;i++){
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