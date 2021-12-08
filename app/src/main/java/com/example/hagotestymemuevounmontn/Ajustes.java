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

public class Ajustes extends AppCompatActivity {
    ListView listView;
    ManejadorLogros manejadorLogros = new ManejadorLogros(this);
    ArrayList<Integer> ID= new ArrayList();
    ArrayList<String> FechaHora= new ArrayList();
    ArrayList<String> Resultado= new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        listView=findViewById(R.id.listView1);
        MostrarDatos();
    }
    //Función que nos muestra en todo momento como está relleno nuestro listView
    public void MostrarDatos(){

        Cursor cursor = manejadorLogros.listar();
        ArrayAdapter<String> arrayAdapter;
        List<String> lista = new ArrayList<>();

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