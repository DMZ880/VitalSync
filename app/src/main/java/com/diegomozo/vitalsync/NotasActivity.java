package com.diegomozo.vitalsync;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.diegomozo.vitalsync.Controllers.MedicamentoController;
import com.diegomozo.vitalsync.Models.Nota;
import java.util.List;


public class NotasActivity extends AppCompatActivity {

    private MedicamentoController controller;
    private ListView lvNotas;
    private ImageView btnVolverNotas;
    private List<Nota> listaNotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);

        controller = new MedicamentoController(this);
        lvNotas = findViewById(R.id.lvNotas);
        btnVolverNotas = findViewById(R.id.btnVolverNotas);

        btnVolverNotas.setOnClickListener(v -> finish());

        cargarNotas();

        lvNotas.setOnItemClickListener((parent, view, position, id) -> {
            Nota nota = listaNotas.get(position);
            mostrarDetalleNota(nota);
        });
    }

    private void cargarNotas() {
        listaNotas = controller.obtenerListaNotas();
        ArrayAdapter<Nota> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaNotas);
        lvNotas.setAdapter(adapter);
    }

    private void mostrarDetalleNota(Nota nota) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        builder.setTitle("Detalle de la nota");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 20);

        TextView tvDetalle = new TextView(this);
        tvDetalle.setText("Tomado el: " + nota.getFechaHora() + "\n\n" +
                "Medicamento: " + nota.getNombreMedicamento() + "\n\n" +
                "Nota:\n" + nota.getTextoNota());
        tvDetalle.setTextSize(16);
        tvDetalle.setTextColor(Color.BLACK);
        layout.addView(tvDetalle);

        builder.setView(layout);

        builder.setPositiveButton("Borrar Nota", (dialog, which) -> {
            controller.borrarNota(nota.getIdMedicamento(), nota.getFechaHora());
            Toast.makeText(this, "Nota borrada", Toast.LENGTH_SHORT).show();
            cargarNotas();
        });

        builder.setNegativeButton("Cerrar", null);

        builder.show();
    }
}