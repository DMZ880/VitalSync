package com.diegomozo.vitalsync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
    private int idModificar = -1;

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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, formatos);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spFormato.setAdapter(adapter);

        if (getIntent().hasExtra("ID_MEDICAMENTO")) {
            idModificar = getIntent().getIntExtra("ID_MEDICAMENTO", -1);
            Medicamento m = controller.obtenerMedicamentoPorId(idModificar);
            if (m != null) {
                etNombre.setText(m.getNombre());
                etDosis.setText(String.valueOf(m.getDosis()));
                etStock.setText(String.valueOf(m.getStockTotal()));
                etFrecuencia.setText(String.valueOf(m.getFrecuenciaHoras()));
                for (int i = 0; i < formatos.length; i++) {
                    if (formatos[i].equals(m.getFormato())) {
                        spFormato.setSelection(i);
                        break;
                    }
                }
            }
        }

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

        Medicamento med = new Medicamento(idModificar != -1 ? idModificar : 0, nombre, dosis, formato, stock, frecuencia, 1);

        if (idModificar != -1) {
            controller.actualizarMedicamento(med);
            Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            long id = controller.registrarMedicamento(med);
            if (id != -1) {
                programarAlarma(id, nombre, frecuencia);
                Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void programarAlarma(long id, String nombre, int frecuencia) {
        long tiempoMilis = System.currentTimeMillis() + ((long) frecuencia * 60000);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(tiempoMilis);
        int hora = cal.get(java.util.Calendar.HOUR_OF_DAY);
        int minuto = cal.get(java.util.Calendar.MINUTE);

        Intent intent = new Intent(android.provider.AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(android.provider.AlarmClock.EXTRA_MESSAGE, "VitalSync: " + nombre);
        intent.putExtra(android.provider.AlarmClock.EXTRA_HOUR, hora);
        intent.putExtra(android.provider.AlarmClock.EXTRA_MINUTES, minuto);
        intent.putExtra(android.provider.AlarmClock.EXTRA_SKIP_UI, true);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No se encontró app de reloj nativa", Toast.LENGTH_SHORT).show();
        }
    }
}