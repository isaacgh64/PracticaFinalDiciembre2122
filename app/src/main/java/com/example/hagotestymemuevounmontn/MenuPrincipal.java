package com.example.hagotestymemuevounmontn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuPrincipal extends AppCompatActivity {
    Button buttonPreparar, buttonJugar, buttonAjustes;
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
                Intent intent = new Intent(MenuPrincipal.this,Aprender.class);
                startActivity(intent);
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