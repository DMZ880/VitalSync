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
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
            java.util.Date date = sdf.parse(fechaHora);
            java.util.Calendar calRaw = java.util.Calendar.getInstance();
            calRaw.setTime(date);

            java.util.Calendar hoy = java.util.Calendar.getInstance();
            java.util.Calendar ayer = java.util.Calendar.getInstance();
            ayer.add(java.util.Calendar.DAY_OF_YEAR, -1);

            java.text.SimpleDateFormat formatoHora = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

            if (calRaw.get(java.util.Calendar.YEAR) == hoy.get(java.util.Calendar.YEAR) && calRaw.get(java.util.Calendar.DAY_OF_YEAR) == hoy.get(java.util.Calendar.DAY_OF_YEAR)) {
                return "Hoy - " + formatoHora.format(date) + " | 💊 " + nombreMedicamento;
            } else if (calRaw.get(java.util.Calendar.YEAR) == ayer.get(java.util.Calendar.YEAR) && calRaw.get(java.util.Calendar.DAY_OF_YEAR) == ayer.get(java.util.Calendar.DAY_OF_YEAR)) {
                return "Ayer - " + formatoHora.format(date) + " | 💊 " + nombreMedicamento;
            } else {
                java.text.SimpleDateFormat formatoCorto = new java.text.SimpleDateFormat("dd MMM - HH:mm", java.util.Locale.getDefault());
                return formatoCorto.format(date) + " | 💊 " + nombreMedicamento;
            }
        } catch (Exception e) {
            return fechaHora + " | " + nombreMedicamento;
        }
    }
}