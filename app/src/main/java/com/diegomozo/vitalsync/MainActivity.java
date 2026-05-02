package com.diegomozo.vitalsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import com.diegomozo.vitalsync.Controllers.MedicamentoController;
import com.diegomozo.vitalsync.Models.Medicamento;

public class MainActivity extends AppCompatActivity {

    private MedicamentoController controller;
    private TextView tvProximaToma;
    private Button btnConfirmarToma;
    private ImageView btnMenuOpciones;
    private ListView lvInventario;
    private Medicamento medActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new MedicamentoController(this);
        tvProximaToma = findViewById(R.id.tvProximaToma);
        btnConfirmarToma = findViewById(R.id.btnConfirmarToma);
        btnMenuOpciones = findViewById(R.id.btnMenuOpciones);
        lvInventario = findViewById(R.id.lvInventario);

        cargarUI();

        btnConfirmarToma.setOnClickListener(v -> {
            if (medActual != null) {
                if (medActual.getStockTotal() >= medActual.getDosis()) {
                    float nuevoStock = medActual.getStockTotal() - medActual.getDosis();
                    controller.actualizarStock(medActual.getIdMedicamento(), nuevoStock);
                    Toast.makeText(this, "Toma confirmada", Toast.LENGTH_SHORT).show();
                    cargarUI();
                } else {
                    Toast.makeText(this, "Stock insuficiente", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnMenuOpciones.setOnClickListener(this::mostrarMenu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarUI();
    }

    private void mostrarMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add("Añadir Medicamento");
        popup.getMenu().add("Ajustes");

        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Añadir Medicamento")) {
                startActivity(new Intent(MainActivity.this, SegundoActivity.class));
            } else if (item.getTitle().equals("Ajustes")) {
                startActivity(new Intent(MainActivity.this, AjustesActivity.class));
            }
            return true;
        });
        popup.show();
    }

    private void cargarUI() {
        List<Medicamento> lista = controller.obtenerMedicamentos();

        if (!lista.isEmpty()) {
            medActual = lista.get(0);
            tvProximaToma.setText(medActual.getNombre() + " " + medActual.getDosis() + medActual.getFormato());
            btnConfirmarToma.setEnabled(true);
        } else {
            medActual = null;
            tvProximaToma.setText("Sin medicamentos");
            btnConfirmarToma.setEnabled(false);
        }

        List<String> inventarioTexto = new ArrayList<>();
        for (Medicamento m : lista) {
            inventarioTexto.add(m.getNombre() + " - Stock restante: " + m.getStockTotal() + " " + m.getFormato());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, inventarioTexto);
        lvInventario.setAdapter(adapter);
    }
}