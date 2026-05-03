package com.diegomozo.vitalsync;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.diegomozo.vitalsync.Controllers.MedicamentoController;
import com.diegomozo.vitalsync.Models.Medicamento;

public class MainActivity extends AppCompatActivity {

    private MedicamentoController controller;
    private TextView tvProximaToma;
    private Button btnConfirmarToma;
    private ImageView btnMenuOpciones, btnCambiarToma;
    private ListView lvInventario;
    private boolean avisoMostrado = false;
    private List<TomaProgramada> tomasProgramadas;
    private int indiceTomaActual = 0;
    private int tamanoExtra = 0;

    private class TomaProgramada implements Comparable<TomaProgramada> {
        Medicamento medicamento;
        long tiempo;

        TomaProgramada(Medicamento m, long t) {
            this.medicamento = m;
            this.tiempo = t;
        }

        @Override
        public int compareTo(TomaProgramada otra) {
            return Long.compare(this.tiempo, otra.tiempo);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        controller = new MedicamentoController(this);
        tvProximaToma = findViewById(R.id.tvProximaToma);
        btnConfirmarToma = findViewById(R.id.btnConfirmarToma);
        btnMenuOpciones = findViewById(R.id.btnMenuOpciones);
        btnCambiarToma = findViewById(R.id.btnCambiarToma);
        lvInventario = findViewById(R.id.lvInventario);

        cargarUI();

        btnConfirmarToma.setOnClickListener(v -> {
            if (tomasProgramadas != null && !tomasProgramadas.isEmpty()) {
                Medicamento medActual = tomasProgramadas.get(indiceTomaActual).medicamento;
                if (medActual.getStockTotal() >= medActual.getDosis()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
                    builder.setTitle("Añadir Nota Clínica (Opcional)");
                    final EditText input = new EditText(this);
                    input.setTextColor(Color.BLACK);
                    input.setHintTextColor(Color.DKGRAY);
                    builder.setView(input);

                    builder.setPositiveButton("Guardar Nota", (dialog, which) -> {
                        String nota = input.getText().toString();
                        float nuevoStock = medActual.getStockTotal() - medActual.getDosis();
                        controller.actualizarStock(medActual.getIdMedicamento(), nuevoStock);
                        controller.registrarToma(medActual.getIdMedicamento(), "Confirmada", nota);
                        programarSiguienteAlarma(medActual.getIdMedicamento(), medActual.getNombre(), medActual.getFrecuenciaHoras());
                        Toast.makeText(this, "Toma y nota registradas", Toast.LENGTH_SHORT).show();
                        cargarUI();
                    });

                    builder.setNegativeButton("Solo Confirmar", (dialog, which) -> {
                        float nuevoStock = medActual.getStockTotal() - medActual.getDosis();
                        controller.actualizarStock(medActual.getIdMedicamento(), nuevoStock);
                        controller.registrarToma(medActual.getIdMedicamento(), "Confirmada", "");
                        programarSiguienteAlarma(medActual.getIdMedicamento(), medActual.getNombre(), medActual.getFrecuenciaHoras());
                        Toast.makeText(this, "Toma confirmada", Toast.LENGTH_SHORT).show();
                        cargarUI();
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#238C42"));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#238C42"));
                }
            }
        });

        btnMenuOpciones.setOnClickListener(this::mostrarMenu);

        btnCambiarToma.setOnClickListener(v -> {
            if (tomasProgramadas != null && tomasProgramadas.size() > 1) {
                indiceTomaActual = (indiceTomaActual + 1) % tomasProgramadas.size();
                actualizarTarjetaToma();
            } else {
                Toast.makeText(this, "No hay más medicamentos activos", Toast.LENGTH_SHORT).show();
            }
        });

        lvInventario.setOnItemClickListener((parent, view, position, id) -> {
            Medicamento m = (Medicamento) parent.getItemAtPosition(position);
            mostrarDetalleMedicamento(m);
        });
    }

    private void programarSiguienteAlarma(int idMedicamento, String nombre, int frecuencia) {
        SharedPreferences prefs = getSharedPreferences("VitalSyncPrefs", MODE_PRIVATE);

        long tiempoMilis = System.currentTimeMillis() + ((long) frecuencia * 3600000);
        prefs.edit().putLong("prox_toma_" + idMedicamento, tiempoMilis).apply();

        if (!prefs.getBoolean("notificacionesActivas", true)) return;

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
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        avisoMostrado = false;
        cargarUI();
    }

    private void mostrarMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add("Añadir Medicamento");
        popup.getMenu().add("Ver Notas Clínicas");
        popup.getMenu().add("Ajustes");

        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Añadir Medicamento")) {
                startActivity(new Intent(MainActivity.this, SegundoActivity.class));
            } else if (item.getTitle().equals("Ver Notas Clínicas")) {
                startActivity(new Intent(MainActivity.this, NotasActivity.class));
            } else if (item.getTitle().equals("Ajustes")) {
                startActivity(new Intent(MainActivity.this, AjustesActivity.class));
            }
            return true;
        });
        popup.show();
    }

    private void mostrarHistorialNotas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        builder.setTitle("Historial de Notas");

        android.database.sqlite.SQLiteDatabase db = new com.diegomozo.vitalsync.Models.BaseDatosLocal(this).getReadableDatabase();
        android.database.Cursor cursor = db.rawQuery("SELECT m.nombre, t.fechaHoraReal, t.notaClinica FROM Toma t JOIN Medicamento m ON t.idMedicamento = m.idMedicamento WHERE t.notaClinica != '' ORDER BY t.fechaHoraReal DESC", null);
        StringBuilder historial = new StringBuilder();
        if (cursor.moveToFirst()) {
            do {
                historial.append(cursor.getString(1)).append(" - ").append(cursor.getString(0)).append("\nNota: ").append(cursor.getString(2)).append("\n\n");
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        builder.setMessage(historial.length() > 0 ? historial.toString() : "No hay notas clínicas guardadas.");
        builder.setPositiveButton("Cerrar", null);
        builder.show();
    }

    private void mostrarDetalleMedicamento(Medicamento m) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        builder.setTitle(m.getNombre());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 20);

        TextView tvDetalle = new TextView(this);
        tvDetalle.setText("Cantidad restante: " + m.getStockTotal() + " " + m.getFormato() + "\n\n" +
                "Frecuencia: Cada " + m.getFrecuenciaHoras() + " horas\n\n" +
                "Estado: " + (m.getActivo() == 1 ? "Receta Activa" : "Receta Finalizada"));
        tvDetalle.setTextSize(16);
        tvDetalle.setTextColor(Color.BLACK);
        layout.addView(tvDetalle);

        builder.setView(layout);

        builder.setPositiveButton(m.getActivo() == 1 ? "Finalizar receta" : "Restablecer", (dialog, which) -> {
            int nuevoEstado = m.getActivo() == 1 ? 0 : 1;
            controller.cambiarEstadoPrescripcion(m.getIdMedicamento(), nuevoEstado);
            cargarUI();
        });

        builder.setNeutralButton("Modificar", (dialog, which) -> {
            Intent intent = new Intent(MainActivity.this, SegundoActivity.class);
            intent.putExtra("ID_MEDICAMENTO", m.getIdMedicamento());
            startActivity(intent);
        });

        builder.setNegativeButton("Cerrar", null);
        builder.show();
    }

    private void cargarUI() {
        SharedPreferences prefs = getSharedPreferences("VitalSyncPrefs", MODE_PRIVATE);
        tamanoExtra = prefs.getInt("tamanoExtra", 0);
        List<Medicamento> lista = controller.obtenerMedicamentos();

        tomasProgramadas = new ArrayList<>();
        indiceTomaActual = 0;

        for (Medicamento m : lista) {
            if (m.getStockTotal() >= m.getDosis() && m.getActivo() == 1) {
                long prox = prefs.getLong("prox_toma_" + m.getIdMedicamento(), 0);
                if (prox == 0) {
                    prox = System.currentTimeMillis() + ((long) m.getFrecuenciaHoras() * 3600000);
                }
                tomasProgramadas.add(new TomaProgramada(m, prox));
            }
        }

        Collections.sort(tomasProgramadas);
        actualizarTarjetaToma();

        InventarioAdapter adapter = new InventarioAdapter(this, lista);
        lvInventario.setAdapter(adapter);

        if (!avisoMostrado) {
            StringBuilder medBajos = new StringBuilder();
            for (Medicamento m : lista) {
                if (m.getActivo() == 1) {
                    float stockIn = controller.obtenerStockInicial(m.getIdMedicamento(), m.getStockTotal(), m.getDosis());
                    if (stockIn > 0) {
                        float porcentaje = m.getStockTotal() / stockIn;
                        if (porcentaje <= 0.20f) {
                            medBajos.append(m.getNombre()).append(" ");
                        }
                    }
                }
            }
            if (medBajos.length() > 0) {
                Toast.makeText(this, "Aviso stock bajo (<20%): " + medBajos.toString(), Toast.LENGTH_LONG).show();
                avisoMostrado = true;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void actualizarTarjetaToma() {
        if (tomasProgramadas != null && !tomasProgramadas.isEmpty()) {
            TomaProgramada actual = tomasProgramadas.get(indiceTomaActual);
            String horaActualStr = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            String horaTomaStr = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(actual.tiempo));

            String dosisFormateada = formatearDosis(actual.medicamento.getDosis(), actual.medicamento.getFormato());
            tvProximaToma.setText("🕒 " + horaActualStr + " -> 💊 " + actual.medicamento.getNombre() + " (" + dosisFormateada + ")\nProgramada: " + horaTomaStr);

            btnConfirmarToma.setEnabled(true);
            btnConfirmarToma.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#238C42")));
            if (tomasProgramadas.size() > 1) {
                btnCambiarToma.setVisibility(View.VISIBLE);
            } else {
                btnCambiarToma.setVisibility(View.GONE);
            }
        } else {
            tvProximaToma.setText("No hay ninguna toma programada");
            btnConfirmarToma.setEnabled(false);
            btnConfirmarToma.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A0A0A0")));
            btnCambiarToma.setVisibility(View.GONE);
        }
        tvProximaToma.setTextSize(18 + tamanoExtra);
    }
    private String formatearDosis(float dosis, String formato) {
        String cantidad = (dosis == (long) dosis) ? String.format(java.util.Locale.getDefault(), "%d", (long) dosis) : String.format(java.util.Locale.getDefault(), "%s", dosis);
        if (formato.toLowerCase().contains("pastilla")) {
            return dosis == 1f ? cantidad + " pastilla" : cantidad + " pastillas";
        }
        return cantidad + " " + formato;
    }

}