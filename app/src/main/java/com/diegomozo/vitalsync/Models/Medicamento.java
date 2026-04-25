package com.diegomozo.vitalsync.Models;

public class Medicamento {

    private int idMedicamento;
    private String nombre;
    private float dosis;
    private String formato;
    private float stockTotal;
    private int frecuenciaHoras;

    public Medicamento() {}

    public Medicamento(int idMedicamento, String nombre, float dosis, String formato, float stockTotal, int frecuenciaHoras) {
        this.idMedicamento = idMedicamento;
        this.nombre = nombre;
        this.dosis = dosis;
        this.formato = formato;
        this.stockTotal = stockTotal;
        this.frecuenciaHoras = frecuenciaHoras;
    }

    public int getIdMedicamento() { return idMedicamento; }
    public void setIdMedicamento(int idMedicamento) { this.idMedicamento = idMedicamento; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public float getDosis() { return dosis; }
    public void setDosis(float dosis) { this.dosis = dosis; }
    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }
    public float getStockTotal() { return stockTotal; }
    public void setStockTotal(float stockTotal) { this.stockTotal = stockTotal; }
    public int getFrecuenciaHoras() { return frecuenciaHoras; }
    public void setFrecuenciaHoras(int frecuenciaHoras) { this.frecuenciaHoras = frecuenciaHoras; }
}