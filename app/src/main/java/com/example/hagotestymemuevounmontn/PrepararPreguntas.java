package com.example.hagotestymemuevounmontn;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PrepararPreguntas extends AppCompatActivity {

    ListView listView;
    ArrayList<Preguntas> preguntas;
    ManejadorBD manejadorBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparar_preguntas);
        listView=findViewById(R.id.listView);
        manejadorBD=new ManejadorBD(this);
        MostrarDatos();
    }

    //Función que nos muestra en todo momento como está relleno nuestro listView
    public void MostrarDatos(){
        preguntas = new ArrayList<>();
        Cursor cursor = manejadorBD.listar();
        ArrayAdapter<String> arrayAdapter;
        List<String> lista = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String fila = "";
                fila += "ID: " + cursor.getString(0);
                fila += "\n\tPREGUNTA: " + cursor.getString(1);
                fila += "\n\t RESPUESTA_C: " + cursor.getString(2);
                fila += "\n\t RESPUESTA_I: " + cursor.getString(3);
                fila += "\n\t RESPUESTA_I: " + cursor.getString(4);
                lista.add(fila);
                preguntas.add(new Preguntas(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)));
            }
            arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, lista);
            listView.setAdapter(arrayAdapter);
            /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    editTextPregunta.setText(preguntas.get(i).getId());
                    editTextRespuestaC.setText(preguntas.get(i).getPregunta());
                }
            });*/
        } else {
            Toast.makeText(PrepararPreguntas.this, "Nada que mostrar", Toast.LENGTH_SHORT).show();
        }
    }

    //Clase Preguntas que nos recoge los datos
    class Preguntas{
        String id,pregunta, respuestaC,respuestaI1, respuestaI2;

        public Preguntas(String id, String pregunta, String respuestaC, String respuestaI1, String respuestaI2) {
            this.id = id;
            this.pregunta = pregunta;
            this.respuestaC = respuestaC;
            this.respuestaI1= respuestaI1;
            this.respuestaI2=respuestaI2;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPregunta() {
            return pregunta;
        }

        public void setPregunta(String pregunta) {
            this.pregunta = pregunta;
        }

        public String getRespuestaC() {
            return respuestaC;
        }

        public void setRespuestaC(String respuestaC) {
            this.respuestaC = respuestaC;
        }

        public String getRespuestaI1() {
            return respuestaI1;
        }

        public void setRespuestaI1(String respuestaI1) {
            this.respuestaI1 = respuestaI1;
        }
        public String getRespuestaI2() {
            return respuestaI2;
        }

        public void setRespuestaI2(String respuestaI2) {
            this.respuestaI2 = respuestaI2;
        }
    }

}