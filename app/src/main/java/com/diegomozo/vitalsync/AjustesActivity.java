package com.diegomozo.vitalsync;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AjustesActivity extends AppCompatActivity {

    private ImageView btnVolverAjustes;
    private Switch swAnuncios;
    private Button btnBorrarDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        btnVolverAjustes = findViewById(R.id.btnVolverAjustes);
        swAnuncios = findViewById(R.id.swAnuncios);
        btnBorrarDatos = findViewById(R.id.btnBorrarDatos);

        btnVolverAjustes.setOnClickListener(v -> finish());

        btnBorrarDatos.setOnClickListener(v -> {
            deleteDatabase("vitalsync.db");
            Toast.makeText(this, "Todos los datos han sido borrados", Toast.LENGTH_LONG).show();
            finishAffinity();
        });
    }
}