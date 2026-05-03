package com.diegomozo.vitalsync;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;
import com.diegomozo.vitalsync.Controllers.MedicamentoController;
import com.diegomozo.vitalsync.Models.Medicamento;

public class InventarioAdapter extends ArrayAdapter<Medicamento> {

    private MedicamentoController controller;
    private int tamanoExtra;

    public InventarioAdapter(Context context, List<Medicamento> medicamentos) {
        super(context, 0, medicamentos);
        controller = new MedicamentoController(context);
        SharedPreferences prefs = context.getSharedPreferences("VitalSyncPrefs", Context.MODE_PRIVATE);
        tamanoExtra = prefs.getInt("tamanoExtra", 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_inventario, parent, false);
        }

        Medicamento med = getItem(position);
        TextView tvNombreInv = convertView.findViewById(R.id.tvNombreInv);
        TextView tvPorcentaje = convertView.findViewById(R.id.tvPorcentaje);
        ProgressBar pbStock = convertView.findViewById(R.id.pbStock);

        tvNombreInv.setTextSize(14 + tamanoExtra);
        tvPorcentaje.setTextSize(14 + tamanoExtra);

        float stockInicial = controller.obtenerStockInicial(med.getIdMedicamento(), med.getStockTotal(), med.getDosis());
        int porcentaje = 0;
        if (stockInicial > 0) {
            porcentaje = (int) ((med.getStockTotal() / stockInicial) * 100);
        }

        tvNombreInv.setText(med.getNombre().toUpperCase() + " " + med.getDosis() + med.getFormato());
        tvPorcentaje.setText("(" + porcentaje + "%)");

        pbStock.setMax(100);
        pbStock.setProgress(porcentaje);

        if (porcentaje <= 20) {
            pbStock.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#BD1414")));
        } else {
            pbStock.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#238C42")));
        }

        return convertView;
    }
}