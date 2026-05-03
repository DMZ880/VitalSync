package com.diegomozo.vitalsync;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.diegomozo.vitalsync.Models.BaseDatosLocal;

public class AjustesActivity extends AppCompatActivity {

    private ImageView btnVolverAjustes;
    private SeekBar sbTamanoLetra;
    private Switch swNotificaciones;
    private Button btnBorrarDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        btnVolverAjustes = findViewById(R.id.btnVolverAjustes);
        sbTamanoLetra = findViewById(R.id.sbTamanoLetra);
        swNotificaciones = findViewById(R.id.swNotificaciones);
        btnBorrarDatos = findViewById(R.id.btnBorrarDatos);

        SharedPreferences prefs = getSharedPreferences("VitalSyncPrefs", Context.MODE_PRIVATE);
        int tamanoExtra = prefs.getInt("tamanoExtra", 0);
        boolean notifActivas = prefs.getBoolean("notificacionesActivas", true);

        sbTamanoLetra.setProgress(tamanoExtra);
        swNotificaciones.setChecked(notifActivas);

        sbTamanoLetra.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int extra = seekBar.getProgress();
                SharedPreferences.Editor editor = getSharedPreferences("VitalSyncPrefs", Context.MODE_PRIVATE).edit();
                editor.putInt("tamanoExtra", extra);
                editor.apply();
                Toast.makeText(AjustesActivity.this, "Tamaño de letra actualizado", Toast.LENGTH_SHORT).show();
            }
        });

        swNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences("VitalSyncPrefs", Context.MODE_PRIVATE).edit();
            editor.putBoolean("notificacionesActivas", isChecked);
            editor.apply();
        });

        btnBorrarDatos.setOnClickListener(v -> mostrarDialogoConfirmacion());

        btnVolverAjustes.setOnClickListener(v -> finish());
    }

    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar borrado");
        builder.setMessage("¿Estás seguro de que quieres borrar todos los datos? Esta acción no se puede deshacer.");

        builder.setPositiveButton("Sí, borrar todo", (dialog, which) -> borrarDatos());
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void borrarDatos() {
        BaseDatosLocal dbHelper = new BaseDatosLocal(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM Toma");
        db.execSQL("DELETE FROM Alarma");
        db.execSQL("DELETE FROM Medicamento");
        db.close();

        SharedPreferences.Editor editor = getSharedPreferences("VitalSyncPrefs", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Todos los datos han sido borrados", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}