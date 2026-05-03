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
        ArrayAdapter<Nota> adapter = new ArrayAdapter<>(this, R.layout.item_lista_nota, listaNotas);
        lvNotas.setAdapter(adapter);
    }

    private void mostrarDetalleNota(Nota nota) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle("📝 " + nota.getNombreMedicamento());

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 20);

        android.widget.TextView tvFecha = new android.widget.TextView(this);
        tvFecha.setText("🕒 " + nota.toString().split(" \\| ")[0]);
        tvFecha.setTextSize(14);
        tvFecha.setTextColor(android.graphics.Color.DKGRAY);
        layout.addView(tvFecha);

        android.widget.TextView tvDetalle = new android.widget.TextView(this);
        tvDetalle.setText("\n" + nota.getTextoNota());
        tvDetalle.setTextSize(18);
        tvDetalle.setTextColor(android.graphics.Color.BLACK);
        layout.addView(tvDetalle);

        builder.setView(layout);

        builder.setPositiveButton("Borrar Nota", (dialog, which) -> {
            controller.borrarNota(nota.getIdMedicamento(), nota.getFechaHora());
            android.widget.Toast.makeText(this, "Nota borrada", android.widget.Toast.LENGTH_SHORT).show();
            cargarNotas();
        });

        builder.setNegativeButton("Cerrar", null);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(android.graphics.Color.parseColor("#D32F2F"));
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(android.graphics.Color.parseColor("#757575"));
    }
}