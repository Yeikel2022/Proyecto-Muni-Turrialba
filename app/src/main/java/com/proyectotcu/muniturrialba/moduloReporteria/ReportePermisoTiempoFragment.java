package com.proyectotcu.muniturrialba.moduloReporteria;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proyectotcu.muniturrialba.R;


public class ReportePermisoTiempoFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_reporte_permisos_tiempo, container, false);
    }
}