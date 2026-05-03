package com.diegomozo.vitalsync.Models;

public class Nota {
    private int idMedicamento;
    private String nombreMedicamento;
    private String fechaHora;
    private String textoNota;

    public Nota(int idMedicamento, String nombreMedicamento, String fechaHora, String textoNota) {
        this.idMedicamento = idMedicamento;
        this.nombreMedicamento = nombreMedicamento;
        this.fechaHora = fechaHora;
        this.textoNota = textoNota;
    }

    public int getIdMedicamento() { return idMedicamento; }
    public String getNombreMedicamento() { return nombreMedicamento; }
    public String getFechaHora() { return fechaHora; }
    public String getTextoNota() { return textoNota; }

    @Override
    public String toString() {
        return fechaHora;
    }
}