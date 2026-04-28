package com.proyectotcu.muniturrialba.index;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.proyectotcu.muniturrialba.MainActivity;
import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityMenuPrincipalBinding;
import com.proyectotcu.muniturrialba.moduloAdministracionArchivos.AdministracionArchivosActivity;
import com.proyectotcu.muniturrialba.moduloEmpleados.ControlEmpleadosActivity;
import com.proyectotcu.muniturrialba.moduloReporteria.ReporteriaActivity;


public class MenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        Button botonEmpleado = view.findViewById(R.id.btn_ControlEmpleado);
        Button botonReporteria = view.findViewById(R.id.btn_Reporteria);
        Button botonAdministracionArchivos = view.findViewById(R.id.btn_AdministracionArchivos);

        botonEmpleado.setOnClickListener(v -> VistaEmpleados());
        botonReporteria.setOnClickListener(v -> VistaReporteria());
        botonAdministracionArchivos.setOnClickListener(v -> VistaAdministracionArchivos());


        return view;
        //return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    private void VistaEmpleados() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentEmpleado = new Intent(getActivity(), ControlEmpleadosActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentEmpleado);
    }

    private void VistaReporteria() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentReporteria = new Intent(getActivity(), ReporteriaActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentReporteria);
    }

    private void VistaAdministracionArchivos() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentArchivos = new Intent(getActivity(), AdministracionArchivosActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentArchivos);
    }

}