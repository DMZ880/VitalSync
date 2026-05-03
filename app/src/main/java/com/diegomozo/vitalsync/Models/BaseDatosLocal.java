package com.diegomozo.vitalsync.Models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatosLocal extends SQLiteOpenHelper {

    private static final String NOMBRE_BD = "vitalsync.db";
    private static final int VERSION_BD = 2;

    public BaseDatosLocal(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Medicamento (idMedicamento INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, dosis REAL, formato TEXT, stockTotal REAL, frecuenciaHoras INTEGER NOT NULL, activo INTEGER DEFAULT 1)");
        db.execSQL("CREATE TABLE Toma (idToma INTEGER PRIMARY KEY AUTOINCREMENT, idMedicamento INTEGER, fechaHoraReal DATETIME, estado TEXT, notaClinica TEXT, FOREIGN KEY(idMedicamento) REFERENCES Medicamento(idMedicamento))");
        db.execSQL("CREATE TABLE Alarma (idAlarma INTEGER PRIMARY KEY AUTOINCREMENT, idMedicamento INTEGER, horaProgramada DATETIME, activa INTEGER, FOREIGN KEY(idMedicamento) REFERENCES Medicamento(idMedicamento))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE Medicamento ADD COLUMN activo INTEGER DEFAULT 1");
            } catch (Exception e) {}
        }
    }
}