package com.example.hagotestymemuevounmontn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PrepararPreguntas extends AppCompatActivity {

    ListView listView;
    ArrayList<Preguntas> preguntas;
    ManejadorBD manejadorBD;
    ArrayList<Integer> ID= new ArrayList();
    ArrayList<String> Preguntas= new ArrayList();
    ArrayList<String> RespuestaC= new ArrayList();
    ArrayList<String> RespuestaI1= new ArrayList();
    ArrayList<String> RespuestaI2= new ArrayList();

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


        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String fila = "";
                ID.add(cursor.getInt(0));
                Preguntas.add(cursor.getString(1));
                RespuestaC.add(cursor.getString(2));
                RespuestaI1.add(cursor.getString(3));
                RespuestaI2.add(cursor.getString(4));
                preguntas.add(new Preguntas(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)));
            }
            AdaptadorParaPreguntas adaptadorParaPreguntas = new AdaptadorParaPreguntas(this,R.layout.adaptador,Preguntas);
            listView.setAdapter(adaptadorParaPreguntas);

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
    //Clase con la que adaptaremos nuestras preguntas para que se vean mejor
    private class AdaptadorParaPreguntas extends ArrayAdapter<String> {

        public AdaptadorParaPreguntas(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
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
            View mifila = inflater.inflate(R.layout.adaptador, padre, false);

            TextView pregunta = mifila.findViewById(R.id.textViewPregunta);
            pregunta.setText(ID.get(posicion)+"."+Preguntas.get(posicion));

            TextView textRespuesta1 = mifila.findViewById(R.id.textViewRespuesta1);
            textRespuesta1.setText("-"+RespuestaC.get(posicion));

            TextView textRespuesta2 = mifila.findViewById(R.id.textViewRespuesta2);
            textRespuesta2.setText("-"+RespuestaI1.get(posicion));

            TextView textRespuesta3 = mifila.findViewById(R.id.textViewRespuesta3);
            textRespuesta3.setText("-"+RespuestaI2.get(posicion));


            return mifila;
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