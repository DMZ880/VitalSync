package com.diegomozo.vitalsync.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.diegomozo.vitalsync.Models.BaseDatosLocal;
import com.diegomozo.vitalsync.Models.Medicamento;

public class MedicamentoController {

    private BaseDatosLocal dbHelper;

    public MedicamentoController(Context context) {
        dbHelper = new BaseDatosLocal(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.execSQL("ALTER TABLE Medicamento ADD COLUMN activo INTEGER DEFAULT 1");
        } catch (Exception e) {}
        db.close();
    }

    public long registrarMedicamento(Medicamento medicamento) {
        if (medicamento.getNombre() == null || medicamento.getNombre().trim().isEmpty()) return -1;
        if (medicamento.getFrecuenciaHoras() <= 0) return -1;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", medicamento.getNombre());
        values.put("dosis", medicamento.getDosis());
        values.put("formato", medicamento.getFormato());
        values.put("stockTotal", medicamento.getStockTotal());
        values.put("frecuenciaHoras", medicamento.getFrecuenciaHoras());
        values.put("activo", medicamento.getActivo());

        long id = db.insert("Medicamento", null, values);
        db.close();
        return id;
    }

    public List<Medicamento> obtenerMedicamentos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Medicamento> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Medicamento", null);

        if (cursor.moveToFirst()) {
            do {
                Medicamento med = new Medicamento(
                        cursor.getInt(cursor.getColumnIndexOrThrow("idMedicamento")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("dosis")),
                        cursor.getString(cursor.getColumnIndexOrThrow("formato")),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("stockTotal")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("frecuenciaHoras")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("activo"))
                );
                lista.add(med);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public Medicamento obtenerMedicamentoPorId(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Medicamento WHERE idMedicamento=?", new String[]{String.valueOf(id)});
        Medicamento med = null;
        if (cursor.moveToFirst()) {
            med = new Medicamento(
                    cursor.getInt(cursor.getColumnIndexOrThrow("idMedicamento")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("dosis")),
                    cursor.getString(cursor.getColumnIndexOrThrow("formato")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("stockTotal")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("frecuenciaHoras")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("activo"))
            );
        }
        cursor.close();
        db.close();
        return med;
    }

    public void actualizarMedicamento(Medicamento m) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", m.getNombre());
        values.put("dosis", m.getDosis());
        values.put("formato", m.getFormato());
        values.put("stockTotal", m.getStockTotal());
        values.put("frecuenciaHoras", m.getFrecuenciaHoras());
        values.put("activo", m.getActivo());
        db.update("Medicamento", values, "idMedicamento=?", new String[]{String.valueOf(m.getIdMedicamento())});
        db.close();
    }

    public void actualizarStock(int id, float nuevoStock) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("stockTotal", nuevoStock);
        db.update("Medicamento", values, "idMedicamento=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void cambiarEstadoPrescripcion(int id, int activo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("activo", activo);
        db.update("Medicamento", values, "idMedicamento=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void registrarToma(int idMedicamento, String estado, String nota) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idMedicamento", idMedicamento);
        values.put("fechaHoraReal", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        values.put("estado", estado);
        values.put("notaClinica", nota);
        db.insert("Toma", null, values);
        db.close();
    }

    public float obtenerStockInicial(int idMedicamento, float stockActual, float dosis) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Toma WHERE idMedicamento=? AND estado='Confirmada'", new String[]{String.valueOf(idMedicamento)});
        int tomas = 0;
        if (cursor.moveToFirst()) tomas = cursor.getInt(0);
        cursor.close();
        db.close();
        return stockActual + (dosis * tomas);
    }

    public long obtenerTiempoSiguienteToma(int idMedicamento, int frecuenciaHoras) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT fechaHoraReal FROM Toma WHERE idMedicamento=? AND estado='Confirmada' ORDER BY idToma DESC LIMIT 1", new String[]{String.valueOf(idMedicamento)});
        long tiempo = 0;
        if (cursor.moveToFirst()) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(cursor.getString(0));
                tiempo = date.getTime() + ((long) frecuenciaHoras * 60000);
            } catch (Exception e) {}
        }
        cursor.close();
        db.close();
        return tiempo;
    }
    public String obtenerHistorialNotas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT m.nombre, t.fechaHoraReal, t.notaClinica FROM Toma t JOIN Medicamento m ON t.idMedicamento = m.idMedicamento WHERE t.notaClinica != '' ORDER BY t.fechaHoraReal DESC", null);
        StringBuilder historial = new StringBuilder();
        if (cursor.moveToFirst()) {
            do {
                historial.append(cursor.getString(1)).append(" - ").append(cursor.getString(0)).append("\nNota: ").append(cursor.getString(2)).append("\n\n");
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return historial.length() > 0 ? historial.toString() : "No hay notas clínicas guardadas.";
    }
}