package com.example.hagotestymemuevounmontn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MenuPrincipal extends AppCompatActivity {
    Button buttonPreparar, buttonJugar, buttonAjustes;
    ManejadorBD manejadorBD= new ManejadorBD(this);
    int preguntitas=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        buttonPreparar=findViewById(R.id.buttonPreparar);
        buttonJugar=findViewById(R.id.buttonJugar);
        buttonAjustes=findViewById(R.id.buttonAjustes);

        buttonPreparar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this,PrepararPreguntas.class);
                startActivity(intent);
            }
        });
        buttonJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = manejadorBD.listar();
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        preguntitas++;
                    }
                }
                if(preguntitas>=5){
                    Intent intent = new Intent(MenuPrincipal.this,Aprender.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MenuPrincipal.this, "Debes tener mínimo 5 preguntas en la BD, ve a hacer preguntas", Toast.LENGTH_LONG).show();
                }

            }
        });
        buttonAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this,Ajustes.class);
                startActivity(intent);
            }
        });
    }
}