package com.diegomozo.vitalsync.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import com.diegomozo.vitalsync.Models.BaseDatosLocal;
import com.diegomozo.vitalsync.Models.Medicamento;

public class MedicamentoController {

    private BaseDatosLocal dbHelper;

    public MedicamentoController(Context context) {
        dbHelper = new BaseDatosLocal(context);
    }

    public long registrarMedicamento(Medicamento medicamento) {
        if (medicamento.getNombre() == null || medicamento.getNombre().trim().isEmpty()) {
            return -1;
        }
        if (medicamento.getFrecuenciaHoras() <= 0) {
            return -1;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", medicamento.getNombre());
        values.put("dosis", medicamento.getDosis());
        values.put("formato", medicamento.getFormato());
        values.put("stockTotal", medicamento.getStockTotal());
        values.put("frecuenciaHoras", medicamento.getFrecuenciaHoras());

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
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getFloat(2),
                        cursor.getString(3),
                        cursor.getFloat(4),
                        cursor.getInt(5)
                );
                lista.add(med);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }
    public void actualizarStock(int id, float nuevoStock) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("stockTotal", nuevoStock);
        db.update("Medicamento", values, "idMedicamento=?", new String[]{String.valueOf(id)});
        db.close();
    }
}