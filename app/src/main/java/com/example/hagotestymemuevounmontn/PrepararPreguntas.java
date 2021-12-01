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
import android.widget.Button;
import android.widget.EditText;
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
    AdaptadorParaPreguntas adaptadorParaPreguntas;

    //EditText que vamos a rellenar
    EditText editTextID, editTextPregunta, editTextRespuestaC, editTextRespuestaI1, editTextRespuestaI2;

    //Botones que vamos a usar
    Button buttonInsertar, buttonModificar, buttonBorrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparar_preguntas);
        listView=findViewById(R.id.listView);
        //Los editText buscados
        editTextID=findViewById(R.id.editTextID);
        editTextPregunta=findViewById(R.id.editTextPregunta);
        editTextRespuestaC=findViewById(R.id.editTextRespuestaC);
        editTextRespuestaI1=findViewById(R.id.editTextRespuestaI1);
        editTextRespuestaI2=findViewById(R.id.editTextRespuestaI2);
        //Buscamos los botones
        buttonInsertar=findViewById(R.id.buttonInsertar);
        buttonModificar=findViewById(R.id.buttonModificar);
        buttonBorrar=findViewById(R.id.buttonBorrar);
        manejadorBD=new ManejadorBD(this);
        MostrarDatos();

        //Boton que nos inserta datos nuevos dentro de la base de Datos
        buttonInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean resultado = manejadorBD.insertar(editTextPregunta.getText().toString().trim(), editTextRespuestaC.getText().toString().trim(), editTextRespuestaI1.getText().toString().trim(),editTextRespuestaI2.getText().toString().trim());
                if (resultado) {
                    Toast.makeText(PrepararPreguntas.this, "Pregunta insertada Correctamente", Toast.LENGTH_SHORT).show();
                    adaptadorParaPreguntas.clear();
                    MostrarDatos();
                } else {
                    Toast.makeText(PrepararPreguntas.this, "Error en la inserción", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Botón que nos borra una pregunta
        buttonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean borrado = manejadorBD.borrar(editTextID.getText().toString());
                Toast.makeText(PrepararPreguntas.this, borrado ? "Borrado Correctamente" : "No se ha borrado nada", Toast.LENGTH_SHORT).show();
                adaptadorParaPreguntas.clear();
                MostrarDatos();
            }
        });

        //Botón que nos modifica los datos
        buttonModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean resultado = manejadorBD.actualizar(editTextID.getText().toString().trim(),editTextPregunta.getText().toString().trim(), editTextRespuestaC.getText().toString().trim(), editTextRespuestaI1.getText().toString().trim(),editTextRespuestaI2.getText().toString().trim());
                if (resultado) {
                    Toast.makeText(PrepararPreguntas.this, "Pregunta modificada Correctamente", Toast.LENGTH_SHORT).show();
                    adaptadorParaPreguntas.clear();
                    MostrarDatos();
                } else {
                    Toast.makeText(PrepararPreguntas.this, "Error en la inserción", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            adaptadorParaPreguntas = new AdaptadorParaPreguntas(this,R.layout.adaptador,Preguntas);
            listView.setAdapter(adaptadorParaPreguntas);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    editTextID.setText(preguntas.get(i).getId());
                    editTextPregunta.setText(preguntas.get(i).getPregunta());
                    editTextRespuestaC.setText(preguntas.get(i).getRespuestaC());
                    editTextRespuestaI1.setText(preguntas.get(i).getRespuestaI1());
                    editTextRespuestaI2.setText(preguntas.get(i).getRespuestaI2());
                }
            });
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
            textRespuesta1.setText("·"+RespuestaC.get(posicion));

            TextView textRespuesta2 = mifila.findViewById(R.id.textViewRespuesta2);
            textRespuesta2.setText("·"+RespuestaI1.get(posicion));

            TextView textRespuesta3 = mifila.findViewById(R.id.textViewRespuesta3);
            textRespuesta3.setText("·"+RespuestaI2.get(posicion));


            return mifila;
        }
    }
}