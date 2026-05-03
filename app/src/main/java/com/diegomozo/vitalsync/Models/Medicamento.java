package com.diegomozo.vitalsync.Models;

public class Medicamento {
    private int idMedicamento;
    private String nombre;
    private float dosis;
    private String formato;
    private float stockTotal;
    private int frecuenciaHoras;
    private int activo;

    public Medicamento(int idMedicamento, String nombre, float dosis, String formato, float stockTotal, int frecuenciaHoras, int activo) {
        this.idMedicamento = idMedicamento;
        this.nombre = nombre;
        this.dosis = dosis;
        this.formato = formato;
        this.stockTotal = stockTotal;
        this.frecuenciaHoras = frecuenciaHoras;
        this.activo = activo;
    }

    public int getIdMedicamento() { return idMedicamento; }
    public String getNombre() { return nombre; }
    public float getDosis() { return dosis; }
    public String getFormato() { return formato; }
    public float getStockTotal() { return stockTotal; }
    public int getFrecuenciaHoras() { return frecuenciaHoras; }
    public int getActivo() { return activo; }
}