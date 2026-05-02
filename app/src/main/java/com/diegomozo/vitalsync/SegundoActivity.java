package com.diegomozo.vitalsync;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.diegomozo.vitalsync.Controllers.MedicamentoController;
import com.diegomozo.vitalsync.Models.Medicamento;

public class SegundoActivity extends AppCompatActivity {

    private EditText etNombre, etDosis, etStock, etFrecuencia;
    private Spinner spFormato;
    private Button btnGuardar;
    private ImageView btnVolver;
    private MedicamentoController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.segundo_activity);

        controller = new MedicamentoController(this);

        etNombre = findViewById(R.id.etNombre);
        etDosis = findViewById(R.id.etDosis);
        spFormato = findViewById(R.id.spFormato);
        etStock = findViewById(R.id.etStock);
        etFrecuencia = findViewById(R.id.etFrecuencia);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnVolver = findViewById(R.id.btnVolver);

        String[] formatos = {"mg", "ml", "pastillas", "gotas", "g"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, formatos);
        spFormato.setAdapter(adapter);

        btnVolver.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v -> guardarDatos());
    }

    private void guardarDatos() {
        String nombre = etNombre.getText().toString().trim();
        String dosisStr = etDosis.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        String frecuenciaStr = etFrecuencia.getText().toString().trim();

        if (nombre.isEmpty()) { etNombre.setError("Requerido"); return; }
        if (dosisStr.isEmpty()) { etDosis.setError("Requerido"); return; }
        if (stockStr.isEmpty()) { etStock.setError("Requerido"); return; }
        if (frecuenciaStr.isEmpty()) { etFrecuencia.setError("Requerido"); return; }

        float dosis = Float.parseFloat(dosisStr);
        String formato = spFormato.getSelectedItem().toString();
        float stock = Float.parseFloat(stockStr);
        int frecuencia = Integer.parseInt(frecuenciaStr);

        Medicamento med = new Medicamento(0, nombre, dosis, formato, stock, frecuencia);
        long id = controller.registrarMedicamento(med);

        if (id != -1) {
            Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}